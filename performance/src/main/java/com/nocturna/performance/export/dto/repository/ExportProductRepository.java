package com.nocturna.performance.export.dto.repository;

import com.nocturna.performance.export.dto.ExportProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportProductRepository extends JpaRepository<ExportProduct, String> {
}