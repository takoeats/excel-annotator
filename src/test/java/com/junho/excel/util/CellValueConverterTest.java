package com.junho.excel.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.junho.excel.internal.util.CellValueConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CellValueConverter 테스트")
class CellValueConverterTest {

  private Workbook workbook;
  private Sheet sheet;
  private Row row;

  @BeforeEach
  void setUp() {
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet();
    row = sheet.createRow(0);
  }

  @AfterEach
  void tearDown() throws Exception {
    workbook.close();
  }

  @Test
  @DisplayName("null 값은 빈 셀로 설정")
  void nullValueShouldSetBlankCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValue(cell, null);

    assertEquals(CellType.BLANK, cell.getCellType());
  }

  @Test
  @DisplayName("빈 문자열은 빈 셀로 설정")
  void emptyStringShouldSetBlankCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValue(cell, "   ");

    assertEquals(CellType.BLANK, cell.getCellType());
  }

  @Test
  @DisplayName("숫자형 문자열은 숫자로 변환")
  void numericStringShouldConvertToNumber() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValue(cell, "12345");

    assertEquals(CellType.NUMERIC, cell.getCellType());
    assertEquals(12345.0, cell.getNumericCellValue(), 0.001);
  }

  @Test
  @DisplayName("콤마가 포함된 숫자는 숫자로 변환")
  void commaNumberShouldConvertToNumber() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValue(cell, "1,234,567");

    assertEquals(CellType.NUMERIC, cell.getCellType());
    assertEquals(1234567.0, cell.getNumericCellValue(), 0.001);
  }

  @Test
  @DisplayName("15자리 초과 정수는 문자열로 저장")
  void largeIntegerShouldStoreAsString() {
    Cell cell = row.createCell(0);
    String largeNumber = "1234567890123456";
    CellValueConverter.setCellValue(cell, largeNumber);

    assertEquals(CellType.STRING, cell.getCellType());
    assertEquals(largeNumber, cell.getStringCellValue());
  }

  @Test
  @DisplayName("일반 문자열은 문자열로 저장")
  void textStringShouldStoreAsString() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValue(cell, "Hello World");

    assertEquals(CellType.STRING, cell.getCellType());
    assertEquals("Hello World", cell.getStringCellValue());
  }

  @Test
  @DisplayName("isNumericLike - 정수는 숫자로 인식")
  void integerShouldBeNumeric() {
    assertTrue(CellValueConverter.isNumericLike("123"));
  }

  @Test
  @DisplayName("isNumericLike - 소수는 숫자로 인식")
  void decimalShouldBeNumeric() {
    assertTrue(CellValueConverter.isNumericLike("123.45"));
  }

  @Test
  @DisplayName("isNumericLike - 콤마 포함 숫자는 숫자로 인식")
  void commaNumberShouldBeNumeric() {
    assertTrue(CellValueConverter.isNumericLike("1,234,567"));
  }

  @Test
  @DisplayName("isNumericLike - null은 숫자가 아님")
  void nullShouldNotBeNumeric() {
    assertFalse(CellValueConverter.isNumericLike(null));
  }

  @Test
  @DisplayName("isNumericLike - 일반 문자열은 숫자가 아님")
  void textShouldNotBeNumeric() {
    assertFalse(CellValueConverter.isNumericLike("abc"));
  }

  @Test
  @DisplayName("isNumericLike - 양수 부호(+)가 있는 숫자는 숫자로 인식")
  void positiveSignNumberShouldBeNumeric() {
    assertTrue(CellValueConverter.isNumericLike("+123"));
  }

  @Test
  @DisplayName("isNumericLike - 음수 부호(-)가 있는 숫자는 숫자로 인식")
  void negativeSignNumberShouldBeNumeric() {
    assertTrue(CellValueConverter.isNumericLike("-123"));
  }

  @Test
  @DisplayName("isNumericLike - 소수점으로 시작하는 형식은 숫자가 아님")
  void decimalStartingWithDotShouldNotBeNumeric() {
    assertFalse(CellValueConverter.isNumericLike(".123"));
  }

  @Test
  @DisplayName("isNumericLike - 소수점으로 끝나는 형식은 숫자가 아님")
  void decimalEndingWithDotShouldNotBeNumeric() {
    assertFalse(CellValueConverter.isNumericLike("123."));
  }

  @Test
  @DisplayName("isNumericLike - 이중 부호는 숫자가 아님")
  void doubleSignShouldNotBeNumeric() {
    assertFalse(CellValueConverter.isNumericLike("++123"));
    assertFalse(CellValueConverter.isNumericLike("--123"));
  }

  @Test
  @DisplayName("isNumericLike - 소수점이 2개 이상이면 숫자가 아님")
  void multipleDecimalPointsShouldNotBeNumeric() {
    assertFalse(CellValueConverter.isNumericLike("12.34.56"));
  }

  @Test
  @DisplayName("isTooLargeIntegerForExcel - 15자리 정수는 허용")
  void fifteenDigitsShouldBeAllowed() {
    assertFalse(CellValueConverter.isTooLargeIntegerForExcel("123456789012345"));
  }

  @Test
  @DisplayName("isTooLargeIntegerForExcel - 16자리 정수는 너무 큼")
  void sixteenDigitsShouldBeTooLarge() {
    assertTrue(CellValueConverter.isTooLargeIntegerForExcel("1234567890123456"));
  }

  @Test
  @DisplayName("normalizeNumber - 콤마 제거")
  void shouldRemoveCommas() {
    assertEquals("1234567", CellValueConverter.normalizeNumber("1,234,567"));
  }

  @Test
  @DisplayName("normalizeNumber - 언더스코어 제거")
  void shouldRemoveUnderscores() {
    assertEquals("1234567", CellValueConverter.normalizeNumber("1_234_567"));
  }

  @Test
  @DisplayName("normalizeNumber - 공백 제거")
  void shouldRemoveSpaces() {
    assertEquals("1234567", CellValueConverter.normalizeNumber("1 234 567"));
  }

  @Test
  @DisplayName("getCellValueAsString - 문자열 셀 값 읽기")
  void shouldReadStringCell() {
    Cell cell = row.createCell(0);
    cell.setCellValue("Hello");

    assertEquals("Hello", CellValueConverter.getCellValueAsString(cell));
  }

  @Test
  @DisplayName("getCellValueAsString - 숫자 셀 값 읽기")
  void shouldReadNumericCell() {
    Cell cell = row.createCell(0);
    cell.setCellValue(123.45);

    assertEquals("123.45", CellValueConverter.getCellValueAsString(cell));
  }

  @Test
  @DisplayName("getCellValueAsString - 불린 셀 값 읽기")
  void shouldReadBooleanCell() {
    Cell cell = row.createCell(0);
    cell.setCellValue(true);

    assertEquals("true", CellValueConverter.getCellValueAsString(cell));
  }

  @Test
  @DisplayName("getCellValueAsString - BLANK 셀은 빈 문자열 반환")
  void shouldReadBlankCellAsEmptyString() {
    Cell cell = row.createCell(0);
    cell.setBlank();

    assertEquals("", CellValueConverter.getCellValueAsString(cell));
  }

  @Test
  @DisplayName("toDoubleSafe - 정수 문자열을 double로 변환")
  void shouldConvertIntegerString() {
    assertEquals(123.0, CellValueConverter.toDoubleSafe("123"), 0.001);
  }

  @Test
  @DisplayName("toDoubleSafe - 소수 문자열을 double로 변환")
  void shouldConvertDecimalString() {
    assertEquals(123.45, CellValueConverter.toDoubleSafe("123.45"), 0.001);
  }

  @Test
  @DisplayName("toDoubleSafe - 잘못된 형식은 NaN 반환")
  void shouldReturnNaNForInvalidFormat() {
    assertTrue(Double.isNaN(CellValueConverter.toDoubleSafe("abc")));
  }

  @Test
  @DisplayName("setCellValueSafely - null은 빈 셀로 설정")
  void setCellValueSafely_withNull_shouldSetBlankCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValueSafely(cell, null);

    assertEquals(CellType.BLANK, cell.getCellType());
  }

  @Test
  @DisplayName("setCellValueSafely - 빈 문자열은 빈 셀로 설정")
  void setCellValueSafely_withEmptyString_shouldSetBlankCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValueSafely(cell, "");

    assertEquals(CellType.BLANK, cell.getCellType());
  }

  @Test
  @DisplayName("setCellValueSafely - 문자열은 문자열 셀로 설정")
  void setCellValueSafely_withString_shouldSetStringCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValueSafely(cell, "Hello");

    assertEquals(CellType.STRING, cell.getCellType());
    assertEquals("Hello", cell.getStringCellValue());
  }

  @Test
  @DisplayName("setCellValueSafely - LocalDate는 날짜 셀로 설정")
  void setCellValueSafely_withLocalDate_shouldSetDateCell() {
    Cell cell = row.createCell(0);
    java.time.LocalDate date = java.time.LocalDate.of(2024, 1, 15);
    CellValueConverter.setCellValueSafely(cell, date);

    assertEquals(CellType.NUMERIC, cell.getCellType());
  }

  @Test
  @DisplayName("setCellValueSafely - LocalDateTime은 날짜시간 셀로 설정")
  void setCellValueSafely_withLocalDateTime_shouldSetDateTimeCell() {
    Cell cell = row.createCell(0);
    java.time.LocalDateTime dateTime = java.time.LocalDateTime.of(2024, 1, 15, 14, 30);
    CellValueConverter.setCellValueSafely(cell, dateTime);

    assertEquals(CellType.NUMERIC, cell.getCellType());
  }

  @Test
  @DisplayName("setCellValueSafely - Date는 날짜 셀로 설정")
  void setCellValueSafely_withDate_shouldSetDateCell() {
    Cell cell = row.createCell(0);
    java.util.Date date = new java.util.Date();
    CellValueConverter.setCellValueSafely(cell, date);

    assertEquals(CellType.NUMERIC, cell.getCellType());
  }

  @Test
  @DisplayName("setCellValueSafely - Calendar는 날짜 셀로 설정")
  void setCellValueSafely_withCalendar_shouldSetDateCell() {
    Cell cell = row.createCell(0);
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    CellValueConverter.setCellValueSafely(cell, calendar);

    assertEquals(CellType.NUMERIC, cell.getCellType());
  }

  @Test
  @DisplayName("setCellValueSafely - Integer는 숫자 셀로 설정")
  void setCellValueSafely_withInteger_shouldSetNumericCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValueSafely(cell, 12345);

    assertEquals(CellType.NUMERIC, cell.getCellType());
    assertEquals(12345.0, cell.getNumericCellValue(), 0.001);
  }

  @Test
  @DisplayName("setCellValueSafely - Long은 숫자 셀로 설정")
  void setCellValueSafely_withLong_shouldSetNumericCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValueSafely(cell, 123456789L);

    assertEquals(CellType.NUMERIC, cell.getCellType());
    assertEquals(123456789.0, cell.getNumericCellValue(), 0.001);
  }

  @Test
  @DisplayName("setCellValueSafely - Double은 숫자 셀로 설정")
  void setCellValueSafely_withDouble_shouldSetNumericCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValueSafely(cell, 123.45);

    assertEquals(CellType.NUMERIC, cell.getCellType());
    assertEquals(123.45, cell.getNumericCellValue(), 0.001);
  }

  @Test
  @DisplayName("setCellValueSafely - BigDecimal은 숫자 셀로 설정")
  void setCellValueSafely_withBigDecimal_shouldSetNumericCell() {
    Cell cell = row.createCell(0);
    java.math.BigDecimal value = new java.math.BigDecimal("999.99");
    CellValueConverter.setCellValueSafely(cell, value);

    assertEquals(CellType.NUMERIC, cell.getCellType());
    assertEquals(999.99, cell.getNumericCellValue(), 0.001);
  }

  @Test
  @DisplayName("setCellValueSafely - Boolean은 불린 셀로 설정")
  void setCellValueSafely_withBoolean_shouldSetBooleanCell() {
    Cell cell = row.createCell(0);
    CellValueConverter.setCellValueSafely(cell, true);

    assertEquals(CellType.BOOLEAN, cell.getCellType());
    assertTrue(cell.getBooleanCellValue());
  }

  @Test
  @DisplayName("setCellValueSafely - 기타 객체는 toString()으로 문자열 셀로 설정")
  void setCellValueSafely_withOtherObject_shouldSetStringCellFromToString() {
    Cell cell = row.createCell(0);
    Object customObject = new Object() {
      @Override
      public String toString() {
        return "CustomObject";
      }
    };
    CellValueConverter.setCellValueSafely(cell, customObject);

    assertEquals(CellType.STRING, cell.getCellType());
    assertEquals("CustomObject", cell.getStringCellValue());
  }
}
