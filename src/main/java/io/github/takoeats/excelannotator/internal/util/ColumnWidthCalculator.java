package io.github.takoeats.excelannotator.internal.util;

import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class ColumnWidthCalculator {

    private static final int EXCEL_CHAR_WIDTH = 256;
    private static final int CALIBRATION_FACTOR = 32;
    private static final int PADDING_REDUCTION_CHARS = 5;
    private static final int MIN_COLUMN_WIDTH = 256;
    private static final int LARGE_DATA_THRESHOLD = 10000;

    private ColumnWidthCalculator() {
    }

    public static <T> void applyAutoWidthColumns(Sheet sheet,
                                                 ExcelMetadata<T> metadata) {
        int totalRows = sheet.getLastRowNum() + 1;

        if (totalRows > LARGE_DATA_THRESHOLD) {
            for (int colIndex = 0; colIndex < metadata.getColumnWidths().size(); colIndex++) {
                if (metadata.getColumnWidths().get(colIndex) == -1) {
                    applySampledAutoSize(sheet, colIndex);
                }
            }
            return;
        }

        for (int i = 0; i < metadata.getColumnWidths().size(); i++) {
            if (metadata.getColumnWidths().get(i) == -1) {
                applyAutoSizeToColumn(sheet, i);
            }
        }
    }

    public static void applySampledAutoSize(Sheet sheet, int columnIndex) {
        int maxWidth = 0;
        int rowsToScan = Math.min(LARGE_DATA_THRESHOLD, sheet.getLastRowNum() + 1);

        for (int rowIndex = 0; rowIndex < rowsToScan; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null) {
                    int cellWidth = estimateCellWidth(cell);
                    maxWidth = Math.max(maxWidth, cellWidth);
                }
            }
        }

        int reducedWidth = Math.max(MIN_COLUMN_WIDTH,
                maxWidth - EXCEL_CHAR_WIDTH * PADDING_REDUCTION_CHARS);
        sheet.setColumnWidth(columnIndex, reducedWidth);
    }

    public static int estimateCellWidth(Cell cell) {
        String cellValue = CellValueConverter.getCellValueAsString(cell);

        int length = 0;
        for (char c : cellValue.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO ||
                    Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                length += 2;
            } else {
                length += 1;
            }
        }

        return (length * EXCEL_CHAR_WIDTH) + (EXCEL_CHAR_WIDTH * 2);
    }

    public static void applyAutoSizeToColumn(Sheet sheet, int columnIndex) {
        sheet.autoSizeColumn(columnIndex);
        int currentWidth = sheet.getColumnWidth(columnIndex);
        int reducedWidth = Math.max(MIN_COLUMN_WIDTH,
                currentWidth - EXCEL_CHAR_WIDTH * PADDING_REDUCTION_CHARS);
        sheet.setColumnWidth(columnIndex, reducedWidth);
    }

    public static <T> void applyFixedColumnWidths(Sheet sheet,
                                                  ExcelMetadata<T> metadata) {
        for (int i = 0; i < metadata.getColumnWidths().size(); i++) {
            int width = metadata.getColumnWidths().get(i);
            if (width > 0) {
                sheet.setColumnWidth(i, width * CALIBRATION_FACTOR);
            }
        }
    }

    public static int getCalibrationFactor() {
        return CALIBRATION_FACTOR;
    }
}
