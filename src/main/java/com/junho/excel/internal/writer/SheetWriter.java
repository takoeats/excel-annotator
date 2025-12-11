package com.junho.excel.internal.writer;

import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.metadata.ExcelMetadata;
import com.junho.excel.internal.util.ColumnWidthCalculator;
import com.junho.excel.internal.util.SheetNameValidator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public final class SheetWriter {

  private static final int MAX_ROWS_PER_SHEET = 1000001;

  private final RowWriter rowWriter;

  public SheetWriter(RowWriter rowWriter) {
    this.rowWriter = rowWriter;
  }

  private <T> SXSSFSheet createConfiguredSheet(
      SXSSFWorkbook wb,
      String sheetName,
      ExcelMetadata<T> metadata,
      StyleCacheManager styleCacheManager) {

    SXSSFSheet sheet = wb.createSheet(sheetName);
    configureAutoSizeTracking(sheet, metadata);
    writeHeaderAndApplyWidths(sheet, metadata, styleCacheManager);
    return sheet;
  }

  private void configureAutoSizeTracking(
      SXSSFSheet sheet,
      ExcelMetadata<?> metadata) {

    for (int i = 0; i < metadata.getColumnWidths().size(); i++) {
      if (metadata.getColumnWidths().get(i) == -1) {
        sheet.trackColumnForAutoSizing(i);
      }
    }
  }

  private <T> void writeHeaderAndApplyWidths(
      SXSSFSheet sheet,
      ExcelMetadata<T> metadata,
      StyleCacheManager styleCacheManager) {

    rowWriter.createHeaderRow(sheet, metadata, styleCacheManager);
    ColumnWidthCalculator.applyFixedColumnWidths(sheet, metadata);
  }

  public <T> void write(SXSSFWorkbook wb, SheetWriteContext<T> context) {
    StyleCacheManager styleCacheManager = new StyleCacheManager(wb);

    if (context.isColumnBasedSplit()) {
      writeColumnBasedSheets(wb, context, styleCacheManager);
    } else {
      writeRowBasedSheets(wb, context, styleCacheManager);
    }
  }

  private void writeRowBasedSheets(
      SXSSFWorkbook wb,
      SheetWriteContext<?> context,
      StyleCacheManager styleCacheManager) {

    for (SheetWriteRequest<?> request : context.getSheetRequests()) {
      processSheetRequestMemoryEfficient(wb, request, styleCacheManager);
    }
  }

  private <T> void writeColumnBasedSheets(
      SXSSFWorkbook wb,
      SheetWriteContext<T> context,
      StyleCacheManager styleCacheManager) {

    Iterator<T> dataIterator = context.getColumnDataIterator();
    List<ExcelMetadata<T>> metadataList = context.getColumnMetadataList();

    Map<String, SheetContext> sheetContexts = initializeSheetContexts(
        wb, metadataList, styleCacheManager);

    writeDataToColumnSheets(dataIterator, metadataList, sheetContexts, styleCacheManager);
    applyAutoWidthToSheets(wb, metadataList, sheetContexts);
  }

  private <T> Map<String, SheetContext> initializeSheetContexts(
      SXSSFWorkbook wb,
      List<ExcelMetadata<T>> metadataList,
      StyleCacheManager styleCacheManager) {

    Map<String, SheetContext> sheetContexts = new java.util.HashMap<>();

    for (ExcelMetadata<T> metadata : metadataList) {
      String sheetName = metadata.getSheetName();
      SXSSFSheet sheet = createConfiguredSheet(wb, sheetName, metadata, styleCacheManager);
      sheetContexts.put(sheetName, new SheetContext(sheet, metadata, 0, 0));
    }

    return sheetContexts;
  }

  private <T> void writeDataToColumnSheets(
      Iterator<T> dataIterator,
      List<ExcelMetadata<T>> metadataList,
      Map<String, SheetContext> sheetContexts,
      StyleCacheManager styleCacheManager) {

    while (dataIterator.hasNext()) {
      T item = dataIterator.next();

      for (ExcelMetadata<T> metadata : metadataList) {
        String baseSheetName = metadata.getSheetName();
        SheetContext sheetContext = sheetContexts.get(baseSheetName);

        int headerRows = metadata.hasHeader() ? 1 : 0;
        int maxDataRowsPerSheet = MAX_ROWS_PER_SHEET - headerRows;

        if (sheetContext.currentRowInSheet >= maxDataRowsPerSheet) {
          createNewSheetForContext(sheetContext, baseSheetName, metadata, styleCacheManager);
        }

        int rowIndex = sheetContext.currentRowInSheet + headerRows;
        Row row = sheetContext.sheet.createRow(rowIndex);
        rowWriter.writeDataRow(row, item, sheetContext.currentRowInSheet, metadata, styleCacheManager);

        sheetContext.currentRowInSheet++;
      }
    }
  }

  private <T> void createNewSheetForContext(
      SheetContext sheetContext,
      String baseSheetName,
      ExcelMetadata<T> metadata,
      StyleCacheManager styleCacheManager) {

    sheetContext.sheetIndex++;
    String actualSheetName = buildSheetName(baseSheetName, sheetContext.sheetIndex);

    sheetContext.sheet = createConfiguredSheet(
        sheetContext.sheet.getWorkbook(), actualSheetName, metadata, styleCacheManager);
    sheetContext.currentRowInSheet = 0;
  }

  private <T> void applyAutoWidthToSheets(
      SXSSFWorkbook wb,
      List<ExcelMetadata<T>> metadataList,
      Map<String, SheetContext> sheetContexts) {

    for (ExcelMetadata<T> metadata : metadataList) {
      String baseSheetName = metadata.getSheetName();
      SheetContext sheetContext = sheetContexts.get(baseSheetName);

      for (int i = 0; i <= sheetContext.sheetIndex; i++) {
        String actualSheetName = buildSheetName(baseSheetName, i);
        SXSSFSheet sheet = wb.getSheet(actualSheetName);
        if (sheet != null) {
          ColumnWidthCalculator.applyAutoWidthColumns(sheet, metadata);
        }
      }
    }
  }


  private String buildSheetName(String baseSheetName, int sheetIndex) {
    String sheetName = sheetIndex == 0
        ? baseSheetName
        : baseSheetName + (sheetIndex + 1);
    return SheetNameValidator.validateAndSanitize(sheetName);
  }

  private <T> void processSheetRequestMemoryEfficient(
      SXSSFWorkbook wb,
      SheetWriteRequest<T> request,
      StyleCacheManager styleCacheManager) {

    ExcelMetadata<T> metadata = request.getMetadata();
    Iterator<T> dataIterator = request.getDataIterator();

    String baseSheetName = metadata.getSheetName();
    int headerRows = metadata.hasHeader() ? 1 : 0;
    int maxDataRowsPerSheet = MAX_ROWS_PER_SHEET - headerRows;

    int sheetIndex = 0;
    SXSSFSheet currentSheet = null;
    int currentRowInSheet = 0;

    while (dataIterator.hasNext()) {
      if (currentSheet == null || currentRowInSheet >= maxDataRowsPerSheet) {
        String sanitizedName = buildSheetName(baseSheetName, sheetIndex);
        currentSheet = createConfiguredSheet(wb, sanitizedName, metadata, styleCacheManager);
        currentRowInSheet = 0;
        sheetIndex++;
      }

      T item = dataIterator.next();
      int rowIndex = currentRowInSheet + headerRows;
      Row row = currentSheet.createRow(rowIndex);
      rowWriter.writeDataRow(row, item, currentRowInSheet, metadata, styleCacheManager);

      currentRowInSheet++;
    }

    applyAutoWidthToRowBasedSheets(wb, baseSheetName, sheetIndex, metadata);
  }

  private <T> void applyAutoWidthToRowBasedSheets(
      SXSSFWorkbook wb,
      String baseSheetName,
      int totalSheets,
      ExcelMetadata<T> metadata) {

    for (int i = 0; i < totalSheets; i++) {
      String sanitizedName = buildSheetName(baseSheetName, i);
      SXSSFSheet sheet = wb.getSheet(sanitizedName);
      if (sheet != null) {
        ColumnWidthCalculator.applyAutoWidthColumns(sheet, metadata);
      }
    }
  }

  private static final class SheetContext {
    SXSSFSheet sheet;
    final ExcelMetadata<?> metadata;
    int sheetIndex;
    int currentRowInSheet;

    SheetContext(SXSSFSheet sheet, ExcelMetadata<?> metadata, int sheetIndex, int currentRowInSheet) {
      this.sheet = sheet;
      this.metadata = metadata;
      this.sheetIndex = sheetIndex;
      this.currentRowInSheet = currentRowInSheet;
    }
  }

}
