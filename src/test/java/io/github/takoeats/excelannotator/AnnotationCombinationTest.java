package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.testdto.ExcludeTestDTO;
import io.github.takoeats.excelannotator.testdto.OrderDuplicateDTO;
import io.github.takoeats.excelannotator.testdto.OrderSkipDTO;
import io.github.takoeats.excelannotator.testdto.PriorityTestDTO;
import io.github.takoeats.excelannotator.testdto.StylePriorityDTO;
import io.github.takoeats.excelannotator.util.ExcelAssertions;
import io.github.takoeats.excelannotator.util.ExcelTestHelper;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationCombinationTest {

    @Test
    void sheetName_columnAnnotationCreates_separateSheet() throws Exception {
        PriorityTestDTO data = PriorityTestDTO.builder()
                                              .testField("value1")
                                              .widthField("value2")
                                              .orderField("value3")
                                              .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "priority_test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());

        ExcelAssertions.assertSheetCount(wb, 2);

        Sheet sheet1 = wb.getSheet("ColumnSheetName");
        assertNotNull(sheet1);

        Sheet sheet2 = wb.getSheet("DefaultSheet");
        assertNotNull(sheet2);
    }

    @Test
    void columnWidth_explicitWidthValue_appliesCorrectly() throws Exception {
        PriorityTestDTO data = PriorityTestDTO.builder()
            .widthField("test width")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "width_test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheet("DefaultSheet");

        int columnWidth = sheet.getColumnWidth(1);
        assertEquals(200 * 32, columnWidth);
    }

    @Test
    void excludeField_whenTrue_fieldNotPresentInExcel() throws Exception {
        ExcludeTestDTO data = ExcludeTestDTO.builder()
                                            .includedField1("included1")
                                            .excludedField("SHOULD_NOT_APPEAR")
                                            .includedField2("included2")
                                            .defaultExcludeField("default")
                                            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "exclude_test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        assertEquals(3, headerRow.getLastCellNum());

        ExcelAssertions.assertColumnHeader(sheet, 0, "Included Field 1");
        ExcelAssertions.assertColumnHeader(sheet, 1, "Included Field 2");
        ExcelAssertions.assertColumnHeader(sheet, 2, "Default Exclude");

        Row dataRow = sheet.getRow(1);
        ExcelTestHelper.assertCellValue(dataRow.getCell(0), "included1");
        ExcelTestHelper.assertCellValue(dataRow.getCell(1), "included2");
        ExcelTestHelper.assertCellValue(dataRow.getCell(2), "default");
    }

    @Test
    void excludeField_whenFalse_fieldPresentInExcel() throws Exception {
        ExcludeTestDTO data = ExcludeTestDTO.builder()
            .defaultExcludeField("should appear")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "exclude_false_test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);

        Cell cell = dataRow.getCell(2);
        assertNotNull(cell);
        ExcelTestHelper.assertCellValue(cell, "should appear");
    }

    @Test
    void headerStyle_appliesIndependently_fromColumnStyle() throws Exception {
        StylePriorityDTO data = StylePriorityDTO.builder()
                                                .headerStyleOnly("header only value")
                                                .columnStyleOnly("column only value")
                                                .bothStyles("both styles value")
                                                .noStyle("no style value")
                                                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "style_priority_test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        Cell headerCell1 = headerRow.getCell(0);
        ExcelTestHelper.assertRgbColor(headerCell1, 230, 230, 250);

        Row dataRow = sheet.getRow(1);
        Cell dataCell1 = dataRow.getCell(1);
        ExcelTestHelper.assertRgbColor(dataCell1, 255, 255, 0);
    }

    @Test
    void columnStyle_appliesIndependently_fromHeaderStyle() throws Exception {
        StylePriorityDTO data = StylePriorityDTO.builder()
            .columnStyleOnly("column only")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "column_style_test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);
        Cell dataCell = dataRow.getCell(1);

        ExcelTestHelper.assertRgbColor(dataCell, 255, 255, 0);
    }

    @Test
    void bothStyles_headerAndColumn_applyToRespectiveCells() throws Exception {
        StylePriorityDTO data = StylePriorityDTO.builder()
            .bothStyles("both value")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "both_styles_test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        Cell headerCell = headerRow.getCell(2);
        ExcelTestHelper.assertRgbColor(headerCell, 51, 51, 51);

        Row dataRow = sheet.getRow(1);
        Cell dataCell = dataRow.getCell(2);
        ExcelTestHelper.assertRgbColor(dataCell, 255, 255, 0);
    }

    @Test
    void orderSkipping_gapsInOrderValues_columnsAppearInCorrectSequence() throws Exception {
        OrderSkipDTO data = OrderSkipDTO.builder()
                                        .field1("first")
                                        .field3("second")
                                        .field5("third")
                                        .field7("fourth")
                                        .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "order_skip_test.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        assertEquals(4, headerRow.getLastCellNum());

        ExcelAssertions.assertColumnHeader(sheet, 0, "Field Order 1");
        ExcelAssertions.assertColumnHeader(sheet, 1, "Field Order 3");
        ExcelAssertions.assertColumnHeader(sheet, 2, "Field Order 5");
        ExcelAssertions.assertColumnHeader(sheet, 3, "Field Order 7");

        Row dataRow = sheet.getRow(1);
        ExcelTestHelper.assertCellValue(dataRow.getCell(0), "first");
        ExcelTestHelper.assertCellValue(dataRow.getCell(1), "second");
        ExcelTestHelper.assertCellValue(dataRow.getCell(2), "third");
        ExcelTestHelper.assertCellValue(dataRow.getCell(3), "fourth");
    }

    @Test
    void orderDuplicate_sameOrderValues_throwsExceptionOrDeterministicBehavior() {
        OrderDuplicateDTO data = OrderDuplicateDTO.builder()
                                                  .fieldA("A")
                                                  .fieldB("B")
                                                  .fieldC("C")
                                                  .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> {
            ExcelExporter.excelFromList(baos, "order_duplicate_test.xlsx", Collections.singletonList(data));

            Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
            Sheet sheet = wb.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            assertEquals(3, headerRow.getLastCellNum());
        });
    }

    @Test
    void multipleAnnotations_complexCombination_allAttributesApplyCorrectly() throws Exception {
        PriorityTestDTO data1 = PriorityTestDTO.builder()
            .testField("test1")
            .widthField("width1")
            .orderField("order1")
            .build();

        PriorityTestDTO data2 = PriorityTestDTO.builder()
            .testField("test2")
            .widthField("width2")
            .orderField("order2")
            .build();

        List<PriorityTestDTO> dataList = Arrays.asList(data1, data2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "complex_combination_test.xlsx", dataList);

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        ExcelAssertions.assertExcelFileValid(baos.toByteArray());

        ExcelAssertions.assertSheetCount(wb, 2);

        Sheet columnSheetNameSheet = wb.getSheet("ColumnSheetName");
        assertNotNull(columnSheetNameSheet);

        Sheet defaultSheet = wb.getSheet("DefaultSheet");
        assertNotNull(defaultSheet);
        ExcelAssertions.assertRowCount(defaultSheet, 3);

        int widthCol = defaultSheet.getColumnWidth(1);
        assertEquals(200 * 32, widthCol);

        Row headerRow = defaultSheet.getRow(0);
        Cell headerCell = headerRow.getCell(1);
        ExcelTestHelper.assertRgbColor(headerCell, 230, 230, 250);

        Row dataRow1 = defaultSheet.getRow(1);
        ExcelTestHelper.assertCellValue(dataRow1.getCell(0), "order1");
        ExcelTestHelper.assertCellValue(dataRow1.getCell(1), "width1");
    }
}
