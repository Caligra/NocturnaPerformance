package com.nocturna.performance.shopify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.config.ShopifyProperties;
import com.nocturna.performance.shopify.dto.ShopifyProduct;
import com.nocturna.performance.shopify.dto.repository.ShopifyProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ShopifyProperties shopifyProperties;
    @Autowired
    private ShopifyProductRepository shopifyProductRepository;

    public void shopifyCreateProducts() {
        List<ShopifyProduct> allExport = shopifyProductRepository.findAll();
        for (ShopifyProduct prod : allExport) {
            logger.info(prod.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Shopify-Access-Token", shopifyProperties.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            //ShopifyProductWrapper spw = new ShopifyProductWrapper(prod);
            String jsonBody = convertObjectToJsonString(prod);
            logger.info("jsonbody ::" + jsonBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
            String answer = restTemplate.postForObject(shopifyProperties.getProducts(), requestEntity, String.class);
            System.out.println(answer);
        }
        /*ResponseEntity<String> response = restTemplate.exchange(shopifyProperties.getProducts(),
                HttpMethod.GET, requestEntity);
        ObjectMapper objectMapper = new ObjectMapper();
        Products responseList = objectMapper.readValue(response.getBody(), Products.class);
        logger.info("ProductService::responseList.getProducts().size():: " + responseList.getProducts().size());*/
    /*  try {
            if (!responseList.getProducts().isEmpty()) {
                productEngRepository.saveAll(responseList.getProducts());
            }
        } catch (DataIntegrityViolationException ex) {
            ex.printStackTrace();
        }
    }*/
    }

    public String convertObjectToJsonString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(object);
            return jsonString;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
