package com.nocturna.performance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    public String getBrandCatalog(String input){
        /*
         * Method to fetch catalog
         * */
        logger.info("CatalogService");
        return input + " yayy";
    }
}
