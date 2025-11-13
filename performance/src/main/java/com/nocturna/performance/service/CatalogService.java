package com.nocturna.performance.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.v3.*;
import com.nocturna.performance.dto.Products;
import com.nocturna.performance.dto.repository.ProductEngRepository;
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

@Service
public class CatalogService {
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    private static final String catalogByBrandURL = "https://api.pdm-automotive.com/api/v1/products/export_builder_plus?template_id={template}&brand_code={brandCode}";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ProductEngRepository productEngRepository;

    public void getBrandCatalog(String input) throws IOException {
        /*
         * Method to fetch catalog
         * */
        fetchCatalogDataByBrand("21941","BDDP");
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
        //translateText();
    }

    public static void translateText() throws IOException {
        String projectId = "nocturnatesttranslate";
        String targetLanguage = "es";
        String text = "Spark plug";
        translateText(projectId, targetLanguage, text);
    }

    public static void translateText(String projectId, String targetLanguage, String text)
            throws IOException {

        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(projectId, "global");

            TranslateTextRequest request =
                    TranslateTextRequest.newBuilder()
                            .setParent(parent.toString())
                            .setMimeType("text/plain")
                            .setTargetLanguageCode(targetLanguage)
                            .addContents(text)
                            .build();

            TranslateTextResponse response = client.translateText(request);

            // Display the translation for each input text provided
            for (Translation translation : response.getTranslationsList()) {
                System.out.printf("Translated text: %s\n", translation.getTranslatedText());
            }
        }
    }


}
