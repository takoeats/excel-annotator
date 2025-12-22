package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.ExcelColors;
import io.github.takoeats.excelannotator.style.FontStyle;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SheetDefaultStyleIntegrationTest {

    @Test
    @DisplayName("케이스 1: @ExcelSheet에 아무것도 지정하지 않으면 라이브러리 기본 스타일 적용")
    void case1_noSheetDefaultStyle_usesLibraryDefaultStyle() throws IOException {
        List<NoDefaultStyleDTO> items = Arrays.asList(
                new NoDefaultStyleDTO("Item 1", 100),
                new NoDefaultStyleDTO("Item 2", 200)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", items);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        Row dataRow = sheet.getRow(1);

        XSSFCellStyle nameHeaderStyle = (XSSFCellStyle) headerRow.getCell(0).getCellStyle();
        XSSFColor nameHeaderBg = nameHeaderStyle.getFillForegroundColorColor();
        assertNotNull(nameHeaderBg);
        assertArrayEquals(new byte[]{(byte) 192, (byte) 192, (byte) 192}, nameHeaderBg.getRGB());

        XSSFCellStyle valueHeaderStyle = (XSSFCellStyle) headerRow.getCell(1).getCellStyle();
        XSSFColor valueHeaderBg = valueHeaderStyle.getFillForegroundColorColor();
        assertNotNull(valueHeaderBg);
        assertArrayEquals(new byte[]{(byte) 192, (byte) 192, (byte) 192}, valueHeaderBg.getRGB());

        XSSFCellStyle nameDataStyle = (XSSFCellStyle) dataRow.getCell(0).getCellStyle();
        assertNull(nameDataStyle.getFillForegroundColorColor());

        XSSFCellStyle valueDataStyle = (XSSFCellStyle) dataRow.getCell(1).getCellStyle();
        assertNull(valueDataStyle.getFillForegroundColorColor());

        workbook.close();
    }

    @Test
    @DisplayName("케이스 2: @ExcelSheet에 defaultHeaderStyle만 지정 시 모든 헤더에 적용")
    void case2_sheetDefaultHeaderStyle_appliedToAllHeaders() throws IOException {
        List<OnlyHeaderDefaultDTO> items = Arrays.asList(
                new OnlyHeaderDefaultDTO("P001", "Laptop", 1500000)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", items);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        for (int i = 0; i < 3; i++) {
            XSSFCellStyle headerStyle = (XSSFCellStyle) headerRow.getCell(i).getCellStyle();
            XSSFColor bgColor = headerStyle.getFillForegroundColorColor();
            assertNotNull(bgColor);
            assertArrayEquals(new byte[]{(byte) 0, (byte) 0, (byte) 139}, bgColor.getRGB());
            assertTrue(workbook.getFontAt(headerStyle.getFontIndex()).getBold());
        }

        workbook.close();
    }

    @Test
    @DisplayName("케이스 3: @ExcelSheet에 defaultColumnStyle만 지정 시 모든 데이터 컬럼에 적용")
    void case3_sheetDefaultColumnStyle_appliedToAllDataColumns() throws IOException {
        List<OnlyColumnDefaultDTO> items = Arrays.asList(
                new OnlyColumnDefaultDTO("P001", "Mouse", 25000)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", items);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);
        Row dataRow = sheet.getRow(1);

        for (int i = 0; i < 3; i++) {
            XSSFCellStyle dataStyle = (XSSFCellStyle) dataRow.getCell(i).getCellStyle();
            XSSFColor bgColor = dataStyle.getFillForegroundColorColor();
            assertNotNull(bgColor);
            assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 224}, bgColor.getRGB());
        }

        workbook.close();
    }

    @Test
    @DisplayName("케이스 4: @ExcelSheet에 defaultHeaderStyle + defaultColumnStyle 모두 지정")
    void case4_bothSheetDefaults_appliedCorrectly() throws IOException {
        List<BothDefaultsDTO> items = Arrays.asList(
                new BothDefaultsDTO("P001", "Keyboard", 80000)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", items);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        Row dataRow = sheet.getRow(1);

        for (int i = 0; i < 3; i++) {
            XSSFCellStyle headerStyle = (XSSFCellStyle) headerRow.getCell(i).getCellStyle();
            XSSFColor headerBg = headerStyle.getFillForegroundColorColor();
            assertNotNull(headerBg);
            assertArrayEquals(new byte[]{(byte) 0, (byte) 0, (byte) 139}, headerBg.getRGB());
        }

        for (int i = 0; i < 3; i++) {
            XSSFCellStyle dataStyle = (XSSFCellStyle) dataRow.getCell(i).getCellStyle();
            XSSFColor dataBg = dataStyle.getFillForegroundColorColor();
            assertNotNull(dataBg);
            assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 224}, dataBg.getRGB());
        }

        workbook.close();
    }

    @Test
    @DisplayName("케이스 5: 명시적 @ExcelColumn.headerStyle이 @ExcelSheet.defaultHeaderStyle보다 우선")
    void case5_explicitColumnHeaderStyle_overridesSheetDefault() throws IOException {
        List<ExplicitHeaderStyleDTO> items = Arrays.asList(
                new ExplicitHeaderStyleDTO("P001", "Monitor", 300000)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", items);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        XSSFCellStyle codeHeaderStyle = (XSSFCellStyle) headerRow.getCell(0).getCellStyle();
        XSSFColor codeBg = codeHeaderStyle.getFillForegroundColorColor();
        assertNotNull(codeBg);
        assertArrayEquals(new byte[]{(byte) 0, (byte) 0, (byte) 139}, codeBg.getRGB());

        XSSFCellStyle nameHeaderStyle = (XSSFCellStyle) headerRow.getCell(1).getCellStyle();
        XSSFColor nameBg = nameHeaderStyle.getFillForegroundColorColor();
        assertNotNull(nameBg);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 165, (byte) 0}, nameBg.getRGB());

        XSSFCellStyle priceHeaderStyle = (XSSFCellStyle) headerRow.getCell(2).getCellStyle();
        XSSFColor priceBg = priceHeaderStyle.getFillForegroundColorColor();
        assertNotNull(priceBg);
        assertArrayEquals(new byte[]{(byte) 0, (byte) 0, (byte) 139}, priceBg.getRGB());

        workbook.close();
    }

    @Test
    @DisplayName("케이스 6: 명시적 @ExcelColumn.columnStyle이 @ExcelSheet.defaultColumnStyle보다 우선")
    void case6_explicitColumnDataStyle_overridesSheetDefault() throws IOException {
        List<ExplicitColumnStyleDTO> items = Arrays.asList(
                new ExplicitColumnStyleDTO("P001", "SSD", 150000)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", items);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);
        Row dataRow = sheet.getRow(1);

        XSSFCellStyle codeDataStyle = (XSSFCellStyle) dataRow.getCell(0).getCellStyle();
        XSSFColor codeBg = codeDataStyle.getFillForegroundColorColor();
        assertNotNull(codeBg);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 224}, codeBg.getRGB());

        XSSFCellStyle nameDataStyle = (XSSFCellStyle) dataRow.getCell(1).getCellStyle();
        XSSFColor nameBg = nameDataStyle.getFillForegroundColorColor();
        assertNotNull(nameBg);
        assertArrayEquals(new byte[]{(byte) 144, (byte) 238, (byte) 144}, nameBg.getRGB());

        XSSFCellStyle priceDataStyle = (XSSFCellStyle) dataRow.getCell(2).getCellStyle();
        XSSFColor priceBg = priceDataStyle.getFillForegroundColorColor();
        assertNotNull(priceBg);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 224}, priceBg.getRGB());

        workbook.close();
    }

    @Test
    @DisplayName("케이스 7: 우선순위 전체 검증 - 1.명시적 컬럼 스타일 > 2.시트 기본 스타일 > 3.라이브러리 기본")
    void case7_fullPriorityTest_allCombinations() throws IOException {
        List<ComprehensivePriorityDTO> items = Arrays.asList(
                new ComprehensivePriorityDTO("P001", "CPU", 500000, 50)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", items);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        Row dataRow = sheet.getRow(1);

        XSSFCellStyle codeHeaderStyle = (XSSFCellStyle) headerRow.getCell(0).getCellStyle();
        XSSFColor codeHeaderBg = codeHeaderStyle.getFillForegroundColorColor();
        assertNotNull(codeHeaderBg);
        assertArrayEquals(new byte[]{(byte) 0, (byte) 0, (byte) 139}, codeHeaderBg.getRGB());

        XSSFCellStyle nameHeaderStyle = (XSSFCellStyle) headerRow.getCell(1).getCellStyle();
        XSSFColor nameHeaderBg = nameHeaderStyle.getFillForegroundColorColor();
        assertNotNull(nameHeaderBg);
        assertArrayEquals(new byte[]{(byte) 0, (byte) 0, (byte) 139}, nameHeaderBg.getRGB());

        XSSFCellStyle priceHeaderStyle = (XSSFCellStyle) headerRow.getCell(2).getCellStyle();
        XSSFColor priceHeaderBg = priceHeaderStyle.getFillForegroundColorColor();
        assertNotNull(priceHeaderBg);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 165, (byte) 0}, priceHeaderBg.getRGB());

        XSSFCellStyle stockHeaderStyle = (XSSFCellStyle) headerRow.getCell(3).getCellStyle();
        XSSFColor stockHeaderBg = stockHeaderStyle.getFillForegroundColorColor();
        assertNotNull(stockHeaderBg);
        assertArrayEquals(new byte[]{(byte) 0, (byte) 0, (byte) 139}, stockHeaderBg.getRGB());

        XSSFCellStyle codeDataStyle = (XSSFCellStyle) dataRow.getCell(0).getCellStyle();
        XSSFColor codeDataBg = codeDataStyle.getFillForegroundColorColor();
        assertNotNull(codeDataBg);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 224}, codeDataBg.getRGB());

        XSSFCellStyle nameDataStyle = (XSSFCellStyle) dataRow.getCell(1).getCellStyle();
        XSSFColor nameDataBg = nameDataStyle.getFillForegroundColorColor();
        assertNotNull(nameDataBg);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 224}, nameDataBg.getRGB());

        XSSFCellStyle priceDataStyle = (XSSFCellStyle) dataRow.getCell(2).getCellStyle();
        XSSFColor priceDataBg = priceDataStyle.getFillForegroundColorColor();
        assertNotNull(priceDataBg);
        assertArrayEquals(new byte[]{(byte) 144, (byte) 238, (byte) 144}, priceDataBg.getRGB());

        XSSFCellStyle stockDataStyle = (XSSFCellStyle) dataRow.getCell(3).getCellStyle();
        XSSFColor stockDataBg = stockDataStyle.getFillForegroundColorColor();
        assertNotNull(stockDataBg);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 224}, stockDataBg.getRGB());

        workbook.close();
    }

    @ExcelSheet(value = "No Defaults")
    @Data
    @AllArgsConstructor
    public static class NoDefaultStyleDTO {
        @ExcelColumn(header = "이름", order = 1)
        private String name;

        @ExcelColumn(header = "값", order = 2)
        private Integer value;
    }

    @ExcelSheet(value = "Only Header Default", defaultHeaderStyle = BlueHeaderStyle.class)
    @Data
    @AllArgsConstructor
    public static class OnlyHeaderDefaultDTO {
        @ExcelColumn(header = "코드", order = 1)
        private String code;

        @ExcelColumn(header = "이름", order = 2)
        private String name;

        @ExcelColumn(header = "가격", order = 3)
        private Integer price;
    }

    @ExcelSheet(value = "Only Column Default", defaultColumnStyle = YellowColumnStyle.class)
    @Data
    @AllArgsConstructor
    public static class OnlyColumnDefaultDTO {
        @ExcelColumn(header = "코드", order = 1)
        private String code;

        @ExcelColumn(header = "이름", order = 2)
        private String name;

        @ExcelColumn(header = "가격", order = 3)
        private Integer price;
    }

    @ExcelSheet(
            value = "Both Defaults",
            defaultHeaderStyle = BlueHeaderStyle.class,
            defaultColumnStyle = YellowColumnStyle.class
    )
    @Data
    @AllArgsConstructor
    public static class BothDefaultsDTO {
        @ExcelColumn(header = "코드", order = 1)
        private String code;

        @ExcelColumn(header = "이름", order = 2)
        private String name;

        @ExcelColumn(header = "가격", order = 3)
        private Integer price;
    }

    @ExcelSheet(
            value = "Explicit Header Style",
            defaultHeaderStyle = BlueHeaderStyle.class,
            defaultColumnStyle = YellowColumnStyle.class
    )
    @Data
    @AllArgsConstructor
    public static class ExplicitHeaderStyleDTO {
        @ExcelColumn(header = "코드", order = 1)
        private String code;

        @ExcelColumn(header = "이름", order = 2, headerStyle = OrangeHeaderStyle.class)
        private String name;

        @ExcelColumn(header = "가격", order = 3)
        private Integer price;
    }

    @ExcelSheet(
            value = "Explicit Column Style",
            defaultHeaderStyle = BlueHeaderStyle.class,
            defaultColumnStyle = YellowColumnStyle.class
    )
    @Data
    @AllArgsConstructor
    public static class ExplicitColumnStyleDTO {
        @ExcelColumn(header = "코드", order = 1)
        private String code;

        @ExcelColumn(header = "이름", order = 2, columnStyle = GreenColumnStyle.class)
        private String name;

        @ExcelColumn(header = "가격", order = 3)
        private Integer price;
    }

    @ExcelSheet(
            value = "Priority Test",
            defaultHeaderStyle = BlueHeaderStyle.class,
            defaultColumnStyle = YellowColumnStyle.class
    )
    @Data
    @AllArgsConstructor
    public static class ComprehensivePriorityDTO {
        @ExcelColumn(header = "상품코드", order = 1)
        private String productCode;

        @ExcelColumn(header = "상품명", order = 2)
        private String productName;

        @ExcelColumn(header = "가격", order = 3, headerStyle = OrangeHeaderStyle.class, columnStyle = GreenColumnStyle.class)
        private Integer price;

        @ExcelColumn(header = "재고", order = 4)
        private Integer stock;
    }

    public static class BlueHeaderStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
            configurer
                    .backgroundColor(ExcelColors.darkBlue())
                    .fontColor(ExcelColors.white())
                    .font("Arial", 11, FontStyle.BOLD)
                    .alignment(HorizontalAlignment.CENTER);
        }
    }

    public static class YellowColumnStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
            configurer
                    .backgroundColor(ExcelColors.lightYellow())
                    .alignment(HorizontalAlignment.LEFT);
        }
    }

    public static class GreenColumnStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
            configurer
                    .backgroundColor(ExcelColors.lightGreen())
                    .dataFormat("#,##0")
                    .alignment(HorizontalAlignment.RIGHT);
        }
    }

    public static class OrangeHeaderStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
            configurer
                    .backgroundColor(ExcelColors.orange())
                    .fontColor(ExcelColors.white())
                    .font("Arial", 11, FontStyle.BOLD)
                    .alignment(HorizontalAlignment.CENTER);
        }
    }
}
