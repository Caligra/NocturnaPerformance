package com.nocturna.performance.service;

import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.nocturna.performance.config.NocturnaProperties;
import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.dto.catalog.Product;
import com.nocturna.performance.dto.catalog.repository.ProductEngRepository;
import com.nocturna.performance.dto.exportproduct.ExportProduct;
import com.nocturna.performance.dto.exportproduct.repository.ExportProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranslateService {
    private static final Logger logger = LoggerFactory.getLogger(TranslateService.class);
    @Autowired
    private SchedulerProperties schedulerProperties;
    @Autowired
    private NocturnaProperties nocturnaProperties;
    @Autowired
    private ProductEngRepository productEngRepository;
    @Autowired
    private ExportProductRepository exportProductRepository;

    public String performTranslateServiceOperation() {
        /**
         * Pick data and send to AI https://libretranslate.com/
         * return data, parse, store
         * */
        try {
            String[] brandCodes = schedulerProperties.getBrandcodes().split("\\+");
            for (String code : brandCodes) {
                translateProductItemDescriptions(code);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Data from service!";
    }

    /**
     * Loop each product and store descriptions in Map
     * Translate descriptions and generate new ExportProduct to store and export later
     */
    public void translateProductItemDescriptions(String brand) throws IOException {
        //Fetch products by brand code
        List<Product> productsByBrand = productEngRepository.findByBrand(brand);
        logger.info("translateProductItemDescriptions:: " + brand + " size():: " + productsByBrand.size());
        List<ExportProduct> allTranslatedProducts = new ArrayList<>();

        //Loop products
        for (Product product : productsByBrand) {
            // Generating Map for translation
            List<String> engDesc = new ArrayList<>();
            engDesc.add((product.getShort_description() == null || product.getShort_description().isEmpty()) ? "" : product.getShort_description());
            engDesc.add((product.getLong_description() == null || product.getLong_description().isEmpty()) ? "" : product.getLong_description());
            engDesc.add((product.getMarketing_description() == null || product.getMarketing_description().isEmpty()) ? "" : product.getMarketing_description());
            engDesc.add((product.getInvoice_description() == null || product.getInvoice_description().isEmpty()) ? "" : product.getInvoice_description());
            Map<String, String> translatedDesc = executeTranslation(nocturnaProperties.getProjectid(), nocturnaProperties.getLanguaje(), engDesc);
            // Generating ExportProduct for manual report
            ExportProduct expProd = new ExportProduct();
            expProd.setCategory(product.getCategory());
            expProd.setBrand(product.getBrand());
            expProd.setBrand_name(product.getBrand_name());
            expProd.setName(product.getName());
            expProd.setPart_number(product.getPart_number());
            expProd.setUpc(product.getUpc());
            expProd.setMedia_url(product.getMedia_url());
            expProd.setInvoice_description(translatedDesc.get("invoiceDesc"));
            expProd.setShort_description(translatedDesc.get("shortDesc"));
            expProd.setLong_description(translatedDesc.get("LongDesc"));
            expProd.setMarketing_description(translatedDesc.get("marketDesc"));
            // Add to list used to save in DB
            allTranslatedProducts.add(expProd);
        }
        if (!allTranslatedProducts.isEmpty()) {
            // Save in DB
            exportProductRepository.saveAll(allTranslatedProducts);
        }
    }

    /**
     * Method to execute Google translate API
     */
    public static Map<String, String> executeTranslation(String projectId, String targetLanguage, List<String> descriptions)
            throws IOException {

        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(projectId, "global");
            TranslateTextResponse response = client.translateText(parent.toString(), targetLanguage, descriptions);
            // Store the translations for descriptions in map to be processed
            Map<String, String> translatedDesc = new HashMap<>();
            List<Translation> translationList = response.getTranslationsList();
            translatedDesc.put("shortDesc", translationList.get(0).getTranslatedText());
            translatedDesc.put("LongDesc", translationList.get(1).getTranslatedText());
            translatedDesc.put("marketDesc", translationList.get(2).getTranslatedText());
            translatedDesc.put("invoiceDesc", translationList.get(3).getTranslatedText());
            return translatedDesc;
        }
    }
}
