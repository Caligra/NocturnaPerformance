package com.nocturna.performance.catalog.dto.repository;

import com.nocturna.performance.catalog.dto.HolleyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolleyRepository extends JpaRepository<HolleyProduct, String> {
    List<HolleyProduct> findByBrand(String brandCode);
}
