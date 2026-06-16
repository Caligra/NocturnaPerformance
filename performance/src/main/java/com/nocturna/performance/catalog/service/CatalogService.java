package com.nocturna.performance.catalog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.catalog.dto.HolleyImage;
import com.nocturna.performance.catalog.dto.HolleyProduct;
import com.nocturna.performance.catalog.dto.repository.HolleyImagesRepository;
import com.nocturna.performance.catalog.dto.repository.HolleyProductRepository;
import com.nocturna.performance.config.properties.HolleyProperties;
import com.nocturna.performance.config.properties.SchedulerProperties;
import com.nocturna.performance.catalog.dto.wrappers.HolleyProducts;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.time.Instant;
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
        // todo check response if not auth flag false brand in DB
        logger.info("CatalogService::START Brand:: " + brandCode );
        //Build our headers for the call
        HttpEntity<Void> requestEntity = new HttpEntity<>(getHeaders());
        logger.info("CatalogService::getHeaders Brand:: " + brandCode );

        //Build and execute the GET call to download brand catalog
        try{
        ResponseEntity<String> response = restTemplate.exchange(holleyProperties.getUrl(),
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                }, template, brandCode);
        logger.info("CatalogService:: ResponseEntity:: " + brandCode + " response.getBody():: " + response.getBody());
        //Wrapper to parse json into Holley DTO
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("CatalogService:: Brand:: " + brandCode + " response.getBody():: " + response.getBody());
        HolleyProducts holleyProducts = objectMapper.readValue(response.getBody(), HolleyProducts.class);
        //Extract object data from wrapper
        List<HolleyProduct> holleyProductList = holleyProducts.getHolleyProducts();
        // Check for empty or duplicated UPC, generate data hash for upsert logic
        List<HolleyProduct> insertList = checkUPCDataHash(holleyProductList);

        //try {
            //Step 1: Store Holley data as is in DB
            if (!insertList.isEmpty()) {
                holleyProductRepository.saveAll(insertList);
            }
//        } catch (DataIntegrityViolationException | HibernateException ex) {
//            ex.printStackTrace();
//        }

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
        //try {
            if (!allImgToInsert.isEmpty()) {
                logger.info("CatalogService:: Brand:: " + brandCode + " holleyImagesRepository.saveAll:: !allImgToInsert.isEmpty()");
                holleyImagesRepository.saveAll(allImgToInsert);
            }
        } catch (Exception ex) {
        //} catch (DataIntegrityViolationException | HibernateException ex ) {
            ex.printStackTrace();
        }
    }

    //Duplicate check by looping through UPC values, unique values are added to a set to be streamed into a list
    private List<HolleyProduct> checkUPCDataHash(List<HolleyProduct> holleyRawList) {
        logger.info("CatalogService::checkUPCDataHash:: start :: holleyRawList.size() " + holleyRawList.size());
        // Set to filter unique UPC - Arraylist to return filtered products
        var uniqueSetUPC = new HashSet<String>();
        var uniqueProdListRO = new ArrayList<HolleyProduct>();
        for (HolleyProduct product : holleyRawList) {
            // There are UPC null values in Holley - Check to skip null values
            String upc = product.getUpc();
            if (upc != null && !upc.isEmpty()) {
                // Generate dataHash to determine upsert
                String shortDesc = product.getShortDescription() == null ? "" : product.getShortDescription();
                String invoiceDesc = product.getInvoiceDescription() == null ? "" : product.getInvoiceDescription();
                String newHash = DigestUtils.sha256Hex(product.getUpc() + "|" + shortDesc + "|" + invoiceDesc);
                // Check if the product exists in DB
                // If the product is a new one, grab data from holley, create datahash, add to RO
                var existingProduct = holleyProductRepository.findById(upc).orElse(null);
                if (existingProduct == null) {
                    product.setDatahash(newHash);
                    // Set .add() method returns false if the value wasn't added
                    // Log duplicate values for debugging
                    // Add unique upc values to return object
                    if (!uniqueSetUPC.add(upc)) {
                        logger.info("CatalogService::duplicateUPCCheck()::Removed upc " + upc);
                    } else {
                        uniqueProdListRO.add(product);
                    }
                } else if (!Objects.equals(existingProduct.getDatahash(), newHash)) {
                    // Update only if changed
                    product.setDatahash(newHash);
                    uniqueProdListRO.add(product);
                }
            }
        }
        //Stream unique to return list
        logger.info("CatalogService::checkUPCDataHash:: finish :: uniqueProdListRO.size()" + uniqueProdListRO.size());
        return uniqueProdListRO;
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
                if (!urlLink.contains("pdf") && !urlLink.contains("youtube")) {
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
