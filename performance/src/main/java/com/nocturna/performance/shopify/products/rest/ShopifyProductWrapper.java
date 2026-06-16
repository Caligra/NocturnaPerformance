package com.nocturna.performance.shopify.products.rest;

import com.nocturna.performance.shopify.products.dto.ShopifyProduct;

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
