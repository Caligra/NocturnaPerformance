package com.nocturna.performance.brands.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "holley_brands")
@Getter
@Setter
public class HolleyBrand {

    @Id
    @Column(name = "pdm_internal_code", nullable = false)
    private String pdmInternalCode;
    @Column(name = "brand_name", nullable = false)
    private String brandName;
    @Column(name = "aca_brand_name", nullable = false)
    private String acaBrandName;
    @Column(name = "source", nullable = false)
    private String source;
    @Column(name = "approved", nullable = false)
    private boolean approved;
    @Column(name = "created_on", nullable = false, updatable = false )
    private LocalDateTime createdOn;
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;


}
