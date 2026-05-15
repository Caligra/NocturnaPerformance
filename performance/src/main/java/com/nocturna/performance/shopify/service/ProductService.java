package com.nocturna.performance.shopify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.config.ShopifyConfig;
import com.nocturna.performance.shopify.dto.ShopifyProduct;
import com.nocturna.performance.shopify.dto.repository.ShopifyProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductService {

    private final RestTemplate restTemplate;
    private final ShopifyConfig shopifyConfig;
    private final ShopifyProductRepository shopifyProductRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    public ProductService(RestTemplate restTemplate, ShopifyConfig shopifyConfig, ShopifyProductRepository shopifyProductRepository){
        this.restTemplate=restTemplate;
        this.shopifyConfig=shopifyConfig;
        this.shopifyProductRepository=shopifyProductRepository;
    }

    public void shopifyCreateProducts() {
        /*List<ShopifyProduct> allExport = shopifyProductRepository.findAll();
        for (ShopifyProduct prod : allExport) {*/
        ShopifyProduct prod = new ShopifyProduct();
            logger.info(prod.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Shopify-Access-Token", shopifyConfig.getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            //ShopifyProductWrapper spw = new ShopifyProductWrapper(prod);
            String jsonBody = convertObjectToJsonString(prod);
        String testJson = "{"
                + "\"product\":{"
                + "\"title\":\"High-Flow Fuel 2\","
                + "\"body_html\":\"<p>Durable construction, supports up to 750 HP engines. Designed for reliability in both street and track environments.</p>\","
                + "\"vendor\":\"Holley Performance\","
                + "\"product_type\":\"Fuel Systems\","
                + "\"tags\":\"Holley, Fuel Systems, High-Flow\","
                + "\"variants\":[{"
                + "\"price\":\"0.00\","
                + "\"sku\":\"\","
                + "\"barcode\":\"123456789012\""
                + "}]"
                + "}"
                + "}";
            logger.info("testJson ::" + testJson);
            logger.info("jsonbody ::" + jsonBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(testJson, headers);
            String answer = restTemplate.postForObject(shopifyConfig.getCreateProducts(), requestEntity, String.class);
            System.out.println(answer);
        //}
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
