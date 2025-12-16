package com.junho.excel.internal.metadata.extractor;

import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.metadata.SheetInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SheetInfoExtractorTest {

    @Test
    void extract_withExcelSheetAnnotation_returnsSheetInfo() {
        SheetInfo sheetInfo = SheetInfoExtractor.extract(TestDTO.class);

        assertNotNull(sheetInfo);
        assertEquals("TestSheet", sheetInfo.getName());
        assertTrue(sheetInfo.isHasHeader());
        assertEquals(10, sheetInfo.getOrder());
    }

    @Test
    void extract_withMinimalExcelSheet_returnsSheetInfo() {
        SheetInfo sheetInfo = SheetInfoExtractor.extract(MinimalDTO.class);

        assertNotNull(sheetInfo);
        assertEquals("Minimal", sheetInfo.getName());
    }

    @Test
    void extract_withoutExcelSheetAnnotation_throwsException() {
        assertThrows(ExcelExporterException.class, () -> {
            SheetInfoExtractor.extract(NoAnnotationDTO.class);
        });
    }

    @ExcelSheet(value = "TestSheet", hasHeader = true, order = 10)
    private static class TestDTO {
        private String name;
    }

    @ExcelSheet("Minimal")
    private static class MinimalDTO {
        private String field;
    }

    private static class NoAnnotationDTO {
        private String field;
    }
}
