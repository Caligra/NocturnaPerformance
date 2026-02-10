package com.nocturna.performance.util;

import com.nocturna.performance.export.dto.ExportProduct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelFileExport {

    public static void exportProductsToExcel(List<ExportProduct> toExport) throws IOException {
        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");
        // Make columns 'text' type
        DataFormat fmt = workbook.createDataFormat();
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(fmt.getFormat("@"));
        sheet.setDefaultColumnStyle(0, textStyle);
        sheet.setDefaultColumnStyle(1, textStyle);
        sheet.setDefaultColumnStyle(2, textStyle);
        sheet.setDefaultColumnStyle(3, textStyle);
        sheet.setDefaultColumnStyle(4, textStyle);
        sheet.setDefaultColumnStyle(5, textStyle);
        sheet.setDefaultColumnStyle(6, textStyle);
        sheet.setDefaultColumnStyle(7, textStyle);
        sheet.setDefaultColumnStyle(8, textStyle);
        sheet.setDefaultColumnStyle(9, textStyle);
        sheet.setDefaultColumnStyle(10, textStyle);
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Category");
        headerRow.createCell(1).setCellValue("Brand");
        headerRow.createCell(2).setCellValue("Brand Name");
        headerRow.createCell(3).setCellValue("Name");
        headerRow.createCell(4).setCellValue("UPC");
        headerRow.createCell(5).setCellValue("SH desc");
        headerRow.createCell(6).setCellValue("IN desc");
        headerRow.createCell(7).setCellValue("MK desc");
        headerRow.createCell(8).setCellValue("Media");
        headerRow.createCell(9).setCellValue("LG desc");
        headerRow.createCell(10).setCellValue("LG desc");

        // Populate data rows
        int rowNum = 1;
        for (ExportProduct product : toExport) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getCategory());
            row.createCell(1).setCellValue(product.getBrand());
            row.createCell(2).setCellValue(product.getBrand_name());
            row.createCell(3).setCellValue(product.getName());
            row.createCell(4).setCellValue(product.getUpc());
            row.createCell(5).setCellValue(product.getShort_description());
            row.createCell(6).setCellValue(product.getInvoice_description());
            row.createCell(7).setCellValue(product.getMarketing_description());
            row.createCell(8).setCellValue(product.getMedia_url());
            row.createCell(9).setCellValue(product.getLong_description());
            row.createCell(10).setCellValue(product.getPart_number());
        }

        FileOutputStream fileOut = new FileOutputStream("C:/NocturnaExport/NocturnaExport.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}
