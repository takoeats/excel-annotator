package io.github.takoeats.excelannotator.internal.metadata.extractor;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColumnInfoExtractorTest {

    @Test
    void extractAll_withValidDTO_returnsColumnInfoList() {
        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(TestDTO.class);

        assertNotNull(columnInfos);
        assertEquals(2, columnInfos.size());
    }

    @Test
    void extractAll_sortsColumnsByOrder() {
        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(TestDTO.class);

        assertEquals("Age", columnInfos.get(0).getHeader());
        assertEquals(1, columnInfos.get(0).getOrder());
        assertEquals("Name", columnInfos.get(1).getHeader());
        assertEquals(2, columnInfos.get(1).getOrder());
    }

    @Test
    void extractAll_excludesFieldsWithExcludeTrue() {
        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(TestDTO.class);

        boolean hasExcludedField = columnInfos.stream()
                .anyMatch(info -> "Excluded".equals(info.getHeader()));
        assertFalse(hasExcludedField);
    }

    @Test
    void extractAll_ignoresFieldsWithoutExcelColumn() {
        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(TestDTO.class);

        boolean hasNoAnnotationField = columnInfos.stream()
                .anyMatch(info -> info.getField().getName().equals("noAnnotation"));
        assertFalse(hasNoAnnotationField);
    }

    @Test
    void extractAll_withNullClass_returnsEmptyList() {
        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(null);

        assertNotNull(columnInfos);
        assertTrue(columnInfos.isEmpty());
    }

    @Test
    void extractAll_withClassWithoutFields_returnsEmptyList() {
        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(EmptyDTO.class);

        assertNotNull(columnInfos);
        assertTrue(columnInfos.isEmpty());
    }

    @Test
    void extractAll_preservesColumnInfo() {
        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(DetailedDTO.class);

        assertEquals(1, columnInfos.size());
        ColumnInfo info = columnInfos.get(0);

        assertEquals("Amount", info.getHeader());
        assertEquals(1, info.getOrder());
        assertEquals(150, info.getWidth());
        assertEquals("#,##0.00", info.getFormat());
        assertEquals("amount", info.getField().getName());
        assertEquals("DataSheet", info.getSheetName());
    }

    @ExcelSheet("Test")
    private static class TestDTO {
        @ExcelColumn(header = "Name", order = 2)
        private String name;

        @ExcelColumn(header = "Age", order = 1)
        private Integer age;

        @ExcelColumn(header = "Excluded", order = 3, exclude = true)
        private String excluded;

        private String noAnnotation;
    }

    @ExcelSheet("Empty")
    private static class EmptyDTO {
    }

    @ExcelSheet("Detailed")
    private static class DetailedDTO {
        @ExcelColumn(header = "Amount", order = 1, width = 150, format = "#,##0.00", sheetName = "DataSheet")
        private Double amount;
    }
}
