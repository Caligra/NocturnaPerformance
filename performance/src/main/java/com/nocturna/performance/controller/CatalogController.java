package com.nocturna.performance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nocturna.performance.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;


@RestController
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/catalog")
    public void showCatalog(@RequestBody String input) throws IOException {
        logger.info(String.format("succesful input %s", input));
        catalogService.getBrandCatalog(input);
    }
}
