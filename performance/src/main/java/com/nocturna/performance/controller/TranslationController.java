package com.nocturna.performance.controller;

import com.nocturna.performance.service.TranslateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslationController {

    private final TranslateService translateService;

    public TranslationController(TranslateService translateService){
        this.translateService=translateService;
    }

    @GetMapping("/catalog/translate")
    public void startTranslation(String JSONCatalog){
        translateService.performTranslateServiceOperation();
    }


}
