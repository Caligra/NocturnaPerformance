package com.nocturna.performance.export.job;

import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.export.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ExportTask {

    private static final Logger log = LoggerFactory.getLogger(ExportTask.class);
    @Autowired
    private SchedulerProperties schedulerProperties;
    @Autowired
    ExportService exportService;
    /*@Scheduled(fixedRateString = "${cron.schedule.export}")
    public void runTask() throws IOException {
        log.info("Starting Export Job");
        exportService.doSomething();

        log.info("Ending Export Job");
    }*/

    //@Scheduled(fixedRate = 300000)
    @Scheduled(cron = "${cron.schedule.export}")
    public void runTask() throws IOException {
        log.info("Starting Export Job");
        //exportService.exportExcelProducts();
        log.info("Ending Export Job");
    }
}
