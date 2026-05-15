package com.nocturna.performance.translate.service;

import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.nocturna.performance.catalog.dto.HolleyProduct;
import com.nocturna.performance.catalog.dto.repository.HolleyProductRepository;
import com.nocturna.performance.config.HolleyProperties;
import com.nocturna.performance.config.SchedulerProperties;
import com.nocturna.performance.shopify.dto.ShopifyProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        // Loop products and translate, then fix format and store in shopify table
        for (HolleyProduct product : productsByBrand) {

            // Creating new ShopifyProduct with descriptions translated if any available
            var shopifyProduct = initializeShopifyProductAndTranslate(product, holleyProperties.getProjectid());
            // Adding upc - not null - no validation
            shopifyProduct.setUpc(product.getUpc());
            //Adding general attributes with validations
            if(product.getInvoiceDescription()!=null && !product.getInvoiceDescription().isBlank()){
                shopifyProduct.setInvoiceDescription(product.getInvoiceDescription());
            }


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
    /*public static Map<String, String> executeTranslation(String projectId, String targetLanguage, HolleyProduct product) {

        Map<String, String> translatedDesc = new HashMap<>();
        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(projectId, "global");
            TranslateTextResponse response = client.translateText(parent.toString(), targetLanguage, descriptions);
            // Store the translations for descriptions in map to be processed
            List<Translation> translationList = response.getTranslationsList();
            translatedDesc.put("shortDesc", translationList.get(0).getTranslatedText());
            translatedDesc.put("LongDesc", translationList.get(1).getTranslatedText());
            translatedDesc.put("marketDesc", translationList.get(2).getTranslatedText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return translatedDesc;
    }
*/
    /**
     * @param product
     * @param projectId
     * @return new empty product if no descriptions or error
     * product with translated descriptions if successful
     */
    public ShopifyProduct initializeShopifyProductAndTranslate(HolleyProduct product, String projectId) {
        List<String> contents = new ArrayList<>();
        List<String> types = new ArrayList<>();
        ShopifyProduct shopifyProduct = new ShopifyProduct();

        //Collect only non-empty descriptions and add flag to match
        if (product.getShortDescription() != null && !product.getShortDescription().isBlank()) {
            contents.add(product.getShortDescription());
            types.add("short");
        }
        if (product.getLongDescription() != null && !product.getLongDescription().isBlank()) {
            contents.add(product.getLongDescription());
            types.add("long");
        }
        if (product.getMarketingDescription() != null && !product.getMarketingDescription().isBlank()) {
            contents.add(product.getMarketingDescription());
            types.add("marketing");
        }

        // Build request only with valid contents
        TranslateTextRequest request = TranslateTextRequest.newBuilder()
                .setParent("projects/" + projectId + "/locations/global")
                .setSourceLanguageCode("en")
                .setTargetLanguageCode(holleyProperties.getLanguaje())
                .addAllContents(contents)
                .build();

        // Create client and execute translation
        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            TranslateTextResponse response = client.translateText(request);
            // Step 4: Map back translations to product fields
            int i = 0;
            for (Translation translation : response.getTranslationsList()) {
                String translated = translation.getTranslatedText();
                switch (types.get(i)) {
                    case "short" -> shopifyProduct.setShortDescription(translated);
                    case "long" -> shopifyProduct.setLongDescription(translated);
                    case "marketing" -> shopifyProduct.setMarketingDescription(translated);
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shopifyProduct;
    }

}
