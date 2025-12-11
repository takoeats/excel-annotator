package com.junho.excel.internal.writer;

import com.junho.excel.internal.metadata.ExcelMetadata;
import com.junho.excel.internal.util.CellValueConverter;
import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.StyleCache;
import com.junho.excel.style.rule.CellContext;
import com.junho.excel.style.rule.StyleRule;
import java.util.List;
import java.util.function.Function;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class CellWriter {

  private static final String DEFAULT_FORMAT = "General";

  private <T> void writeCell(
      Cell cell,
      Object value,
      ExcelMetadata<T> metadata,
      int columnIndex,
      StyleCacheManager styleCacheManager) {

    CellValueConverter.setCellValueSafely(cell, value);

    CustomExcelCellStyle defaultStyle = metadata.getColumnStyleAt(columnIndex);
    Class<? extends CustomExcelCellStyle> styleClass = defaultStyle != null
        ? defaultStyle.getClass()
        : null;

    String format = determineFormat(metadata, styleClass, columnIndex);
    CellStyle poiStyle = styleCacheManager.getOrCreateStyle(styleClass, format);
    cell.setCellStyle(poiStyle);
  }

  private <T> void writeCellWithConditionalStyle(
      Cell cell,
      Object value,
      ExcelMetadata<T> metadata,
      int columnIndex,
      T dataItem,
      int dataRowIndex,
      CellContext cellContext,
      StyleCacheManager styleCacheManager) {

    CellValueConverter.setCellValueSafely(cell, value);

    Class<? extends CustomExcelCellStyle> styleClass =
        determineStyleClass(metadata, columnIndex, value, dataItem, dataRowIndex, cellContext);

    String format = determineFormat(metadata, styleClass, columnIndex);
    CellStyle poiStyle = styleCacheManager.getOrCreateStyle(styleClass, format);
    cell.setCellStyle(poiStyle);
  }

  <T> void configureHeaderCell(
      Row header,
      int columnIndex,
      ExcelMetadata<T> metadata,
      StyleCacheManager styleCacheManager) {

    Cell cell = header.createCell(columnIndex);
    cell.setCellValue(metadata.getHeaders().get(columnIndex));

    CustomExcelCellStyle headerStyle = metadata.getHeaderStyleAt(columnIndex);
    Class<? extends CustomExcelCellStyle> styleClass = headerStyle != null
        ? headerStyle.getClass()
        : null;

    CellStyle poiStyle = styleCacheManager.getOrCreateStyle(styleClass, null);
    cell.setCellStyle(poiStyle);
  }

  private <T> Class<? extends CustomExcelCellStyle> determineStyleClass(
      ExcelMetadata<T> metadata,
      int columnIndex,
      Object value,
      T dataItem,
      int dataRowIndex,
      CellContext cellContext) {

    List<StyleRule> conditionalRules = metadata.getConditionalStyleRulesAt(columnIndex);

    if (!conditionalRules.isEmpty()) {
      cellContext.update(
          value,
          dataItem,
          columnIndex,
          dataRowIndex,
          metadata.getFieldNameAt(columnIndex)
      );

      for (StyleRule rule : conditionalRules) {
        if (rule.evaluate(cellContext)) {
          return rule.getStyleClass();
        }
      }
    }

    CustomExcelCellStyle defaultStyle = metadata.getColumnStyleAt(columnIndex);
    return defaultStyle != null ? defaultStyle.getClass() : null;
  }

  private String determineFormat(
      ExcelMetadata<?> metadata,
      Class<? extends CustomExcelCellStyle> styleClass,
      int columnIndex) {

    String annotationFormat = metadata.getFormatAt(columnIndex);
    if (annotationFormat != null && !annotationFormat.trim().isEmpty()) {
      return annotationFormat;
    }

    if (styleClass != null) {
      CustomExcelCellStyle styleInstance = StyleCache.getStyleInstance(styleClass);
      String styleFormat = styleInstance.getDataFormat();
      if (styleFormat != null && !styleFormat.equals(DEFAULT_FORMAT)) {
        return styleFormat;
      }
    }

    return null;
  }

  <T> void writeCells(
      Row row,
      T item,
      int dataRowIndex,
      ExcelMetadata<T> metadata,
      CellContext cellContext,
      StyleCacheManager styleCacheManager) {

    List<Function<T, Object>> extractors = metadata.getExtractors();

    for (int colIndex = 0; colIndex < extractors.size(); colIndex++) {
      Function<T, Object> extractor = extractors.get(colIndex);
      Object value = extractor.apply(item);
      Cell cell = row.createCell(colIndex);

      List<StyleRule> conditionalRules = metadata.getConditionalStyleRulesAt(colIndex);

      if (!conditionalRules.isEmpty()) {
        writeCellWithConditionalStyle(cell, value, metadata, colIndex, item,
            dataRowIndex, cellContext, styleCacheManager);
      } else {
        writeCell(cell, value, metadata, colIndex, styleCacheManager);
      }
    }
  }
}
