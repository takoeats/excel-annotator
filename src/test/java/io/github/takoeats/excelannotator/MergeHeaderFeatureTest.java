package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.ExcelColors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MergeHeaderFeatureTest {

    @Test
    void mergeHeader_basicTwoColumnMerge_createsTwoRowHeaderWithMergedCell() throws Exception {
        List<BasicMergeDTO> data = Arrays.asList(
                new BasicMergeDTO("Alice", "alice@example.com", 30),
                new BasicMergeDTO("Bob", "bob@example.com", 40)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "merge.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        assertEquals(3, sheet.getRow(0).getLastCellNum());
        assertEquals(3, sheet.getRow(1).getLastCellNum());

        DataFormatter fmt = new DataFormatter();
        Row mergeHeaderRow = sheet.getRow(0);
        Row normalHeaderRow = sheet.getRow(1);

        assertEquals("Customer Info", fmt.formatCellValue(mergeHeaderRow.getCell(0)));
        assertEquals("Name", fmt.formatCellValue(normalHeaderRow.getCell(0)));
        assertEquals("Email", fmt.formatCellValue(normalHeaderRow.getCell(1)));
        assertEquals("Age", fmt.formatCellValue(normalHeaderRow.getCell(2)));

        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        assertTrue(mergedRegions.size() >= 2);

        boolean foundCustomerMerge = false;
        boolean foundAgeMerge = false;

        for (CellRangeAddress region : mergedRegions) {
            if (region.getFirstRow() == 0 && region.getLastRow() == 0 &&
                    region.getFirstColumn() == 0 && region.getLastColumn() == 1) {
                foundCustomerMerge = true;
            }
            if (region.getFirstRow() == 0 && region.getLastRow() == 1 &&
                    region.getFirstColumn() == 2 && region.getLastColumn() == 2) {
                foundAgeMerge = true;
            }
        }

        assertTrue(foundCustomerMerge, "Customer Info should be merged horizontally");
        assertTrue(foundAgeMerge, "Age should be merged vertically");

        Row dataRow1 = sheet.getRow(2);
        assertEquals("Alice", fmt.formatCellValue(dataRow1.getCell(0)));
        assertEquals("alice@example.com", fmt.formatCellValue(dataRow1.getCell(1)));
        assertEquals(30, dataRow1.getCell(2).getNumericCellValue(), 0.01);

        wb.close();
    }

    @Test
    void mergeHeader_multipleGroups_createsSeparateMergedRegions() throws Exception {
        List<MultiGroupDTO> data = Arrays.asList(
                new MultiGroupDTO("Alice", "alice@example.com", "123 Main St", "Seoul", 30)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "multi.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        DataFormatter fmt = new DataFormatter();
        Row mergeHeaderRow = sheet.getRow(0);
        Row normalHeaderRow = sheet.getRow(1);

        assertEquals("Customer", fmt.formatCellValue(mergeHeaderRow.getCell(0)));
        assertEquals("Address", fmt.formatCellValue(mergeHeaderRow.getCell(2)));

        assertEquals("Name", fmt.formatCellValue(normalHeaderRow.getCell(0)));
        assertEquals("Email", fmt.formatCellValue(normalHeaderRow.getCell(1)));
        assertEquals("Street", fmt.formatCellValue(normalHeaderRow.getCell(2)));
        assertEquals("City", fmt.formatCellValue(normalHeaderRow.getCell(3)));

        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        assertTrue(mergedRegions.size() >= 3);

        wb.close();
    }

    @Test
    void mergeHeader_orderGapInMergeGroup_throwsException() {
        List<InvalidOrderGapDTO> data = Arrays.asList(
                new InvalidOrderGapDTO("Alice", 30, "alice@example.com")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "invalid.xlsx", data)
        );

        assertEquals(ErrorCode.MERGE_HEADER_ORDER_GAP, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("병합 헤더"));
    }

    @Test
    void mergeHeader_withCustomStyle_appliesStyleToMergedHeader() throws Exception {
        List<StyledMergeDTO> data = Arrays.asList(
                new StyledMergeDTO("Alice", "alice@example.com")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "styled.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        Row mergeHeaderRow = sheet.getRow(0);
        Cell mergeHeaderCell = mergeHeaderRow.getCell(0);

        assertNotNull(mergeHeaderCell);
        assertNotNull(mergeHeaderCell.getCellStyle());

        wb.close();
    }

    @Test
    void mergeHeader_onlyUnmergedColumns_createsSingleRowHeader() throws Exception {
        List<NoMergeDTO> data = Arrays.asList(
                new NoMergeDTO("Alice", 30)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "no-merge.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        assertEquals(2, headerRow.getLastCellNum());
        assertEquals("Name", new DataFormatter().formatCellValue(headerRow.getCell(0)));
        assertEquals("Age", new DataFormatter().formatCellValue(headerRow.getCell(1)));

        Row dataRow = sheet.getRow(1);
        assertEquals("Alice", new DataFormatter().formatCellValue(dataRow.getCell(0)));
        assertEquals(30, dataRow.getCell(1).getNumericCellValue(), 0.01);

        wb.close();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Test")
    public static class BasicMergeDTO {
        @ExcelColumn(header = "Name", order = 1, mergeHeader = "Customer Info")
        private String name;

        @ExcelColumn(header = "Email", order = 2, mergeHeader = "Customer Info")
        private String email;

        @ExcelColumn(header = "Age", order = 3)
        private Integer age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Test")
    public static class MultiGroupDTO {
        @ExcelColumn(header = "Name", order = 1, mergeHeader = "Customer")
        private String name;

        @ExcelColumn(header = "Email", order = 2, mergeHeader = "Customer")
        private String email;

        @ExcelColumn(header = "Street", order = 3, mergeHeader = "Address")
        private String street;

        @ExcelColumn(header = "City", order = 4, mergeHeader = "Address")
        private String city;

        @ExcelColumn(header = "Age", order = 5)
        private Integer age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Test")
    public static class InvalidOrderGapDTO {
        @ExcelColumn(header = "Name", order = 1, mergeHeader = "Customer Info")
        private String name;

        @ExcelColumn(header = "Age", order = 2)
        private Integer age;

        @ExcelColumn(header = "Email", order = 3, mergeHeader = "Customer Info")
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Test")
    public static class StyledMergeDTO {
        @ExcelColumn(header = "Name", order = 1, mergeHeader = "Customer", mergeHeaderStyle = BlueMergeHeaderStyle.class)
        private String name;

        @ExcelColumn(header = "Email", order = 2, mergeHeader = "Customer", mergeHeaderStyle = BlueMergeHeaderStyle.class)
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Test")
    public static class NoMergeDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(header = "Age", order = 2)
        private Integer age;
    }

    public static class BlueMergeHeaderStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
            configurer
                    .backgroundColor(ExcelColors.lightBlue())
                    .fontColor(ExcelColors.darkBlue());
        }
    }
}
