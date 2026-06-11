package com.nocturna.performance.metafields;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.startup.NocturnaInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetafieldDefinitionService {
    private static final Logger logger = LoggerFactory.getLogger(NocturnaInitializer.class);

    private final WebClient shopifyWebClient;

    // Full list of definitions to bootstrap
    private static final List<MetafieldDefinitionRequest> DEFINITIONS = List.of(
            build("vehicle_make", "Vehicle Make", "list.single_line_text_field", "Compatible vehicle makes"),
            build("vehicle_model", "Vehicle Model", "list.single_line_text_field", "Compatible vehicle models"),
            build("year_range", "Year Range", "single_line_text_field", "Year range string e.g. 1969-1972"),
            build("vehicle_years", "Vehicle Years", "list.number_integer", "Expanded individual compatible years"),
            build("fitment_notes", "Fitment Notes", "multi_line_text_field", "Full fitment and compatibility notes"),
            build("short_description", "Short Description", "single_line_text_field", "Brief product summary"),
            build("merch_length", "Merch Length (cm)", "number_decimal", "Product length unpackaged"),
            build("merch_width", "Merch Width (cm)", "number_decimal", "Product width unpackaged"),
            build("merch_height", "Merch Height (cm)", "number_decimal", "Product height unpackaged"),
            build("merch_weight", "Merch Weight (kg)", "number_decimal", "Product weight unpackaged")
    );

    private static MetafieldDefinitionRequest build(String key, String name, String type, String description) {
        return MetafieldDefinitionRequest.builder()
                .key(key)
                .name(name)
                .namespace("custom")
                .type(type)
                .description(description)
                .ownerType("PRODUCT")
                .pin(true)
                .build();
    }

    /**
     * Bootstrap all metafield definitions.
     * Safe to re-run — skips definitions that already exist.
     */
    public void bootstrapAll() {
        logger.info("MetafieldDefinitionService::bootstrapAll():: ({} definitions)", DEFINITIONS.size());
        Set<String> existing = fetchExistingKeys();
        logger.info("MetafieldDefinitionService::bootstrapAll():: {} existing meta field definitions: {}", existing.size(),  existing);
        DEFINITIONS.forEach(def -> {
            if (existing.contains(def.getKey())) {
                logger.info("MetafieldDefinitionService::bootstrapAll()::Skipping '{}' — already exists", def.getKey());
            } else {
                createDefinition(def);
                // Respect Shopify rate limit (2 req/sec on Basic plan)
                sleep(500);
            }
        });
        logger.info("MetafieldDefinitionService::bootstrapAll()::Completed initial metafield setup");
    }
    @SuppressWarnings("unchecked")
    private Set<String> fetchExistingKeys() {
        logger.info("MetafieldDefinitionService::fetchExistingKeys()::Start");
        String query = """
        query ProductMetafieldDefinitions($first: Int = 100) {
          metafieldDefinitions(first: $first, ownerType: PRODUCT) {
            edges {
              node {
                id
                name
                namespace
                key
                type {
                  name
                }
              }
            }
          }
        }
        """;
        Map<String, Object> requestBody = Map.of(
                "query", query,
                "variables", Map.of("first", 100)
        );
        try {
            Map<String, Object> response = shopifyWebClient.post()
                    .uri("/graphql.json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(errorBody -> new RuntimeException(
                                            "Shopify GraphQL error fetching definitions: " + errorBody))
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            Map<String, Object> metafieldDefinitions = (Map<String, Object>) data.get("metafieldDefinitions");
            List<Map<String, Object>> edges = (List<Map<String, Object>>) metafieldDefinitions.get("edges");

            Set<String> keys = edges.stream()
                    .map(edge -> (Map<String, Object>) edge.get("node"))
                    .map(node -> (String) node.get("key"))
                    .collect(Collectors.toSet());

            logger.info("MetafieldDefinitionService::fetchExistingKeys():: Found {} existing meta field definitions", keys.size());
            logger.info("MetafieldDefinitionService::fetchExistingKeys()::End");
            return keys;

        } catch (Exception e) {
            logger.error("Failed to fetch existing metafield definitions: {}", e.getMessage());
            return Set.of();
        }
    }
    private void createDefinition(MetafieldDefinitionRequest def) {
        logger.info("MetafieldDefinitionService::fetchExistingKeys()::Start");
        try {
            Map<String, Object> response = shopifyWebClient.post()
                    .uri("/graphql.json")
                    .bodyValue(toGraphQLRequest(def))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(errorBody -> new RuntimeException(
                                            "Shopify API error for key '" + def.getKey() + "': " + errorBody))
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();
            logger.info("MetafieldDefinitionService::createDefinition()::Created metafield definition: '{}'", response);
        } catch (Exception e) {
            logger.error("MetafieldDefinitionService::createDefinition()::Failed to create metafield definition '{}': {}", def.getKey(), e.getMessage());
        }
    }

    public static Map<String, Object> toGraphQLRequest(MetafieldDefinitionRequest dto) {
        String mutation = """
                mutation CreateMetafieldDefinition($definition: MetafieldDefinitionInput!) {
                    metafieldDefinitionCreate(definition: $definition) {
                        createdDefinition {
                            id
                            name
                            namespace
                            key
                            type { name }
                        }
                        userErrors {
                            field
                            message
                        }
                    }
                }
                """;

        Map<String, Object> definition = new HashMap<>();
        definition.put("name", dto.getName());
        definition.put("namespace", dto.getNamespace());
        definition.put("key", dto.getKey());
        definition.put("type", dto.getType());
        definition.put("description", dto.getDescription());
        definition.put("ownerType", dto.getOwnerType());
        definition.put("pin", dto.isPin());

        Map<String, Object> variables = new HashMap<>();
        variables.put("definition", definition);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", mutation);
        requestBody.put("variables", variables);

        return requestBody;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
