package com.nocturna.performance.shopify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.apicredentials.service.TokenService;
import com.nocturna.performance.config.properties.ShopifyProperties;
import com.nocturna.performance.shopify.dto.ShopifyProduct;
import com.nocturna.performance.shopify.dto.repository.ShopifyProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {

    private final RestTemplate restTemplate;
    private final ShopifyProperties shopifyProperties;
    private final TokenService tokenService;
    private final ShopifyProductRepository shopifyProductRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    public ProductService(RestTemplate restTemplate, ShopifyProperties shopifyProperties, ShopifyProductRepository shopifyProductRepository, TokenService tokenService){
        this.restTemplate=restTemplate;
        this.shopifyProperties=shopifyProperties;
        this.shopifyProductRepository=shopifyProductRepository;
        this.tokenService=tokenService;
    }

    public void shopifyCreateProducts() {
        /*List<ShopifyProduct> allExport = shopifyProductRepository.findAll();
        for (ShopifyProduct prod : allExport) {*/
        ShopifyProduct prod = new ShopifyProduct();
            logger.info(prod.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Shopify-Access-Token", tokenService.getValidToken());
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
            String answer = restTemplate.postForObject(shopifyProperties.getCreateProducts(), requestEntity, String.class);
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
