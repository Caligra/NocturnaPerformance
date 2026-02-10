package com.nocturna.performance.shopify.job;

import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.shopify.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class ShopifyProductsAPITask {

    private static final Logger log = LoggerFactory.getLogger(ShopifyProductsAPITask.class);

    @Autowired
    private SchedulerProperties schedulerProperties;
    @Autowired
    ProductService productService;

    @Scheduled(fixedRate = 300000)
    public void runTask() throws IOException {
        log.info("Starting ProductAPI Job");
        //productService.shopifyCreateProducts();
        log.info("Ending ProductAPI Job");
    }

}
