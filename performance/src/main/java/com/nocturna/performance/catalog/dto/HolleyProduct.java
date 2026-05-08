package com.nocturna.performance.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "holley_products")
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class HolleyProduct {
    @Column
    private String category;
    @Column
    private String brand;
    @Column(name = "brand_name")
    @JsonProperty("brand_name")
    private String brandName;
    @Column
    private String name;
    @Id
    @Column(length = 12)
    private String upc;
    @Column(name = "short_description")
    @JsonProperty("short_description")
    private String shortDescription;
    @Column(name = "invoice_description")
    @JsonProperty("invoice_description")
    private String invoiceDescription;
    @Column(columnDefinition = "TEXT", name = "marketing_description")
    @JsonProperty("marketing_description")
    private String marketingDescription;
    @Column(columnDefinition = "TEXT", name = "media_url")
    @JsonProperty("media_url")
    private String mediaUrl;
    @Column(name = "long_description")
    @JsonProperty("long_description")
    private String longDescription;
    @Column(name = "sub_category")
    @JsonProperty("sub_category")
    private String subCategory;
    @Column(name = "part_number")
    @JsonProperty("part_number")
    private String partNumber;
    @Column(name = "application_make")
    @JsonProperty("application_make")
    private String applicationMake;
    @Column(name = "shipping_height")
    @JsonProperty("shipping_height")
    private String shippingHeight;
    @Column(name = "shipping_width")
    @JsonProperty("shipping_width")
    private String shippingWidth;
    @Column(name = "shipping_length")
    @JsonProperty("shipping_length")
    private String shippingLength;
    @Column(name = "merch_width")
    @JsonProperty("merch_width")
    private String merchWidth;
    @Column(name = "merch_heigth")
    @JsonProperty("merch_heigth")
    private String merchHeigth;
    @Column(name = "merch_length")
    @JsonProperty("merch_length")
    private String merchLength;
    @Column(name = "merch_weight")
    @JsonProperty("merch_weight")
    private String merchWeight;
    @Column(columnDefinition = "TEXT", name = "application_model")
    @JsonProperty("application_model")
    private String applicationModel;
    @Column(name = "application_year_from_to")
    @JsonProperty("application_year_from_to")
    private String applicationYearFromTo;
    @Column(columnDefinition = "TEXT", name = "application_full_detail")
    @JsonProperty("application_full_detail")
    private String applicationFullDetail;
    @Column(name = "list_price")
    @JsonProperty("list_price")
    private String list_price;
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;
    @Column(name = "last_updated", insertable = false, updatable = false)
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HolleyImage> images;

    public HolleyProduct() {
    }

    public HolleyProduct(String category, String brand, String brandName, String name, String upc, String shortDescription, String invoiceDescription, String marketingDescription, String mediaUrl, String longDescription, String subCategory, String partNumber, String applicationMake, String shippingHeight, String shippingWidth, String shippingLength, String merchWidth, String merchHeigth, String merchLength, String merchWeight, String applicationModel, String applicationYearFromTo, String applicationFullDetail, String list_price, LocalDateTime createdOn, LocalDateTime lastUpdated) {
        this.category = category;
        this.brand = brand;
        this.brandName = brandName;
        this.name = name;
        this.upc = upc;
        this.shortDescription = shortDescription;
        this.invoiceDescription = invoiceDescription;
        this.marketingDescription = marketingDescription;
        this.mediaUrl = mediaUrl;
        this.longDescription = longDescription;
        this.subCategory = subCategory;
        this.partNumber = partNumber;
        this.applicationMake = applicationMake;
        this.shippingHeight = shippingHeight;
        this.shippingWidth = shippingWidth;
        this.shippingLength = shippingLength;
        this.merchWidth = merchWidth;
        this.merchHeigth = merchHeigth;
        this.merchLength = merchLength;
        this.merchWeight = merchWeight;
        this.applicationModel = applicationModel;
        this.applicationYearFromTo = applicationYearFromTo;
        this.applicationFullDetail = applicationFullDetail;
        this.list_price = list_price;
        this.createdOn = createdOn;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "HolleyProduct{" +
                "upc='" + upc + '\'' +
                ", applicationFullDetail='" + applicationFullDetail + '\'' +
                '}';
    }
}
