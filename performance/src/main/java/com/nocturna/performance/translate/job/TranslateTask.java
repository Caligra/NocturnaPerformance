package com.nocturna.performance.translate.job;

import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.translate.service.TranslateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class TranslateTask {

    private static final Logger log = LoggerFactory.getLogger(TranslateTask.class);
    @Autowired
    private TranslateService translateService;

    @Autowired
    private SchedulerProperties schedulerProperties;

    //@Scheduled(cron = "${cron.schedule.translate}")
    @Scheduled(fixedRate = 3000000) //50 mins set to test
    public void runTask() {
        /**
         * TODO fetch everything from table and check if translation exist, if no, API
         */
        boolean debug = false;
        if (debug) {
            log.info("Starting TranslateTask Job");
            String[] brandCodes = schedulerProperties.getBrandcodes().split("\\+");
            for (String code : brandCodes) {
                try {
                    translateService.translateProductItemDescriptions(code);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("Ending TranslateTask Job");
        }
    }
}
