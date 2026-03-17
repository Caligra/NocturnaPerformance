package com.nocturna.performance.catalog.dto.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocturna.performance.catalog.dto.HolleyProduct;

import java.util.List;

public class HolleyProducts {
    @JsonProperty("products")
    List<HolleyProduct> products;

    public HolleyProducts() {
    }

    public HolleyProducts(List<HolleyProduct> products) {
        this.products = products;
    }

    public List<HolleyProduct> getHolleyProducts() {
        return products;
    }

    public void setHolleyProducts(List<HolleyProduct> products) {
        this.products = products;
    }
}
