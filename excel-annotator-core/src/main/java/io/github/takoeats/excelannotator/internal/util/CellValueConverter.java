package io.github.takoeats.excelannotator.internal.util;

import io.github.takoeats.excelannotator.internal.util.strategy.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CellValueConverter {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[-+]?\\d+(\\.\\d+)?");
    private static final int EXCEL_MAX_PRECISION_DIGITS = 15;

    private static final List<CellValueStrategy> STRATEGIES = Arrays.asList(
            new StringValueStrategy(),
            new LocalDateValueStrategy(),
            new LocalDateTimeValueStrategy(),
            new DateValueStrategy(),
            new CalendarValueStrategy(),
            new NumberValueStrategy(),
            new BooleanValueStrategy(),
            new DefaultValueStrategy()
    );

    public static void setCellValue(Cell cell, String value) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            cell.setBlank();
            return;
        }

        if (isNumericLike(trimmed)) {
            if (isTooLargeIntegerForExcel(trimmed)) {
                cell.setCellValue(trimmed);
            } else {
                cell.setCellValue(toDoubleSafe(trimmed));
            }
        } else {
            cell.setCellValue(trimmed);
        }
    }

    public static boolean isNumericLike(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        char firstChar = s.charAt(0);
        if (!Character.isDigit(firstChar) && firstChar != '-' && firstChar != '+' && firstChar != '.') {
            return false;
        }

        String normalized = normalizeNumber(s);
        return NUMERIC_PATTERN.matcher(normalized).matches();
    }

    public static boolean isTooLargeIntegerForExcel(String s) {
        String normalized = normalizeNumber(s);
        int dotIndex = normalized.indexOf('.');
        String intPart = dotIndex >= 0 ? normalized.substring(0, dotIndex) : normalized;

        if (intPart.startsWith("+") || intPart.startsWith("-")) {
            intPart = intPart.substring(1);
        }

        return intPart.length() > EXCEL_MAX_PRECISION_DIGITS;
    }

    public static double toDoubleSafe(String s) {
        try {
            String normalized = normalizeNumber(s);
            return new BigDecimal(normalized).doubleValue();
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    public static String normalizeNumber(String s) {
        return s.replace(",", "")
                .replace("_", "")
                .replace(" ", "");
    }

    public static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return getFormulaCellValueAsString(cell);
            default:
                return "";
        }
    }

    private static String getFormulaCellValueAsString(Cell cell) {
        switch (cell.getCachedFormulaResultType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public static void setCellValueSafely(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        for (CellValueStrategy strategy : STRATEGIES) {
            if (strategy.supports(value)) {
                strategy.apply(cell, value);
                return;
            }
        }
    }
}
