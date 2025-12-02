package com.nocturna.performance;

import com.nocturna.performance.config.QuartzServletListener;
import jakarta.servlet.ServletContextListener;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NocturnaPerformanceApplication {
	private static final Logger log = LoggerFactory.getLogger(NocturnaPerformanceApplication.class);

	public static void main(String[] args) throws SchedulerException {
		SpringApplication.run(NocturnaPerformanceApplication.class, args);
	}
	@Bean
	public ServletListenerRegistrationBean<ServletContextListener> quartzServletListener() {
		ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean();
		bean.setListener(new QuartzServletListener());
		return bean;
	}
}