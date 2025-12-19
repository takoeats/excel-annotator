package com.junho.excel.internal.writer;

import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.metadata.ExcelMetadata;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CsvWriter {

    private static final String CSV_DELIMITER = ",";
    private static final String CSV_LINE_BREAK = "\r\n";
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public <T> void write(OutputStream outputStream, List<T> data) {
        validateData(data);
        Class<?> clazz = data.get(0).getClass();
        @SuppressWarnings("unchecked")
        ExcelMetadata<T> metadata = (ExcelMetadata<T>) ExcelMetadataFactory.extractExcelMetadata(clazz);
        writeFromIterator(outputStream, data.iterator(), metadata);
    }

    public <T> void write(OutputStream outputStream, Stream<T> dataStream) {
        Iterator<T> iterator = dataStream.iterator();
        if (!iterator.hasNext()) {
            throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
        }

        T firstElement = iterator.next();
        Class<?> clazz = firstElement.getClass();
        @SuppressWarnings("unchecked")
        ExcelMetadata<T> metadata = (ExcelMetadata<T>) ExcelMetadataFactory.extractExcelMetadata(clazz);

        Iterator<T> combinedIterator = prependToIterator(firstElement, iterator);
        writeFromIterator(outputStream, combinedIterator, metadata);
    }

    private <T> void writeFromIterator(OutputStream outputStream, Iterator<T> iterator,
                                       ExcelMetadata<T> metadata) {
        try {
            outputStream.write(UTF8_BOM);

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

                if (metadata.hasHeader()) {
                    writeHeader(writer, metadata.getHeaders());
                }

                writeDataRows(writer, iterator, metadata.getExtractors());
            }
        } catch (IOException e) {
            throw new ExcelExporterException(ErrorCode.IO_ERROR, "CSV 작성 중 오류 발생", e);
        }
    }

    private void writeHeader(BufferedWriter writer, List<String> headers) throws IOException {
        String headerLine = headers.stream()
                .map(this::escapeCsvValue)
                .collect(Collectors.joining(CSV_DELIMITER));
        writer.write(headerLine);
        writer.write(CSV_LINE_BREAK);
    }

    private <T> void writeDataRows(BufferedWriter writer, Iterator<T> iterator,
                                   List<Function<T, Object>> extractors) throws IOException {
        while (iterator.hasNext()) {
            T data = iterator.next();
            String rowLine = extractors.stream()
                    .map(extractor -> extractor.apply(data))
                    .map(this::convertToString)
                    .map(this::escapeCsvValue)
                    .collect(Collectors.joining(CSV_DELIMITER));
            writer.write(rowLine);
            writer.write(CSV_LINE_BREAK);
        }
    }

    private String convertToString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    private String escapeCsvValue(String value) {
        if (value == null) {
            return "\"\"";
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private <T> void validateData(List<T> data) {
        if (data == null || data.isEmpty()) {
            throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
        }
    }

    private <T> Iterator<T> prependToIterator(T firstElement, Iterator<T> rest) {
        return new Iterator<T>() {
            private boolean firstReturned = false;

            @Override
            public boolean hasNext() {
                return !firstReturned || rest.hasNext();
            }

            @Override
            public T next() {
                if (!firstReturned) {
                    firstReturned = true;
                    return firstElement;
                }
                return rest.next();
            }
        };
    }
}
