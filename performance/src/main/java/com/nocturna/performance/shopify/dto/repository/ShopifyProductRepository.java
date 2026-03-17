package com.nocturna.performance.shopify.dto.repository;

import com.nocturna.performance.shopify.dto.ShopifyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyProductRepository extends JpaRepository<ShopifyProduct, String> {
}
