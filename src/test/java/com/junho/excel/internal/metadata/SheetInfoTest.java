package com.junho.excel.internal.metadata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SheetInfoTest {

    @Test
    void builder_withAllParameters_createsInstanceSuccessfully() {
        SheetInfo sheetInfo = SheetInfo.builder()
                .name("TestSheet")
                .hasHeader(true)
                .order(10)
                .build();

        assertNotNull(sheetInfo);
        assertEquals("TestSheet", sheetInfo.getName());
        assertTrue(sheetInfo.isHasHeader());
        assertEquals(10, sheetInfo.getOrder());
    }

    @Test
    void builder_withMinimalParameters_createsInstance() {
        SheetInfo sheetInfo = SheetInfo.builder()
                .name("Sheet1")
                .build();

        assertNotNull(sheetInfo);
        assertEquals("Sheet1", sheetInfo.getName());
    }

    @Test
    void hasOrder_withValidOrder_returnsTrue() {
        SheetInfo sheetInfo = SheetInfo.builder()
                .name("Sheet")
                .order(1)
                .build();

        assertTrue(sheetInfo.hasOrder());
    }

    @Test
    void hasOrder_withZeroOrder_returnsTrue() {
        SheetInfo sheetInfo = SheetInfo.builder()
                .name("Sheet")
                .order(0)
                .build();

        assertTrue(sheetInfo.hasOrder());
    }

    @Test
    void hasOrder_withNegativeOrder_returnsTrue() {
        SheetInfo sheetInfo = SheetInfo.builder()
                .name("Sheet")
                .order(-5)
                .build();

        assertTrue(sheetInfo.hasOrder());
    }

    @Test
    void hasOrder_withMinValueOrder_returnsFalse() {
        SheetInfo sheetInfo = SheetInfo.builder()
                .name("Sheet")
                .order(Integer.MIN_VALUE)
                .build();

        assertFalse(sheetInfo.hasOrder());
    }

    @Test
    void getters_returnCorrectValues() {
        SheetInfo sheetInfo = SheetInfo.builder()
                .name("DataSheet")
                .hasHeader(false)
                .order(100)
                .build();

        assertEquals("DataSheet", sheetInfo.getName());
        assertFalse(sheetInfo.isHasHeader());
        assertEquals(100, sheetInfo.getOrder());
        assertTrue(sheetInfo.hasOrder());
    }
}
