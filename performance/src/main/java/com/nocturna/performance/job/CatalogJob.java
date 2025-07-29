package com.nocturna.performance.job;

import com.nocturna.performance.service.CatalogService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Map;

@Component
public class CatalogJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(CatalogJob.class);
    @Autowired
    private CatalogService catalogService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        catalogService.getBrandCatalog("catalogjob");
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Map <String, Object> toPrint = dataMap.getWrappedMap();
        System.out.println(toPrint.toString());
        String param = dataMap.getString("param");
        System.out.println(MessageFormat.format("Job: {0}; param: {1}; Thread: {2}",
                getClass(), param, Thread.currentThread().getName()));
    }
}
