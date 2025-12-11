package com.junho.excel.internal.writer;

import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.metadata.ExcelMetadata;
import com.junho.excel.style.rule.CellContext;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class RowWriter {

  private final CellWriter cellWriter;

  public RowWriter() {
    this.cellWriter = new CellWriter();
  }

  <T> void writeDataRow(
      Row row,
      T item,
      int dataRowIndex,
      ExcelMetadata<T> metadata,
      StyleCacheManager styleCacheManager) {

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, item, dataRowIndex, metadata, cellContext, styleCacheManager);
    }
  }

  <T> void createHeaderRow(
      Sheet sheet,
      ExcelMetadata<T> metadata,
      StyleCacheManager styleCacheManager) {

    if (!metadata.hasHeader()) {
      return;
    }

    Row header = sheet.createRow(0);
    for (int i = 0; i < metadata.getHeaders().size(); i++) {
      cellWriter.configureHeaderCell(header, i, metadata, styleCacheManager);
    }
  }
}
