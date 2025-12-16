package com.junho.excel;

import static com.junho.excel.util.ExcelAssertions.assertExcelFileValid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.AttentionStyle;
import com.junho.excel.example.style.CriticalAlertStyle;
import com.junho.excel.example.style.CurrencyStyle;
import com.junho.excel.example.style.DateOnlyStyle;
import com.junho.excel.example.style.DateTimeStyle;
import com.junho.excel.example.style.DecimalNumberStyle;
import com.junho.excel.example.style.HighlightStyle;
import com.junho.excel.example.style.KoreanDateStyle;
import com.junho.excel.example.style.PercentageStyle;
import com.junho.excel.example.style.PurpleHeaderStyle;
import com.junho.excel.example.style.TableHeaderStyle;
import com.junho.excel.example.style.TableRowEvenStyle;
import com.junho.excel.example.style.TableRowOddStyle;
import com.junho.excel.example.style.ValidationErrorStyle;
import com.junho.excel.util.ExcelTestHelper;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.junit.jupiter.api.Test;

class PreDefinedStylesTest {

    @Test
    void currencyStyle_appliesKoreanWonFormat() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .currencyValue(new BigDecimal("1234567.00"))
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(0);

        assertNotNull(cell);
        XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
        String format = cellStyle.getDataFormatString();
        assertEquals("₩#,##0", format);

