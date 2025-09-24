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

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String category;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String name;

    @Override
    public String toString() {
        return "Product{" +
                "category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
