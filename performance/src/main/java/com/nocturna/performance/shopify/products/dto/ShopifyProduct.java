package com.nocturna.performance.shopify.products.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "shopify_products")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ShopifyProduct {

    @Id
    @Column(name = "upc", length = 12)
    private String upc;

    @Column(name = "part_number")
    private String partNumber;

    @Column(name = "brand")
    private String brand;

    @Column(name = "name")
    private String name;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "marketing_description", columnDefinition = "TEXT")
    private String marketingDescription;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "application_make")
    private String applicationMake;

    @Column(name = "application_model", columnDefinition = "TEXT")
    private String applicationModel;

    @Column(name = "application_year_from_to")
    private String applicationYearFromTo;

    @Column(name = "application_full_detail", columnDefinition = "TEXT")
    private String applicationFullDetail;

    @Column(name = "media_url", columnDefinition = "TEXT")
    private String mediaUrl;

    @Column(name = "list_price", precision = 10, scale = 2)
    private BigDecimal listPrice;

    @Column(name = "shipping_weight")
    private Double shippingWeight;

    @Column(name = "shipping_length")
    private Double shippingLength;

    @Column(name = "shipping_width")
    private Double shippingWidth;

    @Column(name = "shipping_height")
    private Double shippingHeight;

    @Column(name = "shopify_product_id")
    private Long shopifyProductId;

    @Column(name = "shopify_variant_id")
    private Long shopifyVariantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status")
    private SyncStatus syncStatus = SyncStatus.pending;

    @Column(name = "sync_error", columnDefinition = "TEXT")
    private String syncError;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    public enum SyncStatus { pending, synced, error, skipped }


}
