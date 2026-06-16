package com.nocturna.performance.shopify.products.service;

import com.nocturna.performance.shopify.products.dto.repository.ShopifyProductStagingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductSyncService {

    private static final Logger log = LoggerFactory.getLogger(ProductSyncService.class);

    private final ShopifyProductStagingRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${shopify.store.domain}")
    private String storeDomain;

    @Value("${shopify.access.token}")
    private String accessToken;

    @Value("${shopify.api.version:2025-04}")
    private String apiVersion;

    private final ShopifyProductStagingRepository repo;
    private final SmartCollectionService smartCollectionService;
    private final RestTemplate restTemplate = new RestTemplate();

    public ProductSyncService(ShopifyProductStagingRepository repo,
                              SmartCollectionService smartCollectionService) {
        this.repo = repo;
        this.smartCollectionService = smartCollectionService;
    }

    // ─────────────────────────────────────────────
    // GraphQL — productCreate
    // ─────────────────────────────────────────────

    private static final String PRODUCT_CREATE_MUTATION = """
            mutation productCreate($input: ProductInput!, $media: [CreateMediaInput!]) {
              productCreate(input: $input, media: $media) {
                product {
                  id
                  title
                  variants(first: 1) {
                    edges {
                      node {
                        id
                        price
                        sku
                      }
                    }
                  }
                }
                userErrors {
                  field
                  message
                }
              }
            }
            """;

    // ─────────────────────────────────────────────
    // Public entry point — sync all pending rows
    // ─────────────────────────────────────────────

    public void syncPendingProducts() {
        List<ShopifyProductStaging> pending = repo.findBySyncStatus(SyncStatus.pending);
        log.info("Found {} pending products to sync", pending.size());

        for (ShopifyProductStaging row : pending) {
            try {
                syncProduct(row);
            } catch (Exception e) {
                log.error("Unexpected error syncing UPC={}: {}", row.getUpc(), e.getMessage(), e);
                markError(row, "Unexpected: " + e.getMessage());
            }
        }
    }

    // ─────────────────────────────────────────────
    // Single product sync
    // ─────────────────────────────────────────────

    private void syncProduct(ShopifyProductStaging row) {
        log.info("Syncing product UPC={} name='{}'", row.getUpc(), row.getName());

        // ── Build tags ──────────────────────────────
        List<String> tags = new ArrayList<>();
        tags.add("upc:" + row.getUpc());
        tags.add("part:" + row.getPartNumber());
        if (row.getBrand() != null) tags.add("brand:" + row.getBrand().toLowerCase());
        if (row.getCategory() != null) tags.add("category:" + row.getCategory().toLowerCase());
        if (row.getSubCategory() != null) tags.add("subcategory:" + row.getSubCategory().toLowerCase());
        if (row.getApplicationMake() != null) tags.add("make:" + row.getApplicationMake().toLowerCase());
        if (row.getApplicationModel() != null) tags.add("model:" + row.getApplicationModel().toLowerCase());
        if (row.getApplicationYearFromTo() != null) tags.add("year:" + row.getApplicationYearFromTo());

        // ── Build metafields ────────────────────────
        List<Map<String, Object>> metafields = new ArrayList<>();
        if (row.getApplicationMake() != null) {
            metafields.add(metafield("custom", "vehicle_make", row.getApplicationMake(), "single_line_text_field"));
        }
        if (row.getApplicationModel() != null) {
            metafields.add(metafield("custom", "vehicle_model", row.getApplicationModel(), "single_line_text_field"));
        }
        if (row.getApplicationYearFromTo() != null) {
            metafields.add(metafield("custom", "year_range", row.getApplicationYearFromTo(), "single_line_text_field"));
        }
        if (row.getShortDescription() != null) {
            metafields.add(metafield("custom", "short_description", row.getShortDescription(), "single_line_text_field"));
        }

        // ── Build default variant ───────────────────
        Map<String, Object> variant = new HashMap<>();
        variant.put("sku", row.getUpc());                          // UPC as SKU
        variant.put("price", row.getListPrice() != null
                ? row.getListPrice().toPlainString() : "0.00");
        variant.put("inventoryManagement", "SHOPIFY");
        variant.put("inventoryPolicy", "DENY");

        // Shipping dimensions on variant
        if (row.getShippingWeight() != null) {
            variant.put("weight", row.getShippingWeight());
            variant.put("weightUnit", "POUNDS");               // adjust to your unit
        }

        // ── Build product input ─────────────────────
        Map<String, Object> input = new HashMap<>();
        input.put("title", row.getName());
        input.put("bodyHtml", row.getMarketingDescription() != null ? row.getMarketingDescription() : "");
        input.put("vendor", row.getBrand());
        input.put("productType", row.getCategory() != null ? row.getCategory() : "");
        input.put("tags", tags);
        input.put("variants", List.of(variant));
        input.put("metafields", metafields);
        input.put("status", "ACTIVE");

        // ── Build media (image) ─────────────────────
        List<Map<String, Object>> media = new ArrayList<>();
        if (row.getMediaUrl() != null && !row.getMediaUrl().isBlank()) {
            media.add(Map.of(
                    "originalSource", row.getMediaUrl(),
                    "alt", row.getName(),
                    "mediaContentType", "IMAGE"
            ));
        }

        // ── Execute mutation ────────────────────────
        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);
        variables.put("media", media);

        Map<String, Object> response = executeGraphQL(PRODUCT_CREATE_MUTATION, variables);

        // ── Parse response ──────────────────────────
        try {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            Map<String, Object> productCreate = (Map<String, Object>) data.get("productCreate");

            List<Map<String, String>> userErrors = (List<Map<String, String>>) productCreate.get("userErrors");
            if (userErrors != null && !userErrors.isEmpty()) {
                String errMsg = userErrors.stream()
                        .map(e -> e.get("field") + ": " + e.get("message"))
                        .reduce("", (a, b) -> a + " | " + b);
                log.error("Shopify userErrors for UPC={}: {}", row.getUpc(), errMsg);
                markError(row, errMsg);
                return;
            }

            Map<String, Object> product = (Map<String, Object>) productCreate.get("product");
            String productGid = (String) product.get("id");                          // gid://shopify/Product/123
            Long productId = extractNumericId(productGid);

            // Extract default variant ID
            Map<String, Object> variants = (Map<String, Object>) product.get("variants");
            List<Map<String, Object>> edges = (List<Map<String, Object>>) variants.get("edges");
            String variantGid = (String) ((Map<String, Object>) edges.get(0).get("node")).get("id");
            Long variantId = extractNumericId(variantGid);

            // ── Write back to staging table ─────────
            row.setShopifyProductId(productId);
            row.setShopifyVariantId(variantId);
            row.setSyncStatus(SyncStatus.synced);
            row.setSyncError(null);
            row.setLastSyncedAt(LocalDateTime.now());
            repo.save(row);

            log.info("Synced UPC={} → productId={}, variantId={}", row.getUpc(), productId, variantId);
            // ── Auto-create smart collection for vehicle make ──
            triggerSmartCollections(row);   // ← ADD THIS
        } catch (Exception e) {
            log.error("Failed to parse productCreate response for UPC={}: {}", row.getUpc(), e.getMessage(), e);
            markError(row, "Parse error: " + e.getMessage());
        }
    }

    /**
     * Creates smart collections for the vehicle make (and optionally category)
     * of the just-synced product. Idempotent — safe to call on every sync run.
     */
    private void triggerSmartCollections(ShopifyProductStaging row) {

        // ── Collection by vehicle Make ──────────────────────────────────────
        if (row.getApplicationMake() != null && !row.getApplicationMake().isBlank()) {
            String makeTitle = "Make: " + row.getApplicationMake();
            List<Map<String, String>> makeRules = List.of(
                    Map.of(
                            "column",    "TAG",
                            "relation",  "EQUALS",
                            "condition", "make:" + row.getApplicationMake().toLowerCase()
                    )
            );
            String makeCollectionId = smartCollectionService.createSmartCollectionIfAbsent(
                    makeTitle, makeRules, false
            );
            if (makeCollectionId != null) {
                log.info("Make collection ready: '{}' → {}", makeTitle, makeCollectionId);
            }
        }

        // ── Collection by Category ──────────────────────────────────────────
        if (row.getCategory() != null && !row.getCategory().isBlank()) {
            String categoryTitle = "Category: " + row.getCategory();
            List<Map<String, String>> categoryRules = List.of(
                    Map.of(
                            "column",    "TAG",
                            "relation",  "EQUALS",
                            "condition", "category:" + row.getCategory().toLowerCase()
                    )
            );
            String categoryCollectionId = smartCollectionService.createSmartCollectionIfAbsent(
                    categoryTitle, categoryRules, false
            );
            if (categoryCollectionId != null) {
                log.info("Category collection ready: '{}' → {}", categoryTitle, categoryCollectionId);
            }
        }

        // ── Collection by Brand ─────────────────────────────────────────────
        if (row.getBrand() != null && !row.getBrand().isBlank()) {
            String brandTitle = "Brand: " + row.getBrand();
            List<Map<String, String>> brandRules = List.of(
                    Map.of(
                            "column",    "VENDOR",
                            "relation",  "EQUALS",
                            "condition", row.getBrand()
                    )
            );
            String brandCollectionId = smartCollectionService.createSmartCollectionIfAbsent(
                    brandTitle, brandRules, false
            );
            if (brandCollectionId != null) {
                log.info("Brand collection ready: '{}' → {}", brandTitle, brandCollectionId);
            }
        }
    }


    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private Map<String, Object> metafield(String namespace, String key, String value, String type) {
        return Map.of(
                "namespace", namespace,
                "key", key,
                "value", value,
                "type", type
        );
    }

    /**
     * Extracts numeric ID from GID: "gid://shopify/Product/123456" → 123456L
     */
    private Long extractNumericId(String gid) {
        if (gid == null) return null;
        return Long.parseLong(gid.substring(gid.lastIndexOf('/') + 1));
    }

    private void markError(ShopifyProductStaging row, String error) {
        row.setSyncStatus(SyncStatus.error);
        row.setSyncError(error);
        row.setLastSyncedAt(LocalDateTime.now());
        repo.save(row);
    }

    private Map<String, Object> executeGraphQL(String query, Map<String, Object> variables) {
        String url = String.format("https://%s/admin/api/%s/graphql.json", storeDomain, apiVersion);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", accessToken);

        Map<String, Object> body = Map.of("query", query, "variables", variables);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return response.getBody();
    }
}