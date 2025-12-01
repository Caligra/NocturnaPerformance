package com.nocturna.performance.dto.exportproduct.repository;

import com.nocturna.performance.dto.exportproduct.ExportProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportProductRepository extends JpaRepository<ExportProduct, String> {
}