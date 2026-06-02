package com.nocturna.performance.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "holley.pdm")
@Getter
@Setter
public class HolleyProperties {
    private String url;
    private String token;
    private String projectid;
    private String languaje;
    private String template;
}
