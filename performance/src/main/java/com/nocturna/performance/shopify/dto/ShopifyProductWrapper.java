package com.nocturna.performance.shopify.dto;

public class ShopifyProductWrapper {
    private ShopifyProduct product;

    public ShopifyProductWrapper(ShopifyProduct product) {
        this.product = product;
    }

    public ShopifyProduct getProduct() {
        return product;
    }

    public void setProduct(ShopifyProduct product) {
        this.product = product;
    }
}
