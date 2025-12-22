package io.github.takoeats.excelannotator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.takoeats.excelannotator.testdto.CustomerPartADTO;
import io.github.takoeats.excelannotator.testdto.CustomerPartBDTO;
import io.github.takoeats.excelannotator.testdto.InvalidDTONoAnnotation;
import io.github.takoeats.excelannotator.testdto.OrderConflictDTO1;
import io.github.takoeats.excelannotator.testdto.OrderConflictDTO2;
import io.github.takoeats.excelannotator.testdto.PersonDTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

class ExcelExporterTest {

  @Test
  void excelFromList_withExplicitFileName_writesWorkbookWithoutTimestamp() throws Exception {
    List<PersonDTO> list = Arrays.asList(
        new PersonDTO("Alice", 30, new BigDecimal("123.45")),
        new PersonDTO("Bob", 40, new BigDecimal("67.89"))
    );

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String returnedName = ExcelExporter.excelFromList(baos, "report.xlsx", list);
    assertNotNull(returnedName);
    assertEquals("report.xlsx", returnedName);

    byte[] bytes = baos.toByteArray();
    assertTrue(bytes.length > 0);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes));
    assertEquals(1, wb.getNumberOfSheets());
    Sheet sheet = wb.getSheetAt(0);
    assertEquals("Persons", sheet.getSheetName());

    DataFormatter fmt = new DataFormatter();
    Row header = sheet.getRow(0);
    assertEquals("Name", fmt.formatCellValue(header.getCell(0)));
    assertEquals("Age", fmt.formatCellValue(header.getCell(1)));
    assertEquals("Salary", fmt.formatCellValue(header.getCell(2)));
    wb.close();
  }

  @Test
  void excelFromList_withDefaultFileName_returnsDownloadPrefix() {
    List<PersonDTO> list = Collections.singletonList(
        new PersonDTO("A", 1, new BigDecimal("1.00"))
    );
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String returnedName = ExcelExporter.excelFromList(baos, list);
    assertTrue(returnedName.startsWith("download_"));
    assertTrue(returnedName.endsWith(".xlsx"));
    assertTrue(baos.toByteArray().length > 0);
  }

  @Test
  void excelFromStream_singleSheet_overOutputStream() throws Exception {
    Stream<PersonDTO> stream = Stream.of(
        new PersonDTO("C", 3, new BigDecimal("3.00"))
    );

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String returnedName = ExcelExporter.excelFromStream(baos, "s.xlsx", stream);
    assertEquals("s.xlsx", returnedName);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(1, wb.getNumberOfSheets());
    assertEquals("Persons", wb
        .getSheetAt(0)
        .getSheetName());
    wb.close();
  }


  @Test
  void excelFromList_multiSheet_consolidatesSameSheetNameByOrder() throws Exception {
    Map<String, List<?>> map = new LinkedHashMap<>();
    map.put("partA", Collections.singletonList(new CustomerPartADTO("C001", "김철수")));
    map.put("partB",
        Collections.singletonList(new CustomerPartBDTO("kim@example.com", "010-1234-5678")));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String name = ExcelExporter.excelFromList(baos, "consolidated.xlsx", map);
    assertEquals("consolidated.xlsx", name);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(1, wb.getNumberOfSheets());
    Sheet sheet = wb.getSheetAt(0);
    assertEquals("고객", sheet.getSheetName());

    DataFormatter fmt = new DataFormatter();
    Row headerRow = sheet.getRow(0);
    assertEquals("고객ID", fmt.formatCellValue(headerRow.getCell(0)));
    assertEquals("고객명", fmt.formatCellValue(headerRow.getCell(1)));
    assertEquals("이메일", fmt.formatCellValue(headerRow.getCell(2)));
    assertEquals("전화번호", fmt.formatCellValue(headerRow.getCell(3)));

    Row dataRow = sheet.getRow(1);
    assertEquals("C001", fmt.formatCellValue(dataRow.getCell(0)));
    assertEquals("김철수", fmt.formatCellValue(dataRow.getCell(1)));
    assertEquals("kim@example.com", fmt.formatCellValue(dataRow.getCell(2)));
    assertEquals("010-1234-5678", fmt.formatCellValue(dataRow.getCell(3)));

    wb.close();
  }

  @Test
  void excelFromList_multiSheet_throwsExceptionWhenOrderConflictWithoutLinkedHashMap() {
    Map<String, List<?>> map = new java.util.HashMap<>();
    map.put("dto1", Collections.singletonList(new OrderConflictDTO1("A1", "A2")));
    map.put("dto2", Collections.singletonList(new OrderConflictDTO2("B2", "B3")));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ExcelExporterException exception =
        assertThrows(ExcelExporterException.class,
            () -> ExcelExporter.excelFromList(baos, "conflict.xlsx", map));

    assertTrue(exception
        .getMessage()
        .contains("ORDER_CONFLICT") ||
        exception
            .getMessage()
            .contains("order 값이 충돌"));
  }

  @Test
  void excelFromList_multiSheet_handlesOrderConflictWithLinkedHashMap() throws Exception {
    Map<String, List<?>> map = new LinkedHashMap<>();
    map.put("dto1", Collections.singletonList(new OrderConflictDTO1("A1", "A2")));
    map.put("dto2", Collections.singletonList(new OrderConflictDTO2("B2", "B3")));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String name = ExcelExporter.excelFromList(baos, "conflict.xlsx", map);
    assertEquals("conflict.xlsx", name);

    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(1, wb.getNumberOfSheets());
    Sheet sheet = wb.getSheetAt(0);
    assertEquals("충돌테스트", sheet.getSheetName());

    DataFormatter fmt = new DataFormatter();
    Row headerRow = sheet.getRow(0);
    assertEquals("필드A1", fmt.formatCellValue(headerRow.getCell(0)));
    assertEquals("필드A2", fmt.formatCellValue(headerRow.getCell(1)));
    assertEquals("필드B2", fmt.formatCellValue(headerRow.getCell(2)));
    assertEquals("필드B3", fmt.formatCellValue(headerRow.getCell(3)));

    Row dataRow = sheet.getRow(1);
    assertEquals("A1", fmt.formatCellValue(dataRow.getCell(0)));
    assertEquals("A2", fmt.formatCellValue(dataRow.getCell(1)));
    assertEquals("B2", fmt.formatCellValue(dataRow.getCell(2)));
    assertEquals("B3", fmt.formatCellValue(dataRow.getCell(3)));

    wb.close();
  }

  @Test
  void excelFromStream_throwsExceptionWhenStreamIsEmpty() {
    Stream<PersonDTO> emptyStream = Stream.empty();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ExcelExporterException exception =
        assertThrows(ExcelExporterException.class,
            () -> ExcelExporter.excelFromStream(baos, "test.xlsx", emptyStream));

    assertTrue(exception
        .getMessage()
        .contains("EMPTY_DATA") ||
        exception
            .getMessage()
            .contains("데이터가 없습니다"));
  }

  @Test
  void excelFromList_multiSheet_throwsExceptionWhenDTOMissingExcelSheetAnnotation() {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("InvalidSheet", Collections.singletonList(
        new InvalidDTONoAnnotation("test", "value")
    ));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ExcelExporterException exception =
        assertThrows(ExcelExporterException.class,
            () -> ExcelExporter.excelFromList(baos, "test.xlsx", sheetData));

    assertTrue(exception
        .getMessage()
        .contains("MISSING_EXCEL_SHEET_ANNOTATION") ||
        exception
            .getMessage()
            .contains("@ExcelSheet 어노테이션이 없는 DTO"));
  }

  @Test
  void excelFromList_throwsExceptionWhenDataExceedsOneMillionRows() {
    int excessiveRowCount = 1000001;
    List<PersonDTO> largeList = new java.util.ArrayList<>(excessiveRowCount);
    for (int i = 0; i < excessiveRowCount; i++) {
      largeList.add(new PersonDTO("Person" + i, i, new BigDecimal("100.00")));
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    ExcelExporterException exception =
        assertThrows(ExcelExporterException.class,
            () -> ExcelExporter.excelFromList(baos, "large.xlsx", largeList));

    assertTrue(exception
        .getMessage()
        .contains("EXCEED_MAX_ROWS") ||
        exception
            .getMessage()
            .contains("100만 건을 초과"));
    assertTrue(exception
        .getMessage()
        .contains("Stream API"));
  }

  @Test
  void excelFromList_withTimestampPattern_doesNotAddDuplicateTimestamp() {
    List<PersonDTO> list = Collections.singletonList(
        new PersonDTO("Test", 1, new BigDecimal("1.00"))
    );
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String returnedName = ExcelExporter.excelFromList(baos, "report_20251219_132153.xlsx", list);
    assertEquals("report_20251219_132153.xlsx", returnedName);
    assertTrue(baos.toByteArray().length > 0);
  }

  @Test
  void excelFromList_withTimestampPatternNoExtension_doesNotAddDuplicateTimestamp() {
    List<PersonDTO> list = Collections.singletonList(
        new PersonDTO("Test", 1, new BigDecimal("1.00"))
    );
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String returnedName = ExcelExporter.excelFromList(baos, "report_20251219_132153", list);
    assertEquals("report_20251219_132153.xlsx", returnedName);
    assertTrue(baos.toByteArray().length > 0);
  }

  @Test
  void excelFromStream_withTimestampPattern_doesNotAddDuplicateTimestamp() {
    Stream<PersonDTO> stream = Stream.of(
        new PersonDTO("Test", 1, new BigDecimal("1.00"))
    );
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String returnedName = ExcelExporter.excelFromStream(baos, "data_20251219_132153.xlsx", stream);
    assertEquals("data_20251219_132153.xlsx", returnedName);
    assertTrue(baos.toByteArray().length > 0);
  }

}