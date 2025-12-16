package com.junho.excel.internal.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.junho.excel.annotation.ConditionalStyle;
import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.metadata.ExcelMetadata;
import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.ExcelColors;
import com.junho.excel.style.FontStyle;
import com.junho.excel.style.rule.CellContext;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CellWriterTest {

  private SXSSFWorkbook workbook;
  private Sheet sheet;
  private CellWriter cellWriter;
  private StyleCacheManager styleCacheManager;

  @BeforeEach
  void setUp() {
    workbook = new SXSSFWorkbook();
    sheet = workbook.createSheet("Test");
    cellWriter = new CellWriter();
    styleCacheManager = new StyleCacheManager(workbook);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (workbook != null) {
      workbook.close();
    }
  }

  @Test
  void configureHeaderCell_withCustomHeaderStyle_appliesStyle() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
    Row header = sheet.createRow(0);

    cellWriter.configureHeaderCell(header, 0, metadata, styleCacheManager);

    Cell cell = header.getCell(0);
    assertNotNull(cell);
    assertEquals("Name", cell.getStringCellValue());
    assertNotNull(cell.getCellStyle());
  }

  @Test
  void configureHeaderCell_multipleColumns_createsAllHeaders() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
    Row header = sheet.createRow(0);

    cellWriter.configureHeaderCell(header, 0, metadata, styleCacheManager);
    cellWriter.configureHeaderCell(header, 1, metadata, styleCacheManager);
    cellWriter.configureHeaderCell(header, 2, metadata, styleCacheManager);

    DataFormatter fmt = new DataFormatter();
    assertEquals("Name", fmt.formatCellValue(header.getCell(0)));
    assertEquals("Age", fmt.formatCellValue(header.getCell(1)));
    assertEquals("Salary", fmt.formatCellValue(header.getCell(2)));
  }

  @Test
  void writeCells_withBasicData_writesCellsCorrectly() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
    TestDTO data = new TestDTO("John", 30, new BigDecimal("5000.50"));
    Row row = sheet.createRow(1);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, data, 0, metadata, cellContext, styleCacheManager);
    }

    DataFormatter fmt = new DataFormatter();
    assertEquals("John", fmt.formatCellValue(row.getCell(0)));
    assertEquals("30", fmt.formatCellValue(row.getCell(1)));
    assertEquals("5,000.50", fmt.formatCellValue(row.getCell(2)));
  }

  @Test
  void writeCells_withNullValues_handlesGracefully() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
    TestDTO data = new TestDTO(null, null, null);
    Row row = sheet.createRow(1);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, data, 0, metadata, cellContext, styleCacheManager);
    }

    assertNotNull(row.getCell(0));
    assertNotNull(row.getCell(1));
    assertNotNull(row.getCell(2));
  }

  @Test
  void writeCells_withConditionalStyle_appliesWhenConditionMet() {
    ExcelMetadata<ConditionalDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        ConditionalDTO.class);
    ConditionalDTO data = new ConditionalDTO(-100);
    Row row = sheet.createRow(1);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, data, 0, metadata, cellContext, styleCacheManager);
    }

    Cell cell = row.getCell(0);
    assertNotNull(cell);
    assertNotNull(cell.getCellStyle());
  }

  @Test
  void writeCells_withConditionalStyleNotMet_appliesDefaultStyle() {
    ExcelMetadata<ConditionalDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        ConditionalDTO.class);
    ConditionalDTO data = new ConditionalDTO(100);
    Row row = sheet.createRow(1);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, data, 0, metadata, cellContext, styleCacheManager);
    }

    Cell cell = row.getCell(0);
    assertNotNull(cell);
    assertNotNull(cell.getCellStyle());
  }

  @Test
  void writeCells_withCustomFormat_appliesFormat() {
    ExcelMetadata<FormattedDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        FormattedDTO.class);
    FormattedDTO data = new FormattedDTO(new BigDecimal("1234.56"));
    Row row = sheet.createRow(1);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, data, 0, metadata, cellContext, styleCacheManager);
    }

    DataFormatter fmt = new DataFormatter();
    Cell cell = row.getCell(0);
    assertNotNull(cell);
    assertEquals("1,234.56", fmt.formatCellValue(cell));
  }

  @Test
  void writeCells_multipleRows_maintainsConsistency() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);

    TestDTO data1 = new TestDTO("Alice", 25, new BigDecimal("3000"));
    TestDTO data2 = new TestDTO("Bob", 35, new BigDecimal("4000"));

    Row row1 = sheet.createRow(1);
    Row row2 = sheet.createRow(2);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row1, data1, 0, metadata, cellContext, styleCacheManager);
      cellWriter.writeCells(row2, data2, 1, metadata, cellContext, styleCacheManager);
    }

    DataFormatter fmt = new DataFormatter();
    assertEquals("Alice", fmt.formatCellValue(row1.getCell(0)));
    assertEquals("Bob", fmt.formatCellValue(row2.getCell(0)));
  }

  @Test
  void writeCells_annotationFormatOverridesStyleFormat() {
    ExcelMetadata<FormatPriorityDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        FormatPriorityDTO.class);
    FormatPriorityDTO data = new FormatPriorityDTO(new BigDecimal("1234.5"));
    Row row = sheet.createRow(1);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, data, 0, metadata, cellContext, styleCacheManager);
    }

    DataFormatter fmt = new DataFormatter();
    assertEquals("1,234.50", fmt.formatCellValue(row.getCell(0)));
  }

  @Test
  void writeCells_multipleConditionalRules_selectsHighestPriority() {
    ExcelMetadata<MultiConditionalDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        MultiConditionalDTO.class);
    MultiConditionalDTO data = new MultiConditionalDTO(45);
    Row row = sheet.createRow(1);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, data, 0, metadata, cellContext, styleCacheManager);
    }

    assertNotNull(row.getCell(0));
    assertNotNull(row.getCell(0).getCellStyle());
  }

  @Test
  void writeCells_noConditionalMatch_appliesDefaultColumnStyle() {
    ExcelMetadata<DefaultStyleDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        DefaultStyleDTO.class);
    DefaultStyleDTO data = new DefaultStyleDTO("test");
    Row row = sheet.createRow(1);

    try (CellContext cellContext = CellContext.acquire()) {
      cellWriter.writeCells(row, data, 0, metadata, cellContext, styleCacheManager);
    }

    assertNotNull(row.getCell(0));
    assertNotNull(row.getCell(0).getCellStyle());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Test")
  public static class TestDTO {

    @ExcelColumn(header = "Name", order = 1, headerStyle = TestHeaderStyle.class)
    private String name;

    @ExcelColumn(header = "Age", order = 2)
    private Integer age;

    @ExcelColumn(header = "Salary", order = 3, format = "#,##0.00")
    private BigDecimal salary;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Conditional")
  public static class ConditionalDTO {

    @ExcelColumn(
        header = "Amount",
        order = 1,
        conditionalStyles = @ConditionalStyle(when = "value < 0", style = NegativeStyle.class)
    )
    private Integer amount;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Formatted")
  public static class FormattedDTO {

    @ExcelColumn(header = "Price", order = 1, format = "#,##0.00")
    private BigDecimal price;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("FormatPriority")
  public static class FormatPriorityDTO {

    @ExcelColumn(
        header = "Amount",
        order = 1,
        format = "#,##0.00",
        columnStyle = StyleWithFormat.class
    )
    private BigDecimal amount;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("MultiConditional")
  public static class MultiConditionalDTO {

    @ExcelColumn(
        header = "Score",
        order = 1,
        conditionalStyles = {
            @ConditionalStyle(when = "value < 50", style = LowPriorityStyle.class, priority = 1),
            @ConditionalStyle(when = "value < 60", style = HighPriorityStyle.class, priority = 10)
        }
    )
    private Integer score;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("DefaultStyle")
  public static class DefaultStyleDTO {

    @ExcelColumn(header = "Value", order = 1, columnStyle = DefaultColumnStyle.class)
    private String value;
  }

  public static class TestHeaderStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
      configurer
          .backgroundColor(ExcelColors.lightBlue())
          .font("Arial", 11, FontStyle.BOLD);
    }
  }

  public static class NegativeStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
      configurer
          .backgroundColor(ExcelColors.rose())
          .fontColor(ExcelColors.darkRed());
    }
  }

  public static class StyleWithFormat extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
      configurer
          .numberFormat("0.000");
    }
  }

  public static class LowPriorityStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
      configurer.backgroundColor(ExcelColors.lightYellow());
    }
  }

  public static class HighPriorityStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
      configurer.backgroundColor(ExcelColors.lightOrange());
    }
  }

  public static class DefaultColumnStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
      configurer.backgroundColor(ExcelColors.grey25Percent());
    }
  }
}
