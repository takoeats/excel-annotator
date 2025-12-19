package com.junho.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.exception.ExcelExporterException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

class MultiSheetEdgeCaseTest {

  @Test
  void multiSheet_emptyListInMap_throwsException() {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("Sheet1", Collections.emptyList());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    assertThrows(ExcelExporterException.class,
        () -> ExcelExporter.excelFromList(baos, "test.xlsx", sheetData));
  }

  @Test
  void multiSheet_nullListInMap_throwsException() {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("Sheet1", null);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    assertThrows(ExcelExporterException.class,
        () -> ExcelExporter.excelFromList(baos, "test.xlsx", sheetData));
  }

  @Test
  void multiSheet_emptyMapData_throwsException() {
    Map<String, List<?>> emptyMap = new LinkedHashMap<>();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    assertThrows(ExcelExporterException.class,
        () -> ExcelExporter.excelFromList(baos, "test.xlsx", emptyMap));
  }

  @Test
  void multiSheet_singleSheetMultipleDTOs_consolidatesCorrectly() throws Exception {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("key1", Collections.singletonList(new SameSheetDTO1("A1", "A2")));
    sheetData.put("key2", Collections.singletonList(new SameSheetDTO2("B1", "B2")));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String fileName = ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

    assertNotNull(fileName);
    assertEquals("test.xlsx", fileName);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(1, wb.getNumberOfSheets());
    assertEquals("통합시트", wb
        .getSheetAt(0)
        .getSheetName());
    wb.close();
  }

  @Test
  void multiSheet_differentSheetNames_createsMultipleSheets() throws Exception {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("key1", Collections.singletonList(new DifferentSheet1DTO("X")));
    sheetData.put("key2", Collections.singletonList(new DifferentSheet2DTO("Y")));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(2, wb.getNumberOfSheets());

    Set<String> sheetNames = new HashSet<>(Arrays.asList(
        wb
            .getSheetAt(0)
            .getSheetName(),
        wb
            .getSheetAt(1)
            .getSheetName()
    ));
    assertTrue(sheetNames.contains("시트A"));
    assertTrue(sheetNames.contains("시트B"));
    wb.close();
  }

  @Test
  void multiSheet_columnLevelSheetAssignment_autoCreatesSheets() throws Exception {
    List<ColumnSheetDTO> data = Collections.singletonList(
        new ColumnSheetDTO("Col1", "Col2")
    );

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExcelExporter.excelFromList(baos, "test.xlsx", data);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(2, wb.getNumberOfSheets());

    Set<String> sheetNames = new HashSet<>(Arrays.asList(
        wb
            .getSheetAt(0)
            .getSheetName(),
        wb
            .getSheetAt(1)
            .getSheetName()
    ));
    assertTrue(sheetNames.contains("컬럼시트A"));
    assertTrue(sheetNames.contains("컬럼시트B"));
    wb.close();
  }

  @Test
  void multiSheet_mixedSheetAssignment_handlesCorrectly() throws Exception {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("mixed", Collections.singletonList(new ColumnSheetDTO("X", "Y")));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(2, wb.getNumberOfSheets());
    wb.close();
  }

  @Test
  void multiSheet_veryLongSheetName_truncatesCorrectly() throws Exception {
    List<VeryLongSheetNameDTO> data = Collections.singletonList(
        new VeryLongSheetNameDTO("test")
    );

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExcelExporter.excelFromList(baos, "test.xlsx", data);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    Sheet sheet = wb.getSheetAt(0);

    assertTrue(sheet
        .getSheetName()
        .length() <= 31);
    wb.close();
  }

  @Test
  void multiSheet_specialCharactersInSheetName_sanitizesCorrectly() throws Exception {
    List<SpecialCharSheetDTO> data = Collections.singletonList(
        new SpecialCharSheetDTO("test")
    );

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExcelExporter.excelFromList(baos, "test.xlsx", data);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    Sheet sheet = wb.getSheetAt(0);

    String sheetName = sheet.getSheetName();
    assertFalse(sheetName.contains("["));
    assertFalse(sheetName.contains("]"));
    assertFalse(sheetName.contains("*"));
    assertFalse(sheetName.contains("?"));
    wb.close();
  }

  @Test
  void multiSheet_duplicateSheetNames_handlesCorrectly() throws Exception {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("dup1", Collections.singletonList(new SameSheetDTO1("A", "B")));
    sheetData.put("dup2", Collections.singletonList(new SameSheetDTO2("C", "D")));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(1, wb.getNumberOfSheets());
    assertEquals(2, wb
        .getSheetAt(0)
        .getPhysicalNumberOfRows());

    Sheet sheet = wb.getSheetAt(0);
    Row headerRow = sheet.getRow(0);
    assertEquals(4, headerRow.getPhysicalNumberOfCells());

    DataFormatter formatter = new DataFormatter();
    assertEquals("필드A1", formatter.formatCellValue(headerRow.getCell(0)));
    assertEquals("필드A2", formatter.formatCellValue(headerRow.getCell(1)));
    assertEquals("필드B1", formatter.formatCellValue(headerRow.getCell(2)));
    assertEquals("필드B2", formatter.formatCellValue(headerRow.getCell(3)));

    Row dataRow = sheet.getRow(1);
    assertEquals("A", formatter.formatCellValue(dataRow.getCell(0)));
    assertEquals("B", formatter.formatCellValue(dataRow.getCell(1)));
    assertEquals("C", formatter.formatCellValue(dataRow.getCell(2)));
    assertEquals("D", formatter.formatCellValue(dataRow.getCell(3)));

    wb.close();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("통합시트")
  public static class SameSheetDTO1 {

    @ExcelColumn(header = "필드A1", order = 1)
    private String fieldA1;

    @ExcelColumn(header = "필드A2", order = 2)
    private String fieldA2;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("통합시트")
  public static class SameSheetDTO2 {

    @ExcelColumn(header = "필드B1", order = 3)
    private String fieldB1;

    @ExcelColumn(header = "필드B2", order = 4)
    private String fieldB2;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("시트A")
  public static class DifferentSheet1DTO {

    @ExcelColumn(header = "컬럼X", order = 1)
    private String columnX;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("시트B")
  public static class DifferentSheet2DTO {

    @ExcelColumn(header = "컬럼Y", order = 1)
    private String columnY;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("클래스시트")
  public static class ColumnSheetDTO {

    @ExcelColumn(header = "컬럼A", order = 1, sheetName = "컬럼시트A")
    private String columnA;

    @ExcelColumn(header = "컬럼B", order = 2, sheetName = "컬럼시트B")
    private String columnB;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("이것은매우긴시트이름입니다이것은매우긴시트이름입니다이것은매우긴시트이름입니다")
  public static class VeryLongSheetNameDTO {

    @ExcelColumn(header = "데이터", order = 1)
    private String data;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("시트*이름[특수]문자?테스트")
  public static class SpecialCharSheetDTO {

    @ExcelColumn(header = "데이터", order = 1)
    private String data;
  }
}
