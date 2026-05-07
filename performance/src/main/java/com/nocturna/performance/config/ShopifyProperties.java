package com.nocturna.performance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ShopifyProperties {

    @Value("${shopify.shop}")
    private String shop;
    @Value("${shopify.rest.token}")
    private String restToken;

    @Value("${shopify.api.version}")
    private String apiVersion;

    @Value("${shopify.site.products}")
    private String createRestProductsUrl;

    public String getBaseUrl() {
        return "https://" + shop + "/admin/api/" + apiVersion;
    }

}
