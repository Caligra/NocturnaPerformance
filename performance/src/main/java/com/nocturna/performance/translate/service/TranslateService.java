package com.nocturna.performance.translate.service;

import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.nocturna.performance.catalog.dto.HolleyProduct;
import com.nocturna.performance.catalog.dto.repository.HolleyProductRepository;
import com.nocturna.performance.config.HolleyProperties;
import com.nocturna.performance.config.SchedulerProperties;
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
    private final SchedulerProperties schedulerProperties;
    private final HolleyProperties holleyProperties;
    private final HolleyProductRepository holleyProductRepository;

    public TranslateService(HolleyProductRepository holleyProductRepository, HolleyProperties holleyProperties, SchedulerProperties schedulerProperties) {
        this.holleyProductRepository = holleyProductRepository;
        this.holleyProperties = holleyProperties;
        this.schedulerProperties = schedulerProperties;
    }

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
        var productsByBrand = holleyProductRepository.findByBrand(brand);
        // Logging size
        logger.info("translateProductItemDescriptions:: " + brand + " size():: " + productsByBrand.size());//OBS COP BOOTS 8 PK FORD 2V
        var variable = new ArrayList<String>();
        variable.add("OBS COP BOOTS 8 PK FORD 2V");
        var translatedtestDesc = executeSingleTranslationTest(holleyProperties.getProjectid(), variable);
        logger.info("translateProductItemDescriptions:: translatedtestDesc():: " + translatedtestDesc);//OBS COP BOOTS 8 PK FORD 2V

        // Loop products and translate, then fix format and store in shopify table
        for (HolleyProduct product : productsByBrand) {
            /**
             * Generating Map for text to be translated by Google API
             */
            /*logger.info("translateProductItemDescriptions:: " + product.getUpc() + " shD():: " + product.getShortDescription());
            logger.info("translateProductItemDescriptions:: " + product.getUpc() + " loD():: " + product.getLongDescription());
            logger.info("translateProductItemDescriptions:: " + product.getUpc() + " mkD():: " + product.getMarketingDescription());
            logger.info("translateProductItemDescriptions:: " + product.getUpc() + " inDe():: " + product.getInvoiceDescription());
*/
            var engDesc = new ArrayList<String>();
            engDesc.add((product.getShortDescription() == null || product.getShortDescription().isEmpty()) ? "" : product.getShortDescription());
            engDesc.add((product.getLongDescription() == null || product.getLongDescription().isEmpty()) ? "" : product.getLongDescription());
            engDesc.add((product.getMarketingDescription() == null || product.getMarketingDescription().isEmpty()) ? "" : product.getMarketingDescription());
            engDesc.add((product.getInvoiceDescription() == null || product.getInvoiceDescription().isEmpty()) ? "" : product.getInvoiceDescription());
            /*logger.info("translateProductItemDescriptions:: " + product.getUpc() + " engDesc():: " + engDesc);*/

            // Sending translation API call
            //var translatedDesc = executeTranslation(holleyProperties.getProjectid(), holleyProperties.getLanguaje(), engDesc);


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

        Map<String, String> translatedDesc = new HashMap<>();
        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(projectId, "global");
            TranslateTextResponse response = client.translateText(parent.toString(), targetLanguage, descriptions);
            // Store the translations for descriptions in map to be processed
            List<Translation> translationList = response.getTranslationsList();
            translatedDesc.put("shortDesc", translationList.get(0).getTranslatedText());
            translatedDesc.put("LongDesc", translationList.get(1).getTranslatedText());
            translatedDesc.put("marketDesc", translationList.get(2).getTranslatedText());
            translatedDesc.put("invoiceDesc", translationList.get(3).getTranslatedText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return translatedDesc;
    }

    public String executeSingleTranslationTest(String projectId, List<String> descriptions) {

        String outValue = "";
        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(projectId, "global");
            TranslateTextResponse response = client.translateText(parent.toString(), holleyProperties.getLanguaje(), descriptions);
            // Store the translations for descriptions in map to be processed
            List<Translation> translationList = response.getTranslationsList();
            outValue=translationList.get(0).getTranslatedText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outValue;
    }
}
