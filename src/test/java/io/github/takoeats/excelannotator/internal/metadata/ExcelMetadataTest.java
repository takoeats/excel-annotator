package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.rule.StyleRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ExcelMetadataTest {

    private SheetInfo testSheetInfo;
    private List<ColumnInfo> testColumnInfos;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        testSheetInfo = SheetInfo.builder()
                .name("TestSheet")
                .hasHeader(true)
                .order(1)
                .build();

        Field mockField1 = String.class.getDeclaredField("value");
        Field mockField2 = String.class.getDeclaredField("hash");

        ColumnInfo col1 = new ColumnInfo(
                "Name",
                1,
                100,
                "",
                mockField1,
                null,
                null,
                Collections.emptyList(),
                "Sheet1"
        );

        ColumnInfo col2 = new ColumnInfo(
                "Age",
                2,
                50,
                "#,##0",
                mockField2,
                null,
                null,
                Arrays.asList(StyleRule.builder().priority(1).build()),
                "Sheet1"
        );

        testColumnInfos = Arrays.asList(col1, col2);
    }

    @Test
    void builder_withAllParameters_createsInstanceSuccessfully() {
        List<String> headers = Arrays.asList("Header1", "Header2");
        List<Function<Object, Object>> extractors = Arrays.asList(o -> "value1", o -> "value2");
        List<Integer> widths = Arrays.asList(100, 150);

        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .headers(headers)
                .extractors(extractors)
                .columnWidths(widths)
                .sheetInfo(testSheetInfo)
                .columnInfos(testColumnInfos)
                .build();

        assertNotNull(metadata);
        assertEquals(headers, metadata.getHeaders());
        assertEquals(extractors, metadata.getExtractors());
        assertEquals(widths, metadata.getColumnWidths());
        assertEquals(testSheetInfo, metadata.getSheetInfo());
        assertEquals(testColumnInfos, metadata.getColumnInfos());
    }

    @Test
    void getSheetName_returnsSheetInfoName() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .sheetInfo(testSheetInfo)
                .build();

        assertEquals("TestSheet", metadata.getSheetName());
    }

    @Test
    void hasHeader_returnsSheetInfoHasHeader() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .sheetInfo(testSheetInfo)
                .build();

        assertTrue(metadata.hasHeader());
    }

    @Test
    void getHeaderStyleAt_withValidIndex_returnsHeaderStyle() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertNull(metadata.getHeaderStyleAt(0));
        assertNull(metadata.getHeaderStyleAt(1));
    }

    @Test
    void getHeaderStyleAt_withInvalidIndex_returnsNull() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertNull(metadata.getHeaderStyleAt(-1));
        assertNull(metadata.getHeaderStyleAt(2));
        assertNull(metadata.getHeaderStyleAt(100));
    }

    @Test
    void getHeaderStyleAt_withNullColumnInfos_returnsNull() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(null)
                .build();

        assertNull(metadata.getHeaderStyleAt(0));
    }

    @Test
    void getColumnStyleAt_withValidIndex_returnsColumnStyle() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertNull(metadata.getColumnStyleAt(0));
        assertNull(metadata.getColumnStyleAt(1));
    }

    @Test
    void getColumnStyleAt_withInvalidIndex_returnsNull() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertNull(metadata.getColumnStyleAt(-1));
        assertNull(metadata.getColumnStyleAt(2));
    }

    @Test
    void getConditionalStyleRulesAt_withValidIndex_returnsRules() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertTrue(metadata.getConditionalStyleRulesAt(0).isEmpty());
        assertEquals(1, metadata.getConditionalStyleRulesAt(1).size());
    }

    @Test
    void getConditionalStyleRulesAt_withInvalidIndex_returnsEmptyList() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertTrue(metadata.getConditionalStyleRulesAt(-1).isEmpty());
        assertTrue(metadata.getConditionalStyleRulesAt(2).isEmpty());
    }

    @Test
    void getFieldNameAt_withValidIndex_returnsFieldName() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertEquals("value", metadata.getFieldNameAt(0));
        assertEquals("hash", metadata.getFieldNameAt(1));
    }

    @Test
    void getFieldNameAt_withInvalidIndex_returnsNull() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertNull(metadata.getFieldNameAt(-1));
        assertNull(metadata.getFieldNameAt(2));
    }

    @Test
    void getMinOrder_withColumnInfos_returnsMinimumOrder() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertEquals(1, metadata.getMinOrder());
    }

    @Test
    void getMinOrder_withEmptyColumnInfos_returnsMaxValue() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(Collections.emptyList())
                .build();

        assertEquals(Integer.MAX_VALUE, metadata.getMinOrder());
    }

    @Test
    void getMinOrder_withNullColumnInfos_returnsMaxValue() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(null)
                .build();

        assertEquals(Integer.MAX_VALUE, metadata.getMinOrder());
    }

    @Test
    void getAllOrders_withColumnInfos_returnsOrderSet() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        Set<Integer> orders = metadata.getAllOrders();
        assertEquals(2, orders.size());
        assertTrue(orders.contains(1));
        assertTrue(orders.contains(2));
    }

    @Test
    void getAllOrders_withEmptyColumnInfos_returnsEmptySet() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(Collections.emptyList())
                .build();

        assertTrue(metadata.getAllOrders().isEmpty());
    }

    @Test
    void getAllOrders_withNullColumnInfos_returnsEmptySet() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(null)
                .build();

        assertTrue(metadata.getAllOrders().isEmpty());
    }

    @Test
    void getColumnCount_withColumnInfos_returnsSize() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertEquals(2, metadata.getColumnCount());
    }

    @Test
    void getColumnCount_withNullColumnInfos_returnsZero() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(null)
                .build();

        assertEquals(0, metadata.getColumnCount());
    }

    @Test
    void getFormatAt_withValidIndex_returnsFormat() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertEquals("", metadata.getFormatAt(0));
        assertEquals("#,##0", metadata.getFormatAt(1));
    }

    @Test
    void getFormatAt_withInvalidIndex_returnsNull() {
        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(testColumnInfos)
                .build();

        assertNull(metadata.getFormatAt(-1));
        assertNull(metadata.getFormatAt(2));
    }

    @Test
    void getHeaderStyleAt_withActualStyle_returnsStyle() throws NoSuchFieldException {
        Field mockField = String.class.getDeclaredField("value");
        CustomExcelCellStyle headerStyle = new TestHeaderStyle();

        ColumnInfo col1 = new ColumnInfo(
                "Name",
                1,
                100,
                "",
                mockField,
                headerStyle,
                null,
                Collections.emptyList(),
                "Sheet1"
        );

        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(Collections.singletonList(col1))
                .build();

        CustomExcelCellStyle result = metadata.getHeaderStyleAt(0);
        assertNotNull(result);
        assertInstanceOf(TestHeaderStyle.class, result);
        assertSame(headerStyle, result);
    }

    @Test
    void getColumnStyleAt_withActualStyle_returnsStyle() throws NoSuchFieldException {
        Field mockField = String.class.getDeclaredField("value");
        CustomExcelCellStyle columnStyle = new TestColumnStyle();

        ColumnInfo col1 = new ColumnInfo(
                "Name",
                1,
                100,
                "",
                mockField,
                null,
                columnStyle,
                Collections.emptyList(),
                "Sheet1"
        );

        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(Collections.singletonList(col1))
                .build();

        CustomExcelCellStyle result = metadata.getColumnStyleAt(0);
        assertNotNull(result);
        assertInstanceOf(TestColumnStyle.class, result);
        assertSame(columnStyle, result);
    }

    @Test
    void getHeaderStyleAt_withMultipleColumns_returnCorrectStyleForEachIndex() throws NoSuchFieldException {
        Field mockField1 = String.class.getDeclaredField("value");
        Field mockField2 = String.class.getDeclaredField("hash");

        CustomExcelCellStyle headerStyle1 = new TestHeaderStyle();
        CustomExcelCellStyle headerStyle2 = new TestHeaderStyle2();

        ColumnInfo col1 = new ColumnInfo(
                "Name", 1, 100, "", mockField1,
                headerStyle1, null, Collections.emptyList(), "Sheet1"
        );
        ColumnInfo col2 = new ColumnInfo(
                "Age", 2, 50, "", mockField2,
                headerStyle2, null, Collections.emptyList(), "Sheet1"
        );

        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(Arrays.asList(col1, col2))
                .build();

        assertSame(headerStyle1, metadata.getHeaderStyleAt(0));
        assertSame(headerStyle2, metadata.getHeaderStyleAt(1));
    }

    @Test
    void getColumnStyleAt_withMultipleColumns_returnCorrectStyleForEachIndex() throws NoSuchFieldException {
        Field mockField1 = String.class.getDeclaredField("value");
        Field mockField2 = String.class.getDeclaredField("hash");

        CustomExcelCellStyle columnStyle1 = new TestColumnStyle();
        CustomExcelCellStyle columnStyle2 = new TestColumnStyle2();

        ColumnInfo col1 = new ColumnInfo(
                "Name", 1, 100, "", mockField1,
                null, columnStyle1, Collections.emptyList(), "Sheet1"
        );
        ColumnInfo col2 = new ColumnInfo(
                "Age", 2, 50, "", mockField2,
                null, columnStyle2, Collections.emptyList(), "Sheet1"
        );

        ExcelMetadata<Object> metadata = ExcelMetadata.<Object>builder()
                .columnInfos(Arrays.asList(col1, col2))
                .build();

        assertSame(columnStyle1, metadata.getColumnStyleAt(0));
        assertSame(columnStyle2, metadata.getColumnStyleAt(1));
    }

    private static class TestHeaderStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

    private static class TestHeaderStyle2 extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

    private static class TestColumnStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

    private static class TestColumnStyle2 extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }
}
