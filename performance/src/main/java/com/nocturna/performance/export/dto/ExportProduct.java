package com.nocturna.performance.export.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "export_products")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(ExportProductID.class)
@NamedNativeQueries({
        @NamedNativeQuery(name = "ExportProduct_FindByBrandCode",query = "select * from export_products where brand = ?", resultClass = ExportProduct.class)
})
public class ExportProduct {

    @Column
    private String category;
    @Column
    private String brand;
    @Column
    private String brand_name;
    @Column
    private String name;

    @Column
    private String short_description;
    @Column
    private String long_description;
    @Column
    private String sub_category;
    @Column(columnDefinition = "TEXT")
    private String marketing_description;
    @Column
    private String invoice_description;
    @Column(columnDefinition = "TEXT")
    private String media_url;
    @Id
    @Column
    private String part_number;
    @Id
    @Column
    private String upc;

    @Override
    public String toString() {
        return "Product{" +
                "category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", brandName='" + brand_name + '\'' +
                ", name='" + name + '\'' +
                ", upc='" + upc + '\'' +
                ", shortDescription='" + short_description + '\'' +
                ", longDescription='" + long_description + '\'' +
                ", subcategory='" + sub_category + '\'' +
                ", marketing_description='" + marketing_description + '\'' +
                ", invoice_description='" + invoice_description + '\'' +
                ", part_number='" + part_number + '\'' +
                ", media_url='" + media_url + '\'' +
                '}';
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

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getLong_description() {
        return long_description;
    }

    public void setLong_description(String long_description) {
        this.long_description = long_description;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getMarketing_description() {
        return marketing_description;
    }

    public void setMarketing_description(String marketing_description) {
        this.marketing_description = marketing_description;
    }

    public String getInvoice_description() {
        return invoice_description;
    }

    public void setInvoice_description(String invoice_description) {
        this.invoice_description = invoice_description;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getPart_number() {
        return part_number;
    }

    public void setPart_number(String part_number) {
        this.part_number = part_number;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }
}
