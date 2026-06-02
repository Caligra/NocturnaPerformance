package com.nocturna.performance.startup;

import com.nocturna.performance.brands.dto.repository.HolleyBrandsRepository;
import com.nocturna.performance.catalog.service.CatalogService;
import com.nocturna.performance.config.properties.HolleyProperties;
import com.nocturna.performance.metafields.MetafieldDefinitionService;
import com.nocturna.performance.shopify.service.ProductService;
import com.nocturna.performance.translate.service.TranslateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NocturnaInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(NocturnaInitializer.class);

    private final CatalogService catalogService;
    private final MetafieldDefinitionService metafieldService;
    private final TranslateService translateService;
    private final HolleyBrandsRepository brandsRepository;
    private final HolleyProperties holleyProperties;
    private final ProductService productService;
    @Value("${shopify.loadMetafields}")
    private boolean enabled;

    public NocturnaInitializer(ProductService productService, CatalogService catalogService, HolleyBrandsRepository brandsRepository, HolleyProperties holleyProperties, MetafieldDefinitionService metafieldService, TranslateService translateService) {
        this.catalogService = catalogService;
        this.brandsRepository = brandsRepository;
        this.holleyProperties = holleyProperties;
        this.metafieldService = metafieldService;
        this.translateService = translateService;
        this.productService=productService;
    }


    /**
     * DONE:
     * private final BrandsService brandsService; //fetch all approved brands
     * private final ExternalCatalogService externalCatalogService; // downloads data
     * private final CatalogRepository catalogRepository;           // saves to MySQL
     * private final ShopifyMetafieldService metafieldService;      // step 1
     */
    /**
     * TODO:
     *
     * private final ShopifyCollectionService collectionService;    // step 2
     * private final ShopifyProductService productService;          // step 3
     * private final ShopifyCollectService collectService;          // step 4
     */

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // TODO service for new token ?


        if (enabled){
            logger.info("NocturnaInitializer:: Running metafield bootstrap.");
            metafieldService.bootstrapAll();
        }
        //productService.shopifyCreateProducts();
/*

        logger.info("NocturnaInitializer:: Fetching all authorized brands");
        List<HolleyBrand> approvedBrands = brandsRepository.findByApprovedTrue();
        logger.info("NocturnaInitializer:: # of authorized brands:: " + approvedBrands.size());

        logger.info("NocturnaInitializer:: Starting full catalogue download all brands");
        String exportBuildPlusTemplate = holleyProperties.getTemplate();
        */
/*for (HolleyBrand brand : approvedBrands) {
            logger.info("NocturnaInitializer:: Starting catalogue download for brand:: " + brand.getPdmInternalCode() + " Name:: " + brand.getBrandName());
            catalogService.fetchCatalogDataByBrand(exportBuildPlusTemplate, brand.getPdmInternalCode());
            logger.info("NocturnaInitializer:: Finished catalogue download for brand:: " + brand.getPdmInternalCode() + " Name:: " + brand.getBrandName());

            *//*
*/
/**
             * End goal - activate this code
             * Temporary fix - run a single brand for testing purposes (1)
             *//*
*/
/*
            //logger.info("NocturnaInitializer:: Triggering translate service for brand:: " + brand.getPdmInternalCode() + " Name:: " + brand.getBrandName());
            //translateService.translateProductItemDescriptions(brand.getPdmInternalCode());
            //logger.info("NocturnaInitializer:: Finished translate service for brand:: " + brand.getPdmInternalCode() + " Name:: " + brand.getBrandName());

        }*//*

        */
/**
         * (1) Temporary fix - run a single brand for testing purposes
         * To Be Deleted after test
         *//*

        logger.info("NocturnaInitializer:: Triggering TEST translate service for brand:: BDDP Name:: ACCEL");
        catalogService.fetchCatalogDataByBrand(exportBuildPlusTemplate, "BDDP");
        translateService.translateProductItemDescriptions("BDDP");
        logger.info("NocturnaInitializer:: Finished TEST translate service for brand:: BDDP Name:: ACCEL");


        logger.info("NocturnaInitializer:: Setting metafield definitions");
        //metafieldService.setupDefinitions();

*/

    }
}
