package com.nocturna.performance.service;

import org.springframework.stereotype.Service;

@Service
public class TranslateService {
    public String performTranslateServiceOperation() {
        /**
         * Pick data and send to AI https://libretranslate.com/
         * return data, parse, store
         * */
        return "Data from service!";
    }

    public String printSomething() {
        return "Another yey";
    }
}
