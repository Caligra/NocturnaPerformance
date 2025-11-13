package com.nocturna.performance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="products_eng")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @Column
    private String category;
    @Column
    private String brand;
    @Column
    private String brand_name;
    @Column
    private String name;
    @Id
    private String upc;
    @Column
    private String short_description;
    @Column
    private String long_description;
    @Column
    private String sub_category;
    @Column
    private String marketing_description;

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
                '}';
    }
}
