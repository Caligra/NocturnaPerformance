package com.nocturna.performance.controller;

import com.nocturna.performance.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/catalog")
    public String showCatalog(@RequestBody String input) {
        logger.info(String.format("succesful input %s", input));
        return catalogService.getBrandCatalog(input);
    }
}
