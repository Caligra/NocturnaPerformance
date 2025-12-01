package com.nocturna.performance.dto.exportproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "export_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(ExportProductID.class)
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


}
