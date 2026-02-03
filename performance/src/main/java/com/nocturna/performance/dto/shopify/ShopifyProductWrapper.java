package com.nocturna.performance.dto.shopify;

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
