package com.nocturna.performance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.v3.*;
import com.nocturna.performance.dto.exportproduct.ExportProduct;
import com.nocturna.performance.dto.catalog.Product;
import com.nocturna.performance.dto.catalog.Products;
import com.nocturna.performance.dto.exportproduct.repository.ExportProductRepository;
import com.nocturna.performance.dto.catalog.repository.ProductEngRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatalogService {
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    private static final String catalogByBrandURL = "https://api.pdm-automotive.com/api/v1/products/export_builder_plus?template_id={template}&brand_code={brandCode}";
    private static final String ES_LANG = "es";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ProductEngRepository productEngRepository;

    @Autowired
    private ExportProductRepository exportProductID;

    public void getBrandCatalog(String input) throws IOException {
        /*
         * Method to fetch catalog
         * */
        //fetchCatalogDataByBrand("21941", "BDDP");
        translateProductItemDescriptions("BDDP");
    }

    public void fetchCatalogDataByBrand(String template, String brandCode) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("API-Token", "ANk55DO8mEptpNanZAWHGebZEfikECTW0QW33W7JSGCzqKvDPxGfy9u1qmgH98nC");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(catalogByBrandURL,
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                }, template, brandCode);
        ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Products responseList = objectMapper.readValue(response.getBody(), Products.class);
        logger.info("Consuming Consume Consumer:: " + responseList.getProducts().size());

        try {
            if (!responseList.getProducts().isEmpty()) {
                productEngRepository.saveAll(responseList.getProducts());
            }
        } catch (DataIntegrityViolationException ex) {
            ex.printStackTrace();
        }
    }

    public void translateProductItemDescriptions(String brand) throws IOException {
        String projectId = "nocturnatesttranslate";
        List<Product> productsByBrand = productEngRepository.findByBrand(brand);
        logger.info("productsByBrand.size():: " + productsByBrand.size());
        List<ExportProduct> allTranslatedProducts = new ArrayList<>();


        for (Product product : productsByBrand) {
            List<String> engDesc = new ArrayList<>();
            engDesc.add((product.getShort_description() == null || product.getShort_description().isEmpty()) ? "" : product.getShort_description());
            engDesc.add((product.getLong_description() == null || product.getLong_description().isEmpty()) ? "" : product.getLong_description());
            engDesc.add((product.getMarketing_description() == null || product.getMarketing_description().isEmpty()) ? "" : product.getMarketing_description());
            engDesc.add((product.getInvoice_description() == null || product.getInvoice_description().isEmpty()) ? "" : product.getInvoice_description());
            Map<String, String> translatedDesc = executeTranslation(projectId, ES_LANG, engDesc);
            System.out.println("Product upc:: " + product.getUpc());
            System.out.println("sd:: " + translatedDesc.get("shortDesc"));
            System.out.println("ld:: " + translatedDesc.get("LongDesc"));
            System.out.println("md:: " + translatedDesc.get("marketDesc"));
            System.out.println("id:: " + translatedDesc.get("invoiceDesc"));

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
            allTranslatedProducts.add(expProd);
        }
        if (!allTranslatedProducts.isEmpty()) {
            exportProductID.saveAll(allTranslatedProducts);
        }
        //executeTranslation(projectId, ES_LANG, "spark plug");
    }

    public static Map<String, String> executeTranslation(String projectId, String targetLanguage, List<String> descriptions)
            throws IOException {

        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(projectId, "global");
            TranslateTextResponse response = client.translateText(parent.toString(), targetLanguage, descriptions);
            // Display the translation for each input text provided
            Map<String, String> translatedDesc = new HashMap<>();
            List<Translation> translationList = response.getTranslationsList();
            translatedDesc.put("shortDesc", translationList.get(0).getTranslatedText());
            translatedDesc.put("LongDesc", translationList.get(1).getTranslatedText());
            translatedDesc.put("marketDesc", translationList.get(2).getTranslatedText());
            translatedDesc.put("invoiceDesc", translationList.get(3).getTranslatedText());
            /*for (Translation translation : response.getTranslationsList()) {
                System.out.printf("Translated text: %s\n", translation.getTranslatedText());
            }*/
            return translatedDesc;
        }
    }

}
