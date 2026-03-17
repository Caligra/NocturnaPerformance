package com.nocturna.performance.translate.service;

import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.nocturna.performance.catalog.dto.HolleyProduct;
import com.nocturna.performance.catalog.dto.repository.HolleyRepository;
import com.nocturna.performance.config.NocturnaProperties;
import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.shopify.dto.ShopifyProduct;
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
    private HolleyRepository productEngRepository;

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
        //Fetch products using brand code
        var productsByBrand = productEngRepository.findByBrand(brand);
        // Logging size
        logger.info("translateProductItemDescriptions:: " + brand + " size():: " + productsByBrand.size());

        // Loop products and translate, then fix format and store in shopify table
        for (HolleyProduct product : productsByBrand) {
            /**
             * Generating Map for text to be translated by Google API
             */
            var engDesc = new ArrayList<String>();
            engDesc.add((product.getShortDescription() == null || product.getShortDescription().isEmpty()) ? "" : product.getShortDescription());
            engDesc.add((product.getLongDescription() == null || product.getLongDescription().isEmpty()) ? "" : product.getLongDescription());
            engDesc.add((product.getMarketingDescription() == null || product.getMarketingDescription().isEmpty()) ? "" : product.getMarketingDescription());
            engDesc.add((product.getInvoiceDescription() == null || product.getInvoiceDescription().isEmpty()) ? "" : product.getInvoiceDescription());
            // Sending translation API call
            var translatedDesc = executeTranslation(nocturnaProperties.getProjectid(), nocturnaProperties.getLanguaje(), engDesc);



            // Creating Shopify Product with format for insertion after creating it
            /*var shopifyProd = new ShopifyProduct();
            shopifyProd.setTitle(translatedDesc.get("shortDesc"));
            shopifyProd.setId("");
            shopifyProd.setBody_html(translatedDesc.get("LongDesc"));
            shopifyProd.setTags("");
            shopifyProd.setStatus("");
            shopifyProd.setProduct_type("");
            shopifyProd.setVendor("");*/


            /*var expProd = new ExportProduct();
            expProd.setCategory(product.getCategory());
            expProd.setBrand(product.getBrand());
            expProd.setBrand_name(product.getBrandName());
            expProd.setName(product.getName());
            expProd.setPart_number(product.getPartNumber());
            expProd.setUpc(product.getUpc());
            expProd.setMedia_url(product.getMediaUrl());
            expProd.setInvoice_description(translatedDesc.get("invoiceDesc"));
            expProd.setShort_description(translatedDesc.get("shortDesc"));
            expProd.setLong_description(translatedDesc.get("LongDesc"));
            expProd.setMarketing_description(translatedDesc.get("marketDesc"));
            // Add to list used to save in DB
            allTranslatedProducts.add(expProd);*/
        }
        /*if (!allTranslatedProducts.isEmpty()) {
            // Save in DB
            exportProductRepository.saveAll(allTranslatedProducts);
        }*/ //TODO
    }



    /**
     * Method to execute Google Translate API
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
