package io.github.takoeats.excelannotator.internal.util;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;

public final class SheetNameValidator {

    private static final int MAX_SHEET_NAME_LENGTH = 31;

    private SheetNameValidator() {
    }

    public static String validateAndSanitize(String sheetName) {
        if (sheetName == null || sheetName.trim().isEmpty()) {
            throw new ExcelExporterException(ErrorCode.INVALID_SHEET_NAME, "시트 이름이 비어있습니다.");
        }

        String sanitized = sheetName.trim();

        sanitized = sanitized.replace("..", "");
        sanitized = sanitized.replaceAll("[:\\\\/<>\"?*|\\[\\]]", "");
        sanitized = sanitized.replaceAll("[\u0000-\u001F\u007F]", "");

        if (sanitized.isEmpty()) {
            throw new ExcelExporterException(ErrorCode.INVALID_SHEET_NAME,
                    "시트 이름이 유효하지 않은 문자만 포함하고 있습니다.");
        }

        if (sanitized.length() > MAX_SHEET_NAME_LENGTH) {
            sanitized = sanitized.substring(0, MAX_SHEET_NAME_LENGTH);
        }

        return sanitized;
    }
}
