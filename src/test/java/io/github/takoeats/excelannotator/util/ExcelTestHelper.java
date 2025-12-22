package io.github.takoeats.excelannotator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public final class ExcelTestHelper {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private ExcelTestHelper() {
    }

    public static void assertCellValue(Cell cell, String expected) {
        assertNotNull(cell, "Cell should not be null");
        String actual = FORMATTER.formatCellValue(cell);
        assertEquals(expected, actual, "Cell value mismatch");
    }

    public static void assertCellNumericValue(Cell cell, double expected) {
        assertNotNull(cell, "Cell should not be null");
        assertEquals(expected, cell.getNumericCellValue(), 0.001, "Numeric cell value mismatch");
    }

    public static void assertRgbColor(Cell cell, int r, int g, int b) {
        assertNotNull(cell, "Cell should not be null");
        CellStyle style = cell.getCellStyle();
        assertNotNull(style, "Cell style should not be null");

        if (style instanceof XSSFCellStyle) {
            XSSFCellStyle xssfStyle = (XSSFCellStyle) style;
            XSSFColor fillColor = xssfStyle.getFillForegroundColorColor();

            if (fillColor != null) {
                byte[] rgb = fillColor.getRGB();
                if (rgb != null && rgb.length >= 3) {
                    int actualR = rgb[0] & 0xFF;
                    int actualG = rgb[1] & 0xFF;
                    int actualB = rgb[2] & 0xFF;

                    assertEquals(r, actualR, "Red component mismatch");
                    assertEquals(g, actualG, "Green component mismatch");
                    assertEquals(b, actualB, "Blue component mismatch");
                }
            }
        }
    }

    public static Workbook workbookFromBytes(byte[] bytes) {
        try {
            return WorkbookFactory.create(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create workbook from bytes", e);
        }
    }
}
