package com.nocturna.performance.shopify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shopify_products")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ShopifyProduct {

    /*private String category;
    private String brand;
    private String brand_name;
    private String name;
    private String short_description;
    private String long_description;
    private String sub_category;
    private String marketing_description;
    private String invoice_description;
    private String media_url;
    private String part_number;
    private String upc;*/
/*
    @Id
    private String id;
    private String title;
    private String handle;
    private String body_html;
    private String vendor;
    private String product_type;
    private String tags;
    private String status;*/

    private String category;
    private String brand;
    private String brand_name;
    private String name;
    @Id
    private String upc;

}
