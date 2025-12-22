package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.StyleCache;
import io.github.takoeats.excelannotator.testdto.LargeDataDTO;
import io.github.takoeats.excelannotator.util.ExcelAssertions;
import io.github.takoeats.excelannotator.util.ExcelTestHelper;
import io.github.takoeats.excelannotator.teststyle.CurrencyStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class Style64KLimitTest {

    @Test
    void largeDataset_10KRows_styleCountRemainsLow() throws Exception {
        List<LargeDataDTO> dataList = createLargeDataList(10000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "large_data.xlsx", dataList);

        XSSFWorkbook wb = (XSSFWorkbook) ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        assertNotNull(sheet);
        ExcelAssertions.assertRowCount(sheet, 10001);

        int styleCount = wb.getNumCellStyles();
        assertTrue(styleCount < 20, "Style count should be less than 20, but was: " + styleCount);

        wb.close();
    }

    @Test
    void multipleWorkbooks_independentStyleCaches() throws Exception {
        List<LargeDataDTO> dataList = createLargeDataList(100);

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos1, "workbook1.xlsx", dataList);

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos2, "workbook2.xlsx", dataList);

        XSSFWorkbook wb1 = (XSSFWorkbook) ExcelTestHelper.workbookFromBytes(baos1.toByteArray());
        XSSFWorkbook wb2 = (XSSFWorkbook) ExcelTestHelper.workbookFromBytes(baos2.toByteArray());

        int styleCount1 = wb1.getNumCellStyles();
        int styleCount2 = wb2.getNumCellStyles();

        assertEquals(styleCount1, styleCount2);

        assertTrue(styleCount1 < 20);

        wb1.close();
        wb2.close();
    }

    @Test
    void sameStyleClass_reusedAcrossRows() throws Exception {
        List<LargeDataDTO> dataList = createLargeDataList(1000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "reuse_test.xlsx", dataList);

        XSSFWorkbook wb = (XSSFWorkbook) ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        Row row2 = sheet.getRow(2);
        Row row500 = sheet.getRow(500);

        Cell cell1_col2 = row1.getCell(1);
        Cell cell2_col2 = row2.getCell(1);
        Cell cell500_col2 = row500.getCell(1);

        short styleIdx1 = cell1_col2.getCellStyle().getIndex();
        short styleIdx2 = cell2_col2.getCellStyle().getIndex();
        short styleIdx500 = cell500_col2.getCellStyle().getIndex();

        assertEquals(styleIdx1, styleIdx2);
        assertEquals(styleIdx1, styleIdx500);

        wb.close();
    }

    @Test
    void applicationLevelCache_singletonStyleInstances() {
        CustomExcelCellStyle instance1 =
            StyleCache.getStyleInstance(
                CurrencyStyle.class);

        CustomExcelCellStyle instance2 =
            StyleCache.getStyleInstance(
                CurrencyStyle.class);

        assertSame(instance1, instance2);
    }

    @Test
    void workbookLevelCache_poiCellStylePerWorkbook() throws Exception {
        List<LargeDataDTO> dataList = createLargeDataList(100);

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos1, "wb1.xlsx", dataList);

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos2, "wb2.xlsx", dataList);

        XSSFWorkbook wb1 = (XSSFWorkbook) ExcelTestHelper.workbookFromBytes(baos1.toByteArray());
        XSSFWorkbook wb2 = (XSSFWorkbook) ExcelTestHelper.workbookFromBytes(baos2.toByteArray());

        Sheet sheet1 = wb1.getSheetAt(0);
        Sheet sheet2 = wb2.getSheetAt(0);

        CellStyle style1 = sheet1.getRow(1).getCell(1).getCellStyle();
        CellStyle style2 = sheet2.getRow(1).getCell(1).getCellStyle();

        assertNotSame(style1, style2);

        wb1.close();
        wb2.close();
    }

    @Test
    void style64KLimit_preventedByReuse() throws Exception {
        List<LargeDataDTO> dataList = createLargeDataList(5000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> {
            ExcelExporter.excelFromList(baos, "no_64k_error.xlsx", dataList);
        });

        XSSFWorkbook wb = (XSSFWorkbook) ExcelTestHelper.workbookFromBytes(baos.toByteArray());

        int styleCount = wb.getNumCellStyles();
        assertTrue(styleCount < 64000,
            "Style count should be well below 64K limit, actual: " + styleCount);

        assertTrue(styleCount < 50,
            "Style count should be minimal due to reuse, actual: " + styleCount);

        wb.close();
    }

    private List<LargeDataDTO> createLargeDataList(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> LargeDataDTO.builder()
                .id((long) i)
                .name("Name" + i)
                .amount(BigDecimal.valueOf(1000 + i))
                .date(LocalDate.now().plusDays(i % 365))
                .status(i % 2 == 0 ? "Active" : "Inactive")
                .category("Category" + (i % 10))
                .score(50 + (i % 50))
                .note("Note" + i)
                .region("Region" + (i % 5))
                .type("Type" + (i % 3))
                .build())
            .collect(Collectors.toList());
    }
}
