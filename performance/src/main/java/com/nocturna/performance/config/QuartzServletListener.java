package com.nocturna.performance.config;

import com.nocturna.performance.job.CatalogJob;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebListener
public class QuartzServletListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(QuartzServletListener.class);

    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Retrieve the Spring application context
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        // Inject the Scheduler bean manually
        scheduler = context.getBean(Scheduler.class);

        try {
            // Defining catalog downloading job
            JobDetail jobDetail = JobBuilder.newJob(CatalogJob.class)
                    .usingJobData("param", "value") //pass report name
                    .build();
            // Execute catalog download job
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            //.withIntervalInHours(8)
                            .withIntervalInMinutes(5)
                            //.withIntervalInSeconds(2)
                            .repeatForever()
                            .withRepeatCount(1))
                    .build();
            // Schedule the job
            log.info("Before scheduling job");
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("After scheduling job");
            // Start the scheduler
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Shutdown Quartz scheduler
        try {
            if (scheduler != null) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}