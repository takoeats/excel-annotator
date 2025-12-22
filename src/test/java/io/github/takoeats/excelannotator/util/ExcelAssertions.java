package io.github.takoeats.excelannotator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public final class ExcelAssertions {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private ExcelAssertions() {
    }

    public static void assertExcelFileValid(byte[] bytes) {
        assertNotNull(bytes, "Excel bytes should not be null");
        assertTrue(bytes.length > 0, "Excel bytes should not be empty");

        try {
            Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes));
            assertNotNull(wb, "Workbook should be created successfully");
            assertTrue(wb.getNumberOfSheets() > 0, "Workbook should have at least one sheet");
            wb.close();
        } catch (Exception e) {
            throw new AssertionError("Failed to create valid workbook from bytes", e);
        }
    }

    public static void assertSheetCount(Workbook wb, int expected) {
        assertNotNull(wb, "Workbook should not be null");
        assertEquals(expected, wb.getNumberOfSheets(), "Sheet count mismatch");
    }

    public static void assertSheetName(Workbook wb, int sheetIndex, String expectedName) {
        assertNotNull(wb, "Workbook should not be null");
        assertTrue(sheetIndex < wb.getNumberOfSheets(),
            "Sheet index out of bounds");
        Sheet sheet = wb.getSheetAt(sheetIndex);
        assertEquals(expectedName, sheet.getSheetName(), "Sheet name mismatch");
    }

    public static void assertColumnHeader(Sheet sheet, int colIndex, String expected) {
        assertNotNull(sheet, "Sheet should not be null");
        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow, "Header row should not be null");
        String actual = FORMATTER.formatCellValue(headerRow.getCell(colIndex));
        assertEquals(expected, actual,
            String.format("Header mismatch at column %d", colIndex));
    }

    public static void assertRowCount(Sheet sheet, int expected) {
        assertNotNull(sheet, "Sheet should not be null");
        int lastRowNum = sheet.getLastRowNum();
        int actualRows = lastRowNum + 1;
        assertEquals(expected, actualRows,
            "Row count mismatch (including header row)");
    }

    public static void assertCellValueAt(Sheet sheet, int rowIndex, int colIndex, String expected) {
        assertNotNull(sheet, "Sheet should not be null");
        Row row = sheet.getRow(rowIndex);
        assertNotNull(row, String.format("Row %d should not be null", rowIndex));
        String actual = FORMATTER.formatCellValue(row.getCell(colIndex));
        assertEquals(expected, actual,
            String.format("Cell value mismatch at row %d, col %d", rowIndex, colIndex));
    }
}
