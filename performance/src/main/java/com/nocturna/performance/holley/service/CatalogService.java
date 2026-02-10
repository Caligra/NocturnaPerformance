package com.nocturna.performance.holley.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.config.NocturnaProperties;
import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.holley.dto.HolleyProduct;
import com.nocturna.performance.holley.dto.HolleyProducts;
import com.nocturna.performance.holley.dto.repository.HolleyRepository;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CatalogService {
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HolleyRepository holleyRepository;
    @Autowired
    private NocturnaProperties nocturnaProperties;
    @Autowired
    private SchedulerProperties schedulerProperties;

    public void getBrandCatalog(String code) throws IOException {
        /*
         * Method to fetch catalog
         * */
        logger.info("Starting getBrandCatalog for brand:: " + code);
        fetchCatalogDataByBrand(schedulerProperties.getTemplate(), code);
        logger.info("Finishing getBrandCatalog for brand:: " + code);
    }

    public void fetchCatalogDataByBrand(String template, String brandCode) throws IOException {

        //Build our headers for the call
        HttpHeaders headers = new HttpHeaders();
        headers.set("API-Token", nocturnaProperties.getToken());
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        //Build and execute the GET call to download brand catalog
        ResponseEntity<String> response = restTemplate.exchange(nocturnaProperties.getUrl(),
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                }, template, brandCode);
        //Wrapper to parse json into Holley DTO
        ObjectMapper objectMapper = new ObjectMapper();
        HolleyProducts holleyProducts = objectMapper.readValue(response.getBody(), HolleyProducts.class);
        //Extract object data from wrapper
        List<HolleyProduct> holleyProductList = holleyProducts.getHolleyProducts();
        logger.info("responseList.getProducts().size():: " + holleyProductList.size());
        //Duplicate UPC check
        List<HolleyProduct> insertList = duplicateUPCCheck(holleyProductList);
        logger.info("responseList.insertList().size():: " + insertList.size());

        try {
            //Step 1: Store Holley data as is in DB
            if (!insertList.isEmpty()) {
                logger.info("holleyRepository.saveAll:: !insertList.isEmpty()");
                holleyRepository.saveAll(insertList);
            }
        } catch (DataIntegrityViolationException | HibernateException ex) {
            ex.printStackTrace();
        }
    }

    //Duplicate check by looping through UPC values, unique values are added to a set to be streamed into a list
    private List<HolleyProduct> duplicateUPCCheck(List<HolleyProduct> inputList){
        Set<String> original = new HashSet<>();
        Set<HolleyProduct> unique = new HashSet<>();
        for(HolleyProduct product: inputList){
            String upc = product.getUpc();
            //add returns false if it wasn't added, log duplicate values, else add to unique
            if(!original.add(upc)){
                logger.info("duplicateUPCCheck()::Removed upc " + upc );
            }else{
                unique.add(product);
            }
        }
        //Stream unique to return list
        return unique.stream().toList();
    }

}
