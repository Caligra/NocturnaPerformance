package com.nocturna.performance.job;

import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CatalogTask {

    private static final Logger log = LoggerFactory.getLogger(CatalogJob.class);
    @Autowired
    private CatalogService catalogService;

    @Autowired
    private SchedulerProperties schedulerProperties;
    @Scheduled(fixedRate = 3000)
    public void runRask(){
        log.info("Starting Catalog Job");
        String[] brandCodes = schedulerProperties.getBrandcodes().split("\\+");
        for(String code : brandCodes){
            catalogService.testing(code);
        }
        log.info("Ending Catalog Job");
    }


}
