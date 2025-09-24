package com.nocturna.performance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocturna.performance.dto.Product;
import com.nocturna.performance.dto.Products;
import com.nocturna.performance.dto.repository.ProductEngRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import java.io.IOException;

@Service
public class CatalogService {
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    private static final String test_url = "https://api.pdm-automotive.com/api/v1";
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductEngRepository productEngRepository;

    private final RestClient restClient;

    public CatalogService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(test_url).build();
    }

    public String getBrandCatalog(String input) throws IOException {
        /*
         * Method to fetch catalog
         * */
        logger.info("milestone 1");
        //String consume = someRestCall("8401", "BDDP");
        someRestCall("21941", "8401", "BDDP");
        logger.info("milestone 2");
        //logger.info("CatalogService :: " + consume);
        return input + " yayy";
    }

    public void someRestCall(String template, String part, String brandCode) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("API-Token", "ANk55DO8mEptpNanZAWHGebZEfikECTW0QW33W7JSGCzqKvDPxGfy9u1qmgH98nC");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.pdm-automotive.com/api/v1/products/export_builder_plus?template_id={template}&part_number={part}&limit=1&brand_code={brandCode}",
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                }, template, part, brandCode);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Products responseList = objectMapper.readValue(response.getBody(),Products.class);
        logger.info("Consuming Consume Consumer:: " + responseList.getProducts().size());
        if(responseList.getProducts().size()>0){
            logger.info("Consuming Consume Consumer2:: " + responseList.getProducts().get(0).toString());
        }
        for (Product prod : responseList.getProducts()){
            productEngRepository.save(prod);
        }
        //translateText();

        //return printResponse;
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
