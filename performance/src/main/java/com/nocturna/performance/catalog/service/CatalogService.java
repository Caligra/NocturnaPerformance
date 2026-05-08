package com.nocturna.performance.catalog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.catalog.dto.HolleyImage;
import com.nocturna.performance.catalog.dto.HolleyProduct;
import com.nocturna.performance.catalog.dto.repository.HolleyImagesRepository;
import com.nocturna.performance.catalog.dto.repository.HolleyProductRepository;
import com.nocturna.performance.config.HolleyProperties;
import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.catalog.dto.wrappers.HolleyProducts;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

@Service
public class CatalogService {
    private final RestTemplate restTemplate;
    private final HolleyProductRepository holleyProductRepository;
    private final HolleyImagesRepository holleyImagesRepository;
    private final HolleyProperties holleyProperties;
    private final SchedulerProperties schedulerProperties;
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    public CatalogService(RestTemplate restTemplate, HolleyProductRepository holleyProductRepository, HolleyImagesRepository holleyImagesRepository, HolleyProperties holleyProperties, SchedulerProperties schedulerProperties) {
        this.restTemplate = restTemplate;
        this.holleyProductRepository = holleyProductRepository;
        this.holleyImagesRepository = holleyImagesRepository;
        this.holleyProperties = holleyProperties;
        this.schedulerProperties = schedulerProperties;
    }

    public void getBrandCatalog(String code) throws IOException {
        /*
         * Method to fetch catalog
         * */
        logger.info("Starting getBrandCatalog for brand:: " + code);
        fetchCatalogDataByBrand(holleyProperties.getTemplate(), code);
        logger.info("Finishing getBrandCatalog for brand:: " + code);
    }

    public void fetchCatalogDataByBrand(String template, String brandCode) throws IOException {

        //Build our headers for the call
        HttpEntity<Void> requestEntity = new HttpEntity<>(getHeaders());
        //Build and execute the GET call to download brand catalog
        ResponseEntity<String> response = restTemplate.exchange(holleyProperties.getUrl(),
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                }, template, brandCode);
        //Wrapper to parse json into Holley DTO
        ObjectMapper objectMapper = new ObjectMapper();
        HolleyProducts holleyProducts = objectMapper.readValue(response.getBody(), HolleyProducts.class);
        //Extract object data from wrapper
        List<HolleyProduct> holleyProductList = holleyProducts.getHolleyProducts();
        //Duplicate UPC check
        List<HolleyProduct> insertList = duplicateUPCCheck(holleyProductList);

        try {
            //Step 1: Store Holley data as is in DB
            if (!insertList.isEmpty()) {
                holleyProductRepository.saveAll(insertList);
            }
        } catch (DataIntegrityViolationException | HibernateException ex) {
            ex.printStackTrace();
        }

        /**
         * Processing URL Media before inserting
         * Splitting URL links to exclude YouTube links and pdf files
         */
        var allImgToInsert = new ArrayList<HolleyImage>();
        for (HolleyProduct product : insertList) {
            List<HolleyImage> urlsByProduct = generateImagesByProduct(product);
            if (!urlsByProduct.isEmpty()) {
                allImgToInsert.addAll(urlsByProduct);
            }
        }
        logger.info("CatalogService:: Brand:: " + brandCode + " allImgToInsert.getProducts().size():: " + allImgToInsert.size());
        try {
            if (!allImgToInsert.isEmpty()) {
                logger.info("CatalogService:: Brand:: " + brandCode + " holleyImagesRepository.saveAll:: !allImgToInsert.isEmpty()");
                holleyImagesRepository.saveAll(allImgToInsert);
            }
        } catch (DataIntegrityViolationException | HibernateException ex) {
            ex.printStackTrace();
        }
    }

    //Duplicate check by looping through UPC values, unique values are added to a set to be streamed into a list
    private List<HolleyProduct> duplicateUPCCheck(List<HolleyProduct> inputList) {
        logger.info("CatalogService::duplicateUPCCheck()::inputList size " + inputList.size());
        Set<String> original = new HashSet<>();
        Set<HolleyProduct> unique = new HashSet<>();
        for (HolleyProduct product : inputList) {
            String upc = product.getUpc();
            if (upc != null && !upc.isEmpty()) {
                //add returns false if it wasn't added, log duplicate values, else add to unique
                if (!original.add(upc)) {
                    logger.info("CatalogService::duplicateUPCCheck()::Removed upc " + upc);
                } else {
                    unique.add(product);
                }
            }
        }
        //Stream unique to return list
        logger.info("CatalogService::duplicateUPCCheck()::unique size " + unique.size());
        return unique.stream().toList();
    }

    private List<HolleyImage> generateImagesByProduct(HolleyProduct product) {
        //Object to return generated Image Objects
        var retObj = new ArrayList<HolleyImage>();
        //read upc and mediaURL from product
        var upc = product.getUpc();
        var mediaURLString = product.getMediaUrl();
        //filtering nulls
        if (upc != null && mediaURLString != null && !mediaURLString.isEmpty()) {
            var mediaArray = mediaURLString.split("\\+");
            for (String urlLink : mediaArray) {
                /*if (isImageUrlValid(urlLink)) {
                    retObj.add(new HolleyImage(urlLink, product));
                }*/
                if(!urlLink.contains("pdf") && !urlLink.contains("youtube") ) {
                    retObj.add(new HolleyImage(urlLink, product));
                }
            }
        }
        return retObj;
    }

    public static boolean isImageUrlValid(String urlString) {
        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // fast check, no body download
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            String contentType = connection.getContentType();

            return responseCode == HttpURLConnection.HTTP_OK &&
                    contentType != null &&
                    contentType.startsWith("image");
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("API-Token", holleyProperties.getToken());
        return headers;
    }


}
