package com.nocturna.performance.catalog.dto.repository;

import com.nocturna.performance.catalog.dto.HolleyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface HolleyImagesRepository extends JpaRepository<HolleyImage, String> {
    // JPQL query: find all images by product UPC
    @Query("SELECT i FROM HolleyImage i WHERE i.product.upc = :upc")
    List<HolleyImage> findAllByProductUpc(@Param("upc") String upc);
}
