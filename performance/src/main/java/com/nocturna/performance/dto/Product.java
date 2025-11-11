package com.nocturna.performance.dto;

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
public class Product {

    @Column
    private String category;
    @Column
    private String brand;
    @Column
    private String brandName;
    @Column
    private String name;
    @Id
    private String upc;
    @Column
    private String shortDescription;
    @Column
    private String longDescription;
    @Column
    private String subcategory;

    @Override
    public String toString() {
        return "Product{" +
                "category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", brandName='" + brandName + '\'' +
                ", name='" + name + '\'' +
                ", upc='" + upc + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", longDescription='" + longDescription + '\'' +
                ", subcategory='" + subcategory + '\'' +
                '}';
    }
}
