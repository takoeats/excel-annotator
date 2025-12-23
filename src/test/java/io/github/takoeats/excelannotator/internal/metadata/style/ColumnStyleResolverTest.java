package io.github.takoeats.excelannotator.internal.metadata.style;

import io.github.takoeats.excelannotator.annotation.ConditionalStyle;
import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.internal.metadata.SheetInfo;
import io.github.takoeats.excelannotator.masking.Masking;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultColumnStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultHeaderStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultNumberStyle;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ColumnStyleResolverTest {

    @Test
    void resolveHeaderStyle_withDefaultHeaderStyle_returnsDefaultHeaderStyle() throws NoSuchFieldException {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createDefaultSheetInfo();

        CustomExcelCellStyle headerStyle = ColumnStyleResolver.resolveHeaderStyle(excelColumn, sheetInfo);

        assertNotNull(headerStyle);
        assertInstanceOf(DefaultHeaderStyle.class, headerStyle);
    }

    @Test
    void resolveHeaderStyle_withCustomStyle_returnsStyleInstance() throws NoSuchFieldException {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 0, TestHeaderStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createDefaultSheetInfo();

        CustomExcelCellStyle headerStyle = ColumnStyleResolver.resolveHeaderStyle(excelColumn, sheetInfo);

        assertNotNull(headerStyle);
        assertInstanceOf(TestHeaderStyle.class, headerStyle);
    }

    @Test
    void resolveColumnStyle_withDefaultColumnStyleAndNumericType_returnsDefaultNumberStyle() throws NoSuchFieldException {
        Field numericField = TestDTO.class.getDeclaredField("age");
        ExcelColumn excelColumn = createExcelColumn("Age", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createDefaultSheetInfo();

        CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, numericField, sheetInfo);

        assertNotNull(columnStyle);
        assertInstanceOf(DefaultNumberStyle.class, columnStyle);
    }

    @Test
    void resolveColumnStyle_withDefaultColumnStyleAndStringType_returnsDefaultColumnStyle() throws NoSuchFieldException {
        Field stringField = TestDTO.class.getDeclaredField("name");
        ExcelColumn excelColumn = createExcelColumn("Name", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createDefaultSheetInfo();

        CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, stringField, sheetInfo);

        assertNotNull(columnStyle);
        assertInstanceOf(DefaultColumnStyle.class, columnStyle);
    }

    @Test
    void resolveColumnStyle_withCustomStyle_returnsStyleInstance() throws NoSuchFieldException {
        Field stringField = TestDTO.class.getDeclaredField("name");
        ExcelColumn excelColumn = createExcelColumn("Name", 0, 0, DefaultHeaderStyle.class, TestColumnStyle.class);
        SheetInfo sheetInfo = createDefaultSheetInfo();

        CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, stringField, sheetInfo);

        assertNotNull(columnStyle);
        assertInstanceOf(TestColumnStyle.class, columnStyle);
    }

    @Test
    void resolveHeaderStyle_withSheetDefaultHeaderStyle_returnsSheetDefaultStyle() {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createSheetInfoWithStyles(TestHeaderStyle.class, DefaultColumnStyle.class);

        CustomExcelCellStyle headerStyle = ColumnStyleResolver.resolveHeaderStyle(excelColumn, sheetInfo);

        assertNotNull(headerStyle);
        assertInstanceOf(TestHeaderStyle.class, headerStyle);
    }

    @Test
    void resolveHeaderStyle_columnStyleOverridesSheetStyle_returnsColumnStyle() {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 0, TestColumnStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createSheetInfoWithStyles(TestHeaderStyle.class, DefaultColumnStyle.class);

        CustomExcelCellStyle headerStyle = ColumnStyleResolver.resolveHeaderStyle(excelColumn, sheetInfo);

        assertNotNull(headerStyle);
        assertInstanceOf(TestColumnStyle.class, headerStyle);
    }

    @Test
    void resolveColumnStyle_withSheetDefaultColumnStyle_returnsSheetDefaultStyle() throws NoSuchFieldException {
        Field stringField = TestDTO.class.getDeclaredField("name");
        ExcelColumn excelColumn = createExcelColumn("Name", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createSheetInfoWithStyles(DefaultHeaderStyle.class, TestColumnStyle.class);

        CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, stringField, sheetInfo);

        assertNotNull(columnStyle);
        assertInstanceOf(TestColumnStyle.class, columnStyle);
    }

    @Test
    void resolveColumnStyle_columnStyleOverridesSheetStyle_returnsColumnStyle() throws NoSuchFieldException {
        Field stringField = TestDTO.class.getDeclaredField("name");
        ExcelColumn excelColumn = createExcelColumn("Name", 0, 0, DefaultHeaderStyle.class, TestHeaderStyle.class);
        SheetInfo sheetInfo = createSheetInfoWithStyles(DefaultHeaderStyle.class, TestColumnStyle.class);

        CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, stringField, sheetInfo);

        assertNotNull(columnStyle);
        assertInstanceOf(TestHeaderStyle.class, columnStyle);
    }

    @Test
    void resolveColumnStyle_sheetDefaultStyleWithNumericType_returnsSheetStyle() throws NoSuchFieldException {
        Field numericField = TestDTO.class.getDeclaredField("age");
        ExcelColumn excelColumn = createExcelColumn("Age", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createSheetInfoWithStyles(DefaultHeaderStyle.class, TestColumnStyle.class);

        CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, numericField, sheetInfo);

        assertNotNull(columnStyle);
        assertInstanceOf(TestColumnStyle.class, columnStyle);
    }

    @Test
    void resolveColumnStyle_noSheetStyleWithNumericType_returnsDefaultNumberStyle() throws NoSuchFieldException {
        Field numericField = TestDTO.class.getDeclaredField("age");
        ExcelColumn excelColumn = createExcelColumn("Age", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        SheetInfo sheetInfo = createDefaultSheetInfo();

        CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, numericField, sheetInfo);

        assertNotNull(columnStyle);
        assertInstanceOf(DefaultNumberStyle.class, columnStyle);
    }

    @Test
    void calculateWidth_withNonDefaultAnnotationWidth_returnsAnnotationWidth() {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 150, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        CustomExcelCellStyle columnStyle = new DefaultColumnStyle();

        int width = ColumnStyleResolver.calculateWidth(excelColumn, columnStyle);

        assertEquals(150, width);
    }

    @Test
    void calculateWidth_withAutoWidth_returnsMinusOne() {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        CustomExcelCellStyle columnStyle = new TestAutoWidthStyle();

        int width = ColumnStyleResolver.calculateWidth(excelColumn, columnStyle);

        assertEquals(-1, width);
    }

    @Test
    void calculateWidth_withStyleWidth_returnsStyleWidth() {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        CustomExcelCellStyle columnStyle = new TestFixedWidthStyle();

        int width = ColumnStyleResolver.calculateWidth(excelColumn, columnStyle);

        assertEquals(200, width);
    }

    @Test
    void calculateWidth_withDefaultValues_returnsDefaultWidth() {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 0, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        CustomExcelCellStyle columnStyle = new DefaultColumnStyle();

        int width = ColumnStyleResolver.calculateWidth(excelColumn, columnStyle);

        assertEquals(100, width);
    }

    @Test
    void calculateWidth_withExplicitWidth100_returnsExactly100() {
        ExcelColumn excelColumn = createExcelColumn("Header", 0, 100, DefaultHeaderStyle.class, DefaultColumnStyle.class);
        CustomExcelCellStyle columnStyle = new TestFixedWidthStyle();

        int width = ColumnStyleResolver.calculateWidth(excelColumn, columnStyle);

        assertEquals(100, width);
    }

    private SheetInfo createDefaultSheetInfo() {
        return SheetInfo.builder()
                .name("Sheet1")
                .hasHeader(true)
                .order(Integer.MIN_VALUE)
                .defaultHeaderStyle(DefaultHeaderStyle.class)
                .defaultColumnStyle(DefaultColumnStyle.class)
                .build();
    }

    private SheetInfo createSheetInfoWithStyles(
            Class<? extends CustomExcelCellStyle> defaultHeaderStyle,
            Class<? extends CustomExcelCellStyle> defaultColumnStyle) {
        return SheetInfo.builder()
                .name("Sheet1")
                .hasHeader(true)
                .order(Integer.MIN_VALUE)
                .defaultHeaderStyle(defaultHeaderStyle)
                .defaultColumnStyle(defaultColumnStyle)
                .build();
    }

    private ExcelColumn createExcelColumn(
            String header,
            int order,
            int width,
            Class<? extends CustomExcelCellStyle> headerStyle,
            Class<? extends CustomExcelCellStyle> columnStyle) {
        return new ExcelColumn() {
            @Override
            public String header() {
                return header;
            }

            @Override
            public int order() {
                return order;
            }

            @Override
            public int width() {
                return width;
            }

            @Override
            public String format() {
                return "";
            }

            @Override
            public Class<? extends CustomExcelCellStyle> headerStyle() {
                return headerStyle;
            }

            @Override
            public Class<? extends CustomExcelCellStyle> columnStyle() {
                return columnStyle;
            }

            @Override
            public ConditionalStyle[] conditionalStyles() {
                return new ConditionalStyle[0];
            }

            @Override
            public boolean exclude() {
                return false;
            }

            @Override
            public String sheetName() {
                return "";
            }

            @Override
            public Class<ExcelColumn> annotationType() {
                return ExcelColumn.class;
            }

            @Override
            public Masking masking() {
                return Masking.NONE;
            }

        };
    }

    private static class TestDTO {
        private String name;
        private Integer age;
    }

    public static class TestHeaderStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

    public static class TestColumnStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

    public static class TestAutoWidthStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }

        @Override
        public boolean isAutoWidth() {
            return true;
        }
    }

    public static class TestFixedWidthStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }

        @Override
        public int getColumnWidth() {
            return 200;
        }
    }
}
