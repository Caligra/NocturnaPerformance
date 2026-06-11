package com.nocturna.performance.startup;

import com.nocturna.performance.brands.dto.HolleyBrand;
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

import java.util.List;

@Component
public class NocturnaInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(NocturnaInitializer.class);

    private final CatalogService catalogService;
    private final MetafieldDefinitionService metafieldService;
    private final TranslateService translateService;
    private final HolleyBrandsRepository brandsRepository;
    private final HolleyProperties holleyProperties;
    private final ProductService productService;
    @Value("${shopify.initialRun}")
    private boolean initialRun;

    public NocturnaInitializer(ProductService productService, CatalogService catalogService, HolleyBrandsRepository brandsRepository, HolleyProperties holleyProperties, MetafieldDefinitionService metafieldService, TranslateService translateService) {
        this.catalogService = catalogService;
        this.brandsRepository = brandsRepository;
        this.holleyProperties = holleyProperties;
        this.metafieldService = metafieldService;
        this.translateService = translateService;
        this.productService = productService;
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
     * <p>
     * private final ShopifyCollectionService collectionService;    // step 2
     * private final ShopifyProductService productService;          // step 3
     * private final ShopifyCollectService collectService;          // step 4
     */

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("NocturnaInitializer::run():: Executing initial run :: " + initialRun);
        if (initialRun) {
            logger.info("NocturnaInitializer::run():: Executing initial metafield setup");
            metafieldService.bootstrapAll();

            logger.info("NocturnaInitializer::run():: Fetching all authorized brands");
            List<HolleyBrand> approvedBrands = brandsRepository.findByApprovedTrue();
            logger.info("NocturnaInitializer::run():: # of authorized brands:: " + approvedBrands.size());

            //todo productService.shopifyCreateProducts();
            logger.info("NocturnaInitializer::run():: Starting full catalogue download all brands");
            String exportBuildPlusTemplate = holleyProperties.getTemplate();
            for (HolleyBrand brand : approvedBrands) {
                logger.info("NocturnaInitializer::run():: Starting catalogue download for brand:: " + brand.getPdmInternalCode() + " Name:: " + brand.getBrandName());
                catalogService.fetchCatalogDataByBrand(exportBuildPlusTemplate, brand.getPdmInternalCode());
                logger.info("NocturnaInitializer::run():: Finished catalogue download for brand:: " + brand.getPdmInternalCode() + " Name:: " + brand.getBrandName());

                /**
                 * End goal - activate this code
                 * Temporary fix - run a single brand for testing purposes (1)
                 */
                //logger.info("NocturnaInitializer:: Triggering translate service for brand:: " + brand.getPdmInternalCode() + " Name:: " + brand.getBrandName());
                //translateService.translateProductItemDescriptions(brand.getPdmInternalCode());
                //logger.info("NocturnaInitializer:: Finished translate service for brand:: " + brand.getPdmInternalCode() + " Name:: " + brand.getBrandName());
            }
            logger.info("NocturnaInitializer::run():: Finished");


            /**
             * (1) Temporary fix - run a single brand for testing purposes
             * To Be Deleted after test
             */
            /*try {
                logger.info("NocturnaInitializer:: Triggering TEST translate service for brand:: BBVM Name:: BBVM");
                catalogService.fetchCatalogDataByBrand(exportBuildPlusTemplate, "BBVM");
                //translateService.translateProductItemDescriptions("BDDP");
                logger.info("NocturnaInitializer:: Finished TEST translate service for brand:: BDDP Name:: ACCEL");
            }catch (Exception e){
                e.printStackTrace();
            }*/
        }
    }
}
