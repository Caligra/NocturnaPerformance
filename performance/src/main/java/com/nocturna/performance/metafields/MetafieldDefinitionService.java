package com.nocturna.performance.metafields;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetafieldDefinitionService {
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
        log.info("Starting metafield definition bootstrap ({} definitions)", DEFINITIONS.size());

        Set<String> existing = fetchExistingKeys();
        log.info("Found {} existing metafield definitions: {}", existing.size(),  existing);

        /*DEFINITIONS.forEach(def -> {
            if (existing.contains(def.getKey())) {
                log.info("Skipping '{}' — already exists", def.getKey());
            } else {
                createDefinition(def);
                // Respect Shopify rate limit (2 req/sec on Basic plan)
                sleep(500);
            }
        });*/

        log.info("Bootstrap complete.");
    }
    @SuppressWarnings("unchecked")
    private Set<String> fetchExistingKeys() {
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

            log.info("Found {} existing metafield definitions", keys.size());
            return keys;

        } catch (Exception e) {
            log.error("Failed to fetch existing metafield definitions: {}", e.getMessage());
            return Set.of();
        }
    }


    private void createDefinition(MetafieldDefinitionRequest def) {
        try {
            Map<String, Object> body = Map.of("metafield_definition", def);
            log.info("CcreateDefinition: '{}'", body);

            Map<String, Object> response = shopifyWebClient.post()
                    .uri("/metafield_definitions.json")
                    .bodyValue(body)
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

            log.info("Created metafield definition: '{}'", def.getKey());
            log.info("Created metafield definition: '{}'", response.toString());

        } catch (Exception e) {
            log.error("Failed to create metafield definition '{}': {}", def.getKey(), e.getMessage());
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
