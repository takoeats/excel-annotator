package io.github.takoeats.excelannotator.internal.util;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileNameProcessor {

    private static final String DEFAULT_FILE_NAME = "download";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static String processFileName(String fileName, String extension) {
        String sanitized = FilenameSecurityValidator.sanitizeFilename(fileName);
        return addTimestampIfDefault(sanitized, extension);
    }

    public static String sanitizeFileName(String fileName) {
        return FilenameSecurityValidator.sanitizeFilename(fileName);
    }

    public static String urlEncodeRFC5987(String fileName) {
        try {
            String encoded = URLEncoder.encode(fileName, "UTF-8");
            return encoded
                    .replace("+", "%20")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new ExcelExporterException(ErrorCode.IO_ERROR, "UTF-8 인코딩 지원 안 됨", e);
        }
    }

    private static String addTimestampIfDefault(String fileName, String extension) {
        String nameWithoutExt = removeExtension(fileName);

        if (DEFAULT_FILE_NAME.equals(nameWithoutExt)) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            return nameWithoutExt + "_" + timestamp + extension;
        }

        return ensureExtension(fileName, extension);
    }

    private static String ensureExtension(String fileName, String extension) {
        if (!fileName.endsWith(extension) || !fileName.contains(".")) {
            return removeExtension(fileName) + extension;
        }
        return fileName;
    }

    private static String removeExtension(String fileName) {
        return fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;
    }
}
