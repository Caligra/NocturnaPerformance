package com.nocturna.performance.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "holley_products")
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "last_updated")
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    public String getMarketingDescription() {
        return marketingDescription;
    }

    public void setMarketingDescription(String marketingDescription) {
        this.marketingDescription = marketingDescription;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getApplicationMake() {
        return applicationMake;
    }

    public void setApplicationMake(String applicationMake) {
        this.applicationMake = applicationMake;
    }

    public String getShippingHeight() {
        return shippingHeight;
    }

    public void setShippingHeight(String shippingHeight) {
        this.shippingHeight = shippingHeight;
    }

    public String getShippingWidth() {
        return shippingWidth;
    }

    public void setShippingWidth(String shippingWidth) {
        this.shippingWidth = shippingWidth;
    }

    public String getShippingLength() {
        return shippingLength;
    }

    public void setShippingLength(String shippingLength) {
        this.shippingLength = shippingLength;
    }

    public String getMerchWidth() {
        return merchWidth;
    }

    public void setMerchWidth(String merchWidth) {
        this.merchWidth = merchWidth;
    }

    public String getMerchHeigth() {
        return merchHeigth;
    }

    public void setMerchHeigth(String merchHeigth) {
        this.merchHeigth = merchHeigth;
    }

    public String getMerchLength() {
        return merchLength;
    }

    public void setMerchLength(String merchLength) {
        this.merchLength = merchLength;
    }

    public String getMerchWeight() {
        return merchWeight;
    }

    public void setMerchWeight(String merchWeight) {
        this.merchWeight = merchWeight;
    }

    public String getApplicationModel() {
        return applicationModel;
    }

    public void setApplicationModel(String applicationModel) {
        this.applicationModel = applicationModel;
    }

    public String getApplicationYearFromTo() {
        return applicationYearFromTo;
    }

    public void setApplicationYearFromTo(String applicationYearFromTo) {
        this.applicationYearFromTo = applicationYearFromTo;
    }

    public String getApplicationFullDetail() {
        return applicationFullDetail;
    }

    public void setApplicationFullDetail(String applicationFullDetail) {
        this.applicationFullDetail = applicationFullDetail;
    }

    public String getList_price() {
        return list_price;
    }

    public void setList_price(String list_price) {
        this.list_price = list_price;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
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
