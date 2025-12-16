package com.junho.excel.internal.metadata;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.rule.StyleRule;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColumnInfoTest {

    @Test
    void constructor_withAllParameters_createsInstanceSuccessfully() throws NoSuchFieldException {
        Field mockField = String.class.getDeclaredField("value");
        CustomExcelCellStyle headerStyle = null;
        CustomExcelCellStyle columnStyle = null;
        List<StyleRule> rules = Collections.emptyList();

        ColumnInfo columnInfo = new ColumnInfo(
                "Test Header",
                1,
                100,
                "#,##0",
                mockField,
                headerStyle,
                columnStyle,
                rules,
                "Sheet1"
        );

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
    void constructor_withNullConditionalStyleRules_convertsToEmptyList() throws NoSuchFieldException {
        Field mockField = String.class.getDeclaredField("value");

        ColumnInfo columnInfo = new ColumnInfo(
                "Header",
                0,
                50,
                "",
                mockField,
                null,
                null,
                null,
                null
        );

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

        ColumnInfo columnInfo = new ColumnInfo(
                "Amount",
                5,
                150,
                "#,##0.00",
                mockField,
                null,
                null,
                rules,
                "Data"
        );

        assertEquals("Amount", columnInfo.getHeader());
        assertEquals(5, columnInfo.getOrder());
        assertEquals(150, columnInfo.getWidth());
        assertEquals("#,##0.00", columnInfo.getFormat());
        assertEquals(mockField, columnInfo.getField());
        assertEquals(2, columnInfo.getConditionalStyleRules().size());
        assertEquals("Data", columnInfo.getSheetName());
    }
}
