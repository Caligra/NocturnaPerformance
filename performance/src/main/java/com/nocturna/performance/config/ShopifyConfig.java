package com.nocturna.performance.config;

import com.nocturna.performance.startup.NocturnaInitializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationProperties(prefix = "shopify")
@Data
public class ShopifyConfig {
    private static final Logger logger = LoggerFactory.getLogger(ShopifyConfig.class);

    private String storeDomain;
    private String accessToken;
    private String apiVersion;
    private String loadMetafields;
    private String createProducts;

    @Bean
    public WebClient shopifyWebClient() {
        logger.info("Shopify access token loaded: [{}]",
                accessToken != null ? accessToken + "..." : "NULL");
        return WebClient.builder()
                .baseUrl("https://" + storeDomain + "/admin/api/" + apiVersion)
                .defaultHeader("X-Shopify-Access-Token", accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getBaseUrl() {
        return "https://" + storeDomain + "/admin/api/" + apiVersion;
    }

}
