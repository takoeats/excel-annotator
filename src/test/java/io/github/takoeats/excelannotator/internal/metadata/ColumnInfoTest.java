package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.masking.Masking;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.rule.StyleRule;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColumnInfoTest {

    @Test
    void builder_withAllParameters_createsInstanceSuccessfully() throws NoSuchFieldException {
        Field mockField = String.class.getDeclaredField("value");
        CustomExcelCellStyle headerStyle = null;
        CustomExcelCellStyle columnStyle = null;
        List<StyleRule> rules = Collections.emptyList();

        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Test Header")
                .order(1)
                .width(100)
                .format("#,##0")
                .field(mockField)
                .headerStyle(headerStyle)
                .columnStyle(columnStyle)
                .conditionalStyleRules(rules)
                .sheetName("Sheet1")
                .masking(Masking.NONE)
                .build();

        assertNotNull(columnInfo);
        assertEquals("Test Header", columnInfo.getHeader());
        assertEquals(1, columnInfo.getOrder());
        assertEquals(100, columnInfo.getWidth());
        assertEquals("#,##0", columnInfo.getFormat());
        assertEquals(mockField, columnInfo.getField());
        assertNull(columnInfo.getHeaderStyle());
        assertNull(columnInfo.getColumnStyle());
        assertEquals(Collections.emptyList(), columnInfo.getConditionalStyleRules());
        assertEquals("Sheet1", columnInfo.getSheetName());
    }

    @Test
    void builder_withNullConditionalStyleRules_convertsToEmptyList() throws NoSuchFieldException {
        Field mockField = String.class.getDeclaredField("value");

        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Header")
                .order(0)
                .width(50)
                .format("")
                .field(mockField)
                .masking(Masking.NONE)
                .build();

        assertNotNull(columnInfo.getConditionalStyleRules());
        assertTrue(columnInfo.getConditionalStyleRules().isEmpty());
    }

    @Test
    void getters_returnCorrectValues() throws NoSuchFieldException {
        Field mockField = String.class.getDeclaredField("value");
        List<StyleRule> rules = Arrays.asList(
                StyleRule.builder().priority(1).build(),
                StyleRule.builder().priority(2).build()
        );

        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Amount")
                .order(5)
                .width(150)
                .format("#,##0.00")
                .field(mockField)
                .conditionalStyleRules(rules)
                .sheetName("Data")
                .masking(Masking.NONE)
                .build();

        assertEquals("Amount", columnInfo.getHeader());
        assertEquals(5, columnInfo.getOrder());
        assertEquals(150, columnInfo.getWidth());
        assertEquals("#,##0.00", columnInfo.getFormat());
        assertEquals(mockField, columnInfo.getField());
        assertEquals(2, columnInfo.getConditionalStyleRules().size());
        assertEquals("Data", columnInfo.getSheetName());
    }
}
