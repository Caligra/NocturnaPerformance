package com.nocturna.performance.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cron.schedule")
@Getter
@Setter
public class SchedulerProperties {
    private String catalog;
    private String translate;
    private String export;
    private String brandcodes;

}
