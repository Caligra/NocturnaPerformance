package com.nocturna.performance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.config.NocturnaProperties;
import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.dto.catalog.HolleyProducts;
import com.nocturna.performance.dto.catalog.repository.ProductEngRepository;
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

@Service
public class CatalogService {
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ProductEngRepository productEngRepository;
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

        HttpHeaders headers = new HttpHeaders();
        headers.set("API-Token", nocturnaProperties.getToken());
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(nocturnaProperties.getUrl(),
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                }, template, brandCode);
        ObjectMapper objectMapper = new ObjectMapper();
        HolleyProducts holleyProducts = objectMapper.readValue(response.getBody(), HolleyProducts.class);
        logger.info("responseList.getProducts().size():: " + holleyProducts.getProducts().size());

        try {
            //Step 1: Store Holley data as is in DB
            if (!holleyProducts.getProducts().isEmpty()) {
                productEngRepository.saveAll(holleyProducts.getProducts());
            }
        } catch (DataIntegrityViolationException ex) {
            ex.printStackTrace();
        }
    }
}
