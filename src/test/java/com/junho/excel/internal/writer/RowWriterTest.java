package com.junho.excel.internal.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.metadata.ExcelMetadata;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RowWriterTest {

  private SXSSFWorkbook workbook;
  private Sheet sheet;
  private RowWriter rowWriter;
  private StyleCacheManager styleCacheManager;

  @BeforeEach
  void setUp() {
    workbook = new SXSSFWorkbook();
    sheet = workbook.createSheet("Test");
    rowWriter = new RowWriter();
    styleCacheManager = new StyleCacheManager(workbook);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (workbook != null) {
      workbook.close();
    }
  }

  @Test
  void createHeaderRow_withMetadata_createsHeader() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);

    rowWriter.createHeaderRow(sheet, metadata, styleCacheManager);

    Row header = sheet.getRow(0);
    assertNotNull(header);

    DataFormatter fmt = new DataFormatter();
    assertEquals("Name", fmt.formatCellValue(header.getCell(0)));
    assertEquals("Age", fmt.formatCellValue(header.getCell(1)));
    assertEquals("Salary", fmt.formatCellValue(header.getCell(2)));
  }

  @Test
  void createHeaderRow_withNoHeaderMetadata_doesNotCreateRow() {
    ExcelMetadata<NoHeaderDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        NoHeaderDTO.class);

    rowWriter.createHeaderRow(sheet, metadata, styleCacheManager);

    assertNull(sheet.getRow(0));
  }

  @Test
  void createHeaderRow_multipleColumns_createsAllHeaders() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);

    rowWriter.createHeaderRow(sheet, metadata, styleCacheManager);

    Row header = sheet.getRow(0);
    assertEquals(3, metadata.getHeaders().size());
    assertNotNull(header.getCell(0));
    assertNotNull(header.getCell(1));
    assertNotNull(header.getCell(2));
  }

  @Test
  void writeDataRow_withValidData_writesAllCells() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
    TestDTO data = new TestDTO("Alice", 30, new BigDecimal("5000.50"));
    Row row = sheet.createRow(1);

    rowWriter.writeDataRow(row, data, 0, metadata, styleCacheManager);

    DataFormatter fmt = new DataFormatter();
    assertEquals("Alice", fmt.formatCellValue(row.getCell(0)));
    assertEquals("30", fmt.formatCellValue(row.getCell(1)));
    assertEquals("5,000.50", fmt.formatCellValue(row.getCell(2)));
  }

  @Test
  void writeDataRow_withNullValues_handlesSafely() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
    TestDTO data = new TestDTO(null, null, null);
    Row row = sheet.createRow(1);

    rowWriter.writeDataRow(row, data, 0, metadata, styleCacheManager);

    assertNotNull(row.getCell(0));
    assertNotNull(row.getCell(1));
    assertNotNull(row.getCell(2));
  }

  @Test
  void writeDataRow_multipleRows_maintainsIndependence() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);

    TestDTO data1 = new TestDTO("Alice", 25, new BigDecimal("3000"));
    TestDTO data2 = new TestDTO("Bob", 35, new BigDecimal("4000"));
    TestDTO data3 = new TestDTO("Charlie", 45, new BigDecimal("5000"));

    Row row1 = sheet.createRow(1);
    Row row2 = sheet.createRow(2);
    Row row3 = sheet.createRow(3);

    rowWriter.writeDataRow(row1, data1, 0, metadata, styleCacheManager);
    rowWriter.writeDataRow(row2, data2, 1, metadata, styleCacheManager);
    rowWriter.writeDataRow(row3, data3, 2, metadata, styleCacheManager);

    DataFormatter fmt = new DataFormatter();
    assertEquals("Alice", fmt.formatCellValue(row1.getCell(0)));
    assertEquals("Bob", fmt.formatCellValue(row2.getCell(0)));
    assertEquals("Charlie", fmt.formatCellValue(row3.getCell(0)));
  }

  @Test
  void writeDataRow_withDataRowIndex_passesIndexCorrectly() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
    TestDTO data = new TestDTO("Test", 20, new BigDecimal("1000"));
    Row row = sheet.createRow(5);

    rowWriter.writeDataRow(row, data, 4, metadata, styleCacheManager);

    assertNotNull(row.getCell(0));
    DataFormatter fmt = new DataFormatter();
    assertEquals("Test", fmt.formatCellValue(row.getCell(0)));
  }

  @Test
  void createHeaderRow_calledTwice_doesNotDuplicate() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);

    rowWriter.createHeaderRow(sheet, metadata, styleCacheManager);
    Row firstHeader = sheet.getRow(0);

    Row manualRow = sheet.createRow(0);
    rowWriter.createHeaderRow(sheet, metadata, styleCacheManager);

    assertNotNull(sheet.getRow(0));
    assertEquals("Name", firstHeader.getCell(0).getStringCellValue());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Test")
  public static class TestDTO {

    @ExcelColumn(header = "Name", order = 1)
    private String name;

    @ExcelColumn(header = "Age", order = 2)
    private Integer age;

    @ExcelColumn(header = "Salary", order = 3, format = "#,##0.00")
    private BigDecimal salary;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet(value = "NoHeader", hasHeader = false)
  public static class NoHeaderDTO {

    @ExcelColumn(header = "Data", order = 1)
    private String data;
  }
}
