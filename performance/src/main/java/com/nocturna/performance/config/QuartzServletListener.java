package com.nocturna.performance.config;

import com.nocturna.performance.job.CatalogJob;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebListener
public class QuartzServletListener implements ServletContextListener {
    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Retrieve the Spring application context
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        // Inject the Scheduler bean manually
        scheduler = context.getBean(Scheduler.class);
        try {
            // Define a Quartz job and its trigger
            JobDetail jobDetail = JobBuilder.newJob(CatalogJob.class)
                    .withIdentity("myJob", "group1")
                    .usingJobData("brandLoading", "value")
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("myJobTrigger", "group1")
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(2)
                            .repeatForever())
                    .build();
            // Schedule the job
            scheduler.scheduleJob(jobDetail, trigger);
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