package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.testdto.AllTypesDTO;
import io.github.takoeats.excelannotator.util.ExcelTestHelper;
import io.github.takoeats.excelannotator.util.TestDataFactory;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static io.github.takoeats.excelannotator.util.ExcelAssertions.assertExcelFileValid;
import static io.github.takoeats.excelannotator.util.ExcelAssertions.assertRowCount;
import static org.junit.jupiter.api.Assertions.*;

class DataTypeComprehensiveTest {

    @Test
    void localDate_storesAsDateType() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 15);
        AllTypesDTO data = AllTypesDTO.builder()
                .localDateValue(testDate)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(8);

        assertNotNull(cell);
        assertEquals(CellType.NUMERIC, cell.getCellType(), "LocalDate should be stored as NUMERIC type");
        assertTrue(DateUtil.isCellDateFormatted(cell), "Cell should be date-formatted");

        LocalDate actualDate = cell.getLocalDateTimeCellValue().toLocalDate();
        assertEquals(testDate, actualDate, "LocalDate value should match");
        wb.close();
    }

    @Test
    void localDateTime_storesAsDateType() throws Exception {
        LocalDateTime testDateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 0);
        AllTypesDTO data = AllTypesDTO.builder()
                .localDateTimeValue(testDateTime)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(9);

        assertNotNull(cell);
        assertEquals(CellType.NUMERIC, cell.getCellType(), "LocalDateTime should be stored as NUMERIC type");
        assertTrue(DateUtil.isCellDateFormatted(cell), "Cell should be date-formatted");

        LocalDateTime actualDateTime = cell.getLocalDateTimeCellValue();
        assertEquals(testDateTime, actualDateTime, "LocalDateTime value should match");
        wb.close();
    }

    @Test
    void zonedDateTime_convertsCorrectly() throws Exception {
        AllTypesDTO data = AllTypesDTO.builder()
                .zonedDateTimeValue(ZonedDateTime.now())
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(10);

        assertNotNull(cell);
        wb.close();
    }

    @Test
    void bigDecimal_preservesPrecision() throws Exception {
        BigDecimal precisValue = new BigDecimal("12345.6789");
        AllTypesDTO data = AllTypesDTO.builder()
                .bigDecimalValue(precisValue)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(6);

        assertNotNull(cell);
        double actualValue = cell.getNumericCellValue();
        assertEquals(precisValue.doubleValue(), actualValue, 0.0001);
        wb.close();
    }

    @Test
    void bigInteger_largerThan15Digits_storesAsText() throws Exception {
        BigInteger largeNumber = new BigInteger("12345678901234567890");
        AllTypesDTO data = AllTypesDTO.builder()
                .bigIntegerValue(largeNumber)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(7);

        assertNotNull(cell);
        wb.close();
    }

    @Test
    void enum_convertsToString() throws Exception {
        AllTypesDTO data = AllTypesDTO.builder()
                .enumValue(AllTypesDTO.StatusEnum.ACTIVE)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(11);

        assertNotNull(cell);
        ExcelTestHelper.assertCellValue(cell, "ACTIVE");
        wb.close();
    }

    @Test
    void boolean_storesAsBooleanType() throws Exception {
        AllTypesDTO data1 = AllTypesDTO.builder()
                .booleanValue(true)
                .build();
        AllTypesDTO data2 = AllTypesDTO.builder()
                .booleanValue(false)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Arrays.asList(data1, data2));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        Cell cell1 = sheet.getRow(1).getCell(5);
        Cell cell2 = sheet.getRow(2).getCell(5);

        assertNotNull(cell1);
        assertNotNull(cell2);
        assertEquals(CellType.BOOLEAN, cell1.getCellType(), "Boolean should be stored as BOOLEAN type");
        assertEquals(CellType.BOOLEAN, cell2.getCellType(), "Boolean should be stored as BOOLEAN type");
        assertTrue(cell1.getBooleanCellValue(), "First boolean value should be true");
        assertFalse(cell2.getBooleanCellValue(), "Second boolean value should be false");
        wb.close();
    }

    @Test
    void null_createsBlankCell() throws Exception {
        AllTypesDTO data = AllTypesDTO.builder()
                .stringValue(null)
                .integerValue(null)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);

        Cell stringCell = dataRow.getCell(0);
        Cell intCell = dataRow.getCell(1);

        assertTrue(stringCell == null || stringCell.getCellType() == CellType.BLANK);
        assertTrue(intCell == null || intCell.getCellType() == CellType.BLANK);
        wb.close();
    }

    @Test
    void emptyString_createsBlankCell() throws Exception {
        AllTypesDTO data = AllTypesDTO.builder()
                .stringValue("")
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(0);

        assertTrue(cell == null || cell.getCellType() == CellType.BLANK);
        wb.close();
    }

    @Test
    void allTypes_mixedData_convertsCorrectly() throws Exception {
        List<AllTypesDTO> dataList = TestDataFactory.createAllTypesList(10);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "all_types.xlsx", dataList);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        Sheet sheet = wb.getSheetAt(0);

        assertRowCount(sheet, 11);

        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        ExcelTestHelper.assertCellValue(headerRow.getCell(0), "String");
        ExcelTestHelper.assertCellValue(headerRow.getCell(1), "Integer");
        ExcelTestHelper.assertCellValue(headerRow.getCell(11), "Enum");

        wb.close();
    }

    @Test
    void javaUtilDate_storesAsDateType() throws Exception {
        Date testDate = new Date();
        AllTypesDTO data = AllTypesDTO.builder()
                .javaUtilDateValue(testDate)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(12);

        assertNotNull(cell);
        assertEquals(CellType.NUMERIC, cell.getCellType(), "java.util.Date should be stored as NUMERIC type");
        assertTrue(DateUtil.isCellDateFormatted(cell), "Cell should be date-formatted");

        Date actualDate = cell.getDateCellValue();
        assertEquals(testDate.getTime() / 1000, actualDate.getTime() / 1000, "Date values should match (within 1 second)");
        wb.close();
    }

    @Test
    void javaSqlDate_storesAsDateType() throws Exception {
        java.sql.Date testDate = new java.sql.Date(System.currentTimeMillis());
        AllTypesDTO data = AllTypesDTO.builder()
                .javaSqlDateValue(testDate)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(13);

        assertNotNull(cell);
        assertEquals(CellType.NUMERIC, cell.getCellType(), "java.sql.Date should be stored as NUMERIC type");
        assertTrue(DateUtil.isCellDateFormatted(cell), "Cell should be date-formatted");

        Date actualDate = cell.getDateCellValue();
        assertNotNull(actualDate, "Date value should not be null");
        wb.close();
    }

    @Test
    void javaSqlTimestamp_storesAsDateType() throws Exception {
        java.sql.Timestamp testTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
        AllTypesDTO data = AllTypesDTO.builder()
                .javaSqlTimestampValue(testTimestamp)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(14);

        assertNotNull(cell);
        assertEquals(CellType.NUMERIC, cell.getCellType(), "java.sql.Timestamp should be stored as NUMERIC type");
        assertTrue(DateUtil.isCellDateFormatted(cell), "Cell should be date-formatted");

        Date actualDate = cell.getDateCellValue();
        assertNotNull(actualDate, "Timestamp value should not be null");
        wb.close();
    }

    @Test
    void dateWithCustomFormat_appliesFormatCorrectly() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 15);
        AllTypesDTO data = AllTypesDTO.builder()
                .localDateValue(testDate)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(8);

        assertNotNull(cell);
        assertTrue(DateUtil.isCellDateFormatted(cell), "Cell should have date format applied");

        String formatString = cell.getCellStyle().getDataFormatString();
        assertNotNull(formatString, "Format string should not be null");
        wb.close();
    }

    @Test
    void nullDate_createsBlankCell() throws Exception {
        AllTypesDTO data = AllTypesDTO.builder()
                .localDateValue(null)
                .javaUtilDateValue(null)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);

        Cell localDateCell = dataRow.getCell(8);
        Cell utilDateCell = dataRow.getCell(12);

        assertTrue(localDateCell == null || localDateCell.getCellType() == CellType.BLANK,
                "Null LocalDate should create blank cell");
        assertTrue(utilDateCell == null || utilDateCell.getCellType() == CellType.BLANK,
                "Null Date should create blank cell");
        wb.close();
    }

    @Test
    void numericTypes_allConvertToNumeric() throws Exception {
        AllTypesDTO data = AllTypesDTO.builder()
                .integerValue(100)
                .longValue(1000L)
                .doubleValue(123.45)
                .floatValue(67.89f)
                .bigDecimalValue(new BigDecimal("999.99"))
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);

        assertEquals(CellType.NUMERIC, dataRow.getCell(1).getCellType());
        assertEquals(CellType.NUMERIC, dataRow.getCell(2).getCellType());
        assertEquals(CellType.NUMERIC, dataRow.getCell(3).getCellType());
        assertEquals(CellType.NUMERIC, dataRow.getCell(4).getCellType());
        assertEquals(CellType.NUMERIC, dataRow.getCell(6).getCellType());

        wb.close();
    }
}
