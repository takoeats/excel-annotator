package com.junho.excel.internal.metadata;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MultiSheetMetadataBuilderTest {

    private MultiSheetMetadataBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new MultiSheetMetadataBuilder();
    }

    @Test
    void build_withSingleSheetDTO_returnsSingleMetadataMap() {
        Map<String, ExcelMetadata<SingleSheetDTO>> metadataMap = builder.build(SingleSheetDTO.class);

        assertNotNull(metadataMap);
        assertEquals(1, metadataMap.size());
        assertTrue(metadataMap.containsKey("DefaultSheet"));
    }

    @Test
    void build_withMultiSheetDTO_returnsMultipleMetadataMap() {
        Map<String, ExcelMetadata<MultiSheetDTO>> metadataMap = builder.build(MultiSheetDTO.class);

        assertNotNull(metadataMap);
        assertEquals(3, metadataMap.size());
        assertTrue(metadataMap.containsKey("Sheet1"));
        assertTrue(metadataMap.containsKey("Sheet2"));
        assertTrue(metadataMap.containsKey("DefaultSheet"));
    }

    @Test
    void build_groupsColumnsBySheetName() {
        Map<String, ExcelMetadata<MultiSheetDTO>> metadataMap = builder.build(MultiSheetDTO.class);

        ExcelMetadata<MultiSheetDTO> sheet1 = metadataMap.get("Sheet1");
        ExcelMetadata<MultiSheetDTO> sheet2 = metadataMap.get("Sheet2");
        ExcelMetadata<MultiSheetDTO> defaultSheet = metadataMap.get("DefaultSheet");

        assertEquals(1, sheet1.getHeaders().size());
        assertEquals("Name", sheet1.getHeaders().get(0));

        assertEquals(1, sheet2.getHeaders().size());
        assertEquals("Age", sheet2.getHeaders().get(0));

        assertEquals(1, defaultSheet.getHeaders().size());
        assertEquals("Email", defaultSheet.getHeaders().get(0));
    }

    @Test
    void build_preservesColumnOrderWithinEachSheet() {
        Map<String, ExcelMetadata<OrderTestDTO>> metadataMap = builder.build(OrderTestDTO.class);

        ExcelMetadata<OrderTestDTO> metadata = metadataMap.get("Sheet1");

        assertEquals(2, metadata.getHeaders().size());
        assertEquals("Second", metadata.getHeaders().get(0));
        assertEquals("First", metadata.getHeaders().get(1));
    }

    @Test
    void build_withNullClass_returnsEmptyMap() {
        Map<String, ExcelMetadata<Object>> metadataMap = builder.build(null);

        assertNotNull(metadataMap);
        assertTrue(metadataMap.isEmpty());
    }

    @Test
    void build_preservesSheetInfoFromAnnotation() {
        Map<String, ExcelMetadata<MultiSheetDTO>> metadataMap = builder.build(MultiSheetDTO.class);

        for (ExcelMetadata<MultiSheetDTO> metadata : metadataMap.values()) {
            assertTrue(metadata.hasHeader());
            assertEquals(10, metadata.getSheetInfo().getOrder());
        }
    }

    @ExcelSheet(value = "DefaultSheet", hasHeader = true, order = 5)
    private static class SingleSheetDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(header = "Age", order = 2)
        private Integer age;
    }

    @ExcelSheet(value = "DefaultSheet", hasHeader = true, order = 10)
    private static class MultiSheetDTO {
        @ExcelColumn(header = "Name", order = 1, sheetName = "Sheet1")
        private String name;

        @ExcelColumn(header = "Age", order = 2, sheetName = "Sheet2")
        private Integer age;

        @ExcelColumn(header = "Email", order = 3)
        private String email;
    }

    @ExcelSheet("Sheet1")
    private static class OrderTestDTO {
        @ExcelColumn(header = "First", order = 2, sheetName = "Sheet1")
        private String first;

        @ExcelColumn(header = "Second", order = 1, sheetName = "Sheet1")
        private String second;
    }
}
