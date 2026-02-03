package com.nocturna.performance.dto.catalog;

import com.nocturna.performance.dto.catalog.Product;

import java.util.List;

public class HolleyProducts {
    List<Product> products;

    public HolleyProducts() {
    }

    public HolleyProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
