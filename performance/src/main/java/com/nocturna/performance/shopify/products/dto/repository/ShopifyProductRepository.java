package com.nocturna.performance.shopify.products.dto.repository;

import com.nocturna.performance.shopify.products.dto.ShopifyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyProductRepository extends JpaRepository<ShopifyProduct, String> {
}

@Repository
public interface ShopifyProductStagingRepository extends JpaRepository<ShopifyProductStaging, String> {

    List<ShopifyProductStaging> findBySyncStatus(SyncStatus status);
}