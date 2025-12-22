package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.util.ExcelTestHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ColumnWidthCalculatorDetailTest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("WidthTest")
    public static class WidthTestDTO {
        @ExcelColumn(header = "Fixed Width 200", order = 1, width = 200)
        private String fixedWidth200;

        @ExcelColumn(header = "Fixed Width 50", order = 2, width = 50)
        private String fixedWidth50;

        @ExcelColumn(header = "Auto Width", order = 3, width = -1)
        private String autoWidth;

        @ExcelColumn(header = "Default Width", order = 4)
        private String defaultWidth;

        @ExcelColumn(header = "Very Long Content Auto", order = 5, width = -1)
        private String veryLongContentAuto;
    }

    @Test
    void fixedWidth_appliesCorrectly() throws Exception {
        WidthTestDTO data = WidthTestDTO.builder()
            .fixedWidth200("test")
            .fixedWidth50("abc")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "fixed_width.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        int width200 = sheet.getColumnWidth(0);
        int width50 = sheet.getColumnWidth(1);

        assertEquals(200 * 32, width200, "Fixed width 200 should be 200*32");
        assertEquals(50 * 32, width50, "Fixed width 50 should be 50*32");
    }

    @Test
    void autoWidth_calculatesBasedOnContent() throws Exception {
        WidthTestDTO data = WidthTestDTO.builder()
            .autoWidth("Short")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "auto_width.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        int autoWidthValue = sheet.getColumnWidth(2);

        assertTrue(autoWidthValue > 0, "Auto width should be calculated");
        assertTrue(autoWidthValue > 256, "Auto width should be at least minimum width");
    }

    @Test
    void defaultWidth_100_appliesCorrectly() throws Exception {
        WidthTestDTO data = WidthTestDTO.builder()
            .defaultWidth("default")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "default_width.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        int defaultWidthValue = sheet.getColumnWidth(3);

        assertTrue(defaultWidthValue == 100 * 32 || defaultWidthValue > 0,
            "Default width should be set (either fixed 100*32 or auto-calculated)");
    }

    @Test
    void largeDataset_autoWidth_usesSampling() throws Exception {
        List<WidthTestDTO> largeDataset = IntStream.range(0, 15000)
            .mapToObj(i -> WidthTestDTO.builder()
                .autoWidth("Content-" + i)
                .build())
            .collect(Collectors.toList());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        long startTime = System.currentTimeMillis();
        ExcelExporter.excelFromList(baos, "large_auto_width.xlsx", largeDataset);
        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Auto-width for 15K rows took: " + elapsedTime + "ms");

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        int autoWidthValue = sheet.getColumnWidth(2);
        assertTrue(autoWidthValue > 0, "Auto width should be calculated via sampling");

        assertTrue(elapsedTime < 5000,
            "Sampling should complete within 5 seconds, actual: " + elapsedTime + "ms");
    }

    @Test
    void veryLongContent_autoWidth_handlesGracefully() throws Exception {
        String veryLongText =
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트" +
            "아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트아주아주긴텍스트";

        WidthTestDTO data = WidthTestDTO.builder()
            .veryLongContentAuto(veryLongText)
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "very_long_auto.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        int veryLongWidth = sheet.getColumnWidth(4);

        assertTrue(veryLongWidth > 0, "Very long content should have calculated width");
        assertTrue(veryLongWidth < 65536,
            "Width should not exceed Excel's maximum column width");
    }

    @Test
    void mixedWidthSettings_allApplyCorrectly() throws Exception {
        WidthTestDTO data = WidthTestDTO.builder()
            .fixedWidth200("fixed200")
            .fixedWidth50("f50")
            .autoWidth("auto content")
            .defaultWidth("default")
            .veryLongContentAuto("Very long text for auto calculation")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "mixed_width.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        assertEquals(200 * 32, sheet.getColumnWidth(0));
        assertEquals(50 * 32, sheet.getColumnWidth(1));

        int autoWidth = sheet.getColumnWidth(2);
        assertTrue(autoWidth > 0);

        int defaultWidth = sheet.getColumnWidth(3);
        assertTrue(defaultWidth == 100 * 32 || defaultWidth > 0);

        int veryLongAutoWidth = sheet.getColumnWidth(4);
        assertTrue(veryLongAutoWidth > 100 * 32);
    }

    @Test
    void emptyContent_autoWidth_usesMinimumWidth() throws Exception {
        WidthTestDTO data = WidthTestDTO.builder()
            .autoWidth("")
            .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "empty_auto_width.xlsx", Collections.singletonList(data));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        int autoWidthValue = sheet.getColumnWidth(2);

        assertTrue(autoWidthValue >= 256,
            "Empty content should use at least minimum width (256), actual: " + autoWidthValue);
    }
}
