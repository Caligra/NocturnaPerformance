package com.nocturna.performance.catalog.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "holley_images")
public class HolleyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    @ManyToOne
    @JoinColumn(name = "upc", referencedColumnName = "upc")
    private HolleyProduct product;

    public HolleyImage(String url, HolleyProduct product ){
        this.product = product;
        this.url = url;
    }

}
