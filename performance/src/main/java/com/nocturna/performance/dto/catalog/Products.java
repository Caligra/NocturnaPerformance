package com.nocturna.performance.dto.catalog;

import com.nocturna.performance.dto.catalog.Product;

import java.util.List;

public class Products {
    List<Product> products;

    public Products() {
    }

    public Products(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
