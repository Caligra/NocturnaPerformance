package com.nocturna.performance.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shopify")
@Getter
@Setter
public class ShopifyProperties {
    private String storeDomain;
    private String apiVersion;
    private String loadMetafields;
    private String createProducts;
}
