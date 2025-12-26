package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.masking.Masking;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MetadataAssemblerTest {

    @Test
    void assemble_withValidDTO_returnsExcelMetadata() {
        ExcelMetadata<TestDTO> metadata = MetadataAssembler.assemble(TestDTO.class);

        assertNotNull(metadata);
        assertEquals("TestSheet", metadata.getSheetName());
        assertTrue(metadata.hasHeader());
        assertEquals(2, metadata.getHeaders().size());
        assertEquals(2, metadata.getExtractors().size());
        assertEquals(2, metadata.getColumnWidths().size());
        assertEquals(2, metadata.getColumnInfos().size());
    }

    @Test
    void assemble_withNullClass_returnsEmptyMetadata() {
        ExcelMetadata<Object> metadata = MetadataAssembler.assemble(null);

        assertNotNull(metadata);
        assertTrue(metadata.getHeaders().isEmpty());
        assertTrue(metadata.getExtractors().isEmpty());
        assertTrue(metadata.getColumnWidths().isEmpty());
        assertTrue(metadata.getColumnInfos().isEmpty());
    }

    @Test
    void assemble_preservesColumnOrder() {
        ExcelMetadata<TestDTO> metadata = MetadataAssembler.assemble(TestDTO.class);

        assertEquals("Age", metadata.getHeaders().get(0));
        assertEquals("Name", metadata.getHeaders().get(1));
    }

    @Test
    void assemble_generatesExtractors() {
        ExcelMetadata<TestDTO> metadata = MetadataAssembler.assemble(TestDTO.class);

        TestDTO dto = new TestDTO();
        dto.setName("John");
        dto.setAge(30);

        Object nameValue = metadata.getExtractors().get(1).apply(dto);
        Object ageValue = metadata.getExtractors().get(0).apply(dto);

        assertEquals("John", nameValue);
        assertEquals(30, ageValue);
    }

    @Test
    void assembleFromMergedColumns_createsMetadataForMergedData() throws NoSuchFieldException {
        Map<Integer, ColumnInfo> mergedColumns = getIntegerColumnInfoMap();

        ExcelMetadata<Map<String, Object>> metadata = MetadataAssembler.assembleFromMergedColumns(
                "MergedSheet",
                mergedColumns,
                true
        );

        assertNotNull(metadata);
        assertEquals("MergedSheet", metadata.getSheetName());
        assertTrue(metadata.hasHeader());
        assertEquals(2, metadata.getHeaders().size());
        assertEquals("Name", metadata.getHeaders().get(0));
        assertEquals("Age", metadata.getHeaders().get(1));
    }

    private static Map<Integer, ColumnInfo> getIntegerColumnInfoMap() throws NoSuchFieldException {
        Map<Integer, ColumnInfo> mergedColumns = new LinkedHashMap<>();

        ColumnInfo col1 = ColumnInfo.builder()
                .header("Name")
                .order(1)
                .width(100)
                .format("")
                .field(String.class.getDeclaredField("value"))
                .headerStyle(null)
                .columnStyle(null)
                .conditionalStyleRules(null)
                .sheetName(null)
                .masking(Masking.NONE)
                .build();

        ColumnInfo col2 = ColumnInfo.builder()
                .header("Age")
                .order(2)
                .width(50)
                .format("")
                .field(Integer.class.getDeclaredField("value"))
                .headerStyle(null)
                .columnStyle(null)
                .conditionalStyleRules(null)
                .sheetName(null)
                .masking(Masking.NONE)
                .build();

        mergedColumns.put(1, col1);
        mergedColumns.put(2, col2);
        return mergedColumns;
    }

    @Test
    void assembleFromMergedColumns_generatesMapExtractors() throws NoSuchFieldException {
        Map<Integer, ColumnInfo> mergedColumns = new LinkedHashMap<>();

        ColumnInfo col1 = ColumnInfo.builder()
                .header("Data")
                .order(1)
                .width(100)
                .format("")
                .field(String.class.getDeclaredField("value"))
                .headerStyle(null)
                .columnStyle(null)
                .conditionalStyleRules(null)
                .sheetName(null)
                .masking(Masking.NONE)
                .build();

        mergedColumns.put(1, col1);

        ExcelMetadata<Map<String, Object>> metadata = MetadataAssembler.assembleFromMergedColumns(
                "Sheet",
                mergedColumns,
                false
        );

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("0", "TestValue");

        Object value = metadata.getExtractors().get(0).apply(row);
        assertEquals("TestValue", value);
    }

    @ExcelSheet(value = "TestSheet", hasHeader = true)
    public static class TestDTO {
        @ExcelColumn(header = "Name", order = 2)
        private String name;

        @ExcelColumn(header = "Age", order = 1)
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
