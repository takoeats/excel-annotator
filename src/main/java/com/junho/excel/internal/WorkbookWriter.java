package com.junho.excel.internal;

import com.junho.excel.exception.ExcelExporterException;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

@FunctionalInterface
public interface WorkbookWriter {

    void write(SXSSFWorkbook wb) throws ExcelExporterException;
}
