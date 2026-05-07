package com.nocturna.performance.metafields;

import com.nocturna.performance.catalog.service.CatalogService;
import com.nocturna.performance.config.HolleyProperties;
//import com.nocturna.performance.config.ShopifyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopifyMetafieldService {
    private final RestTemplate restTemplate;
    private final HolleyProperties holleyProperties;
    //private final ShopifyConfig shopifyConfig;
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);


    public ShopifyMetafieldService(RestTemplate restTemplate, HolleyProperties holleyProperties/*, ShopifyConfig shopifyConfig*/) {
        this.restTemplate = restTemplate;
        this.holleyProperties = holleyProperties;
        /*this.shopifyConfig=shopifyConfig;*/
    }
    // ── Core GraphQL executor ─────────────────────────────────────
    private Map<String, Object> executeGraphQL(String query, Map<String, Object> variables) {
        /*String url = shopifyConfig.getBaseUrl().concat("/graphql.json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        requestBody.put("variables", variables);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, getHeaders());

        try {
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity,new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("GraphQL request failed: {}", e.getResponseBodyAsString());
            return null;
        }*/
        return new HashMap<>();
    }
/*
    // ── Create a single metafield definition ─────────────────────
    private void createMetafieldDefinition(MetafieldDefinitionInput input) {
        String mutation = """
            mutation CreateMetafieldDefinition($definition: MetafieldDefinitionInput!) {
                metafieldDefinitionCreate(definition: $definition) {
                    createdDefinition {
                        id
                        name
                        key
                        namespace
                        type {
                            name
                        }
                    }
                    userErrors {
                        field
                        message
                        code
                    }
                }
            }
        """;

        Map<String, Object> definition = new HashMap<>();
        definition.put("name",        input.getName());
        definition.put("namespace",   input.getNamespace());
        definition.put("key",         input.getKey());
        definition.put("description", input.getDescription());
        definition.put("type",        input.getType());
        definition.put("ownerType",   input.getOwnerType());

        Map<String, Object> variables = Map.of("definition", definition);

        Map<String, Object> response = executeGraphQL(mutation, variables);

        if (response != null) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            Map<String, Object> result = (Map<String, Object>) data.get("metafieldDefinitionCreate");
            List<Map<String, Object>> errors = (List<Map<String, Object>>) result.get("userErrors");

            if (errors != null && !errors.isEmpty()) {
                errors.forEach(e -> {
                    String code = (String) e.get("code");
                    // ALREADY_EXISTS is safe to ignore on re-runs
                    if (!"ALREADY_EXISTS".equals(code)) {
                        log.warn("⚠️ Metafield definition error [{}]: {} - {}",
                                input.getKey(), code, e.get("message"));
                    } else {
                        log.info("ℹ️ Definition already exists, skipping: {}.{}",
                                input.getNamespace(), input.getKey());
                    }
                });
            } else {
                log.info("✅ Created metafield definition: {}.{}",
                        input.getNamespace(), input.getKey());
            }
        }
    }

    // ── Setup all vehicle compatibility definitions ───────────────
    public void setupDefinitions() {
        log.info("🔧 Setting up metafield definitions...");

        List<MetafieldDefinitionInput> definitions = List.of(

                MetafieldDefinitionInput.builder()
                        .name("Vehicle Make")
                        .namespace("custom")
                        .key("vehicle_make")
                        .description("List of compatible vehicle makes (e.g. Pontiac, Ford)")
                        .type("list.single_line_text_field")
                        .ownerType("PRODUCT")
                        .build(),

                MetafieldDefinitionInput.builder()
                        .name("Vehicle Model")
                        .namespace("custom")
                        .key("vehicle_model")
                        .description("List of compatible vehicle models (e.g. Tempest, Mustang)")
                        .type("list.single_line_text_field")
                        .ownerType("PRODUCT")
                        .build(),

                MetafieldDefinitionInput.builder()
                        .name("Vehicle Year Min")
                        .namespace("custom")
                        .key("vehicle_year_min")
                        .description("Earliest compatible model year")
                        .type("number_integer")
                        .ownerType("PRODUCT")
                        .build(),

                MetafieldDefinitionInput.builder()
                        .name("Vehicle Year Max")
                        .namespace("custom")
                        .key("vehicle_year_max")
                        .description("Latest compatible model year")
                        .type("number_integer")
                        .ownerType("PRODUCT")
                        .build()
        );

        for (MetafieldDefinitionInput def : definitions) {
            createMetafieldDefinition(def);
            try { Thread.sleep(300); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("✅ Metafield definitions setup complete.");
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", holleyProperties.getToken());
        return headers;
    }*/

}
