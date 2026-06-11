package com.nocturna.performance.config;

import com.nocturna.performance.apicredentials.service.TokenService;
import com.nocturna.performance.config.properties.ShopifyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ShopifyRestAPIConfig {
    private static final Logger logger = LoggerFactory.getLogger(ShopifyRestAPIConfig.class);
    private final TokenService tokenService;
    private final ShopifyProperties properties;

    public ShopifyRestAPIConfig(TokenService tokenService, ShopifyProperties properties ) {
        this.tokenService = tokenService;
        this.properties = properties;
    }
    @Bean
    public WebClient shopifyWebClient() {
        String accessToken = tokenService.getValidToken();
        logger.info("Shopify access token loaded: [{}]",
                accessToken != null ? accessToken + "..." : "NULL");
        return WebClient.builder()
                .baseUrl(getBaseUrl())
                .defaultHeader("X-Shopify-Access-Token", accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    public String getBaseUrl() {
        return "https://" + properties.getStoreDomain() + "/admin/api/" + properties.getApiVersion();
    }

}
