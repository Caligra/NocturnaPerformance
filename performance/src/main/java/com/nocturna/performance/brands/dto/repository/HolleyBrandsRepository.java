package com.nocturna.performance.brands.dto.repository;

import com.nocturna.performance.brands.dto.HolleyBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolleyBrandsRepository extends JpaRepository<HolleyBrand, String> {
    List<HolleyBrand> findByApprovedTrue();
}
