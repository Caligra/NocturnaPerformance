package com.nocturna.performance.holley.job;

import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.holley.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CatalogTask {

    private static final Logger log = LoggerFactory.getLogger(CatalogTask.class);
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private SchedulerProperties schedulerProperties;
    //@Scheduled(cron = "${cron.schedule.catalog}")
    @Scheduled(fixedRate = 3000000) //50 mins set to test
    public void runTask(){
        boolean debug = false;
        if(debug) {
            log.info("Starting Catalog Job");
            String[] brandCodes = schedulerProperties.getBrandcodes().split("\\+");
            for (String code : brandCodes) {
                try {
                    catalogService.getBrandCatalog(code);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("Ending Catalog Job");
        }
    }
}
