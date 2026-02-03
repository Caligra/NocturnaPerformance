package com.nocturna.performance.service;


import com.nocturna.performance.dto.exportproduct.ExportProduct;
import com.nocturna.performance.dto.exportproduct.repository.ExportProductRepository;
import com.nocturna.performance.util.ExcelFileExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExportService {
    @Autowired
    private ExportProductRepository exportProductRepository;
    public void exportExcelProducts() throws IOException {
        List<ExportProduct> allExport = exportProductRepository.findAll();
        ExcelFileExport.exportProductsToExcel(allExport);
    }


}