        ExcelTestHelper.assertRgbColor(cell, 144, 238, 144);
        wb.close();
    }

    @Test
    void decimalNumberStyle_appliesThreeDecimals() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .decimalValue(new BigDecimal("123.456"))
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(1);

        assertNotNull(cell);
        XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
        String format = cellStyle.getDataFormatString();
        assertEquals("0.000", format);

        ExcelTestHelper.assertRgbColor(cell, 192, 192, 192);

        wb.close();
    }

    @Test
    void percentageStyle_appliesPercentFormat() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .percentageValue(new BigDecimal("0.75"))
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(2);

        assertNotNull(cell);
        XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
        String format = cellStyle.getDataFormatString();
        assertEquals("0.00%", format);

        wb.close();
    }

    @Test
    void dateOnlyStyle_appliesDateFormat() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .dateValue(LocalDate.of(2025, 1, 15))
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(3);

        assertNotNull(cell);
        XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
        String format = cellStyle.getDataFormatString();
        assertEquals("yyyy-MM-dd", format);

        wb.close();
    }

    @Test
    void dateTimeStyle_appliesDateTimeFormat() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .dateTimeValue(LocalDateTime.of(2025, 1, 15, 14, 30))
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(4);

        assertNotNull(cell);
        XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
        String format = cellStyle.getDataFormatString();
        assertEquals("yyyy-MM-dd HH:mm", format);

        wb.close();
    }

    @Test
    void koreanDateStyle_appliesKoreanFormat() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .koreanDateValue(LocalDate.of(2025, 1, 15))
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(5);

        assertNotNull(cell);
        XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
        String format = cellStyle.getDataFormatString();
        assertEquals("yyyy\"년\" MM\"월\" dd\"일\"", format);

        wb.close();
    }

    @Test
    void highlightStyle_appliesYellowBackground() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .highlightValue("Highlighted")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(6);

        assertNotNull(cell);
        ExcelTestHelper.assertRgbColor(cell, 255, 255, 0);

        wb.close();
    }

    @Test
    void attentionStyle_appliesOrangeBackground() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .attentionValue("Attention")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(7);

        assertNotNull(cell);
        ExcelTestHelper.assertRgbColor(cell, 255, 160, 122);

        wb.close();
    }

    @Test
    void criticalAlertStyle_appliesPinkBackground() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .criticalValue("Critical")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(8);

        assertNotNull(cell);
        ExcelTestHelper.assertRgbColor(cell, 255, 192, 203);

        wb.close();
    }

    @Test
    void tableHeaderStyle_appliesDarkGrayBackground() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .tableHeaderValue("Header")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(9);

        assertNotNull(cell);
        ExcelTestHelper.assertRgbColor(cell, 51, 51, 51);

        wb.close();
    }

    @Test
    void purpleHeaderStyle_appliesLavenderBackground() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .purpleHeaderValue("Purple")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(10);

        assertNotNull(cell);
        ExcelTestHelper.assertRgbColor(cell, 230, 230, 250);

        wb.close();
    }

    @Test
    void validationErrorStyle_appliesPinkBackground() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .validationErrorValue("Error")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell cell = dataRow.getCell(11);

        assertNotNull(cell);
        ExcelTestHelper.assertRgbColor(cell, 255, 192, 203);

        wb.close();
    }

    @Test
    void tableRowStyles_applyAlternatingColors() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .evenRowValue("Even")
            .oddRowValue("Odd")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);

        Cell evenCell = dataRow.getCell(12);
        Cell oddCell = dataRow.getCell(13);

        assertNotNull(evenCell);
        assertNotNull(oddCell);

        ExcelTestHelper.assertRgbColor(evenCell, 192, 192, 192);
        ExcelTestHelper.assertRgbColor(oddCell, 255, 255, 255);

        wb.close();
    }

    @Test
    void allStyles_createValidExcelFile() throws Exception {
        StyleTestDTO data = StyleTestDTO.builder()
            .currencyValue(new BigDecimal("10000"))
            .decimalValue(new BigDecimal("123.45"))
            .percentageValue(new BigDecimal("0.85"))
            .dateValue(LocalDate.now())
            .dateTimeValue(LocalDateTime.now())
            .koreanDateValue(LocalDate.now())
            .highlightValue("Test")
            .attentionValue("Test")
            .criticalValue("Test")
            .tableHeaderValue("Test")
            .purpleHeaderValue("Test")
            .validationErrorValue("Test")
            .evenRowValue("Test")
            .oddRowValue("Test")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "all_styles.xlsx", Collections.singletonList(data));

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Style Test")
    public static class StyleTestDTO {

        @ExcelColumn(header = "Currency", order = 1, columnStyle = CurrencyStyle.class)
        private BigDecimal currencyValue;

        @ExcelColumn(header = "Decimal", order = 2, columnStyle = DecimalNumberStyle.class)
        private BigDecimal decimalValue;

        @ExcelColumn(header = "Percentage", order = 3, columnStyle = PercentageStyle.class)
        private BigDecimal percentageValue;

        @ExcelColumn(header = "Date", order = 4, columnStyle = DateOnlyStyle.class)
        private LocalDate dateValue;

        @ExcelColumn(header = "DateTime", order = 5, columnStyle = DateTimeStyle.class)
        private LocalDateTime dateTimeValue;

        @ExcelColumn(header = "KoreanDate", order = 6, columnStyle = KoreanDateStyle.class)
        private LocalDate koreanDateValue;

        @ExcelColumn(header = "Highlight", order = 7, columnStyle = HighlightStyle.class)
        private String highlightValue;

        @ExcelColumn(header = "Attention", order = 8, columnStyle = AttentionStyle.class)
        private String attentionValue;

        @ExcelColumn(header = "Critical", order = 9, columnStyle = CriticalAlertStyle.class)
        private String criticalValue;

        @ExcelColumn(header = "TableHeader", order = 10, columnStyle = TableHeaderStyle.class)
        private String tableHeaderValue;

        @ExcelColumn(header = "Purple", order = 11, columnStyle = PurpleHeaderStyle.class)
        private String purpleHeaderValue;

        @ExcelColumn(header = "Error", order = 12, columnStyle = ValidationErrorStyle.class)
        private String validationErrorValue;

        @ExcelColumn(header = "EvenRow", order = 13, columnStyle = TableRowEvenStyle.class)
        private String evenRowValue;

        @ExcelColumn(header = "OddRow", order = 14, columnStyle = TableRowOddStyle.class)
        private String oddRowValue;
    }
}
