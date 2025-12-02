package com.nocturna.performance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cron.schedule")
public class SchedulerProperties {
    private String catalog;
    private String translate;
    private String export;
    private String brandcodes;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getExport() {
        return export;
    }

    public void setExport(String export) {
        this.export = export;
    }

    public String getBrandcodes() {
        return brandcodes;
    }

    public void setBrandcodes(String brandcodes) {
        this.brandcodes = brandcodes;
    }
}
