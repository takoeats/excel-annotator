package io.github.takoeats.excelannotator.internal.builder;

import io.github.takoeats.excelannotator.ExcelExporter;
import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractExcelBuilder {

    protected static final String DEFAULT_FILE_NAME = "download";
    protected static final int MAX_ROWS_FOR_LIST_API = 1000000;

    protected <T> void validateData(List<T> data) {
        if (data == null || data.isEmpty()) {
            throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
        }
        if (data.size() > MAX_ROWS_FOR_LIST_API) {
            throw new ExcelExporterException(ErrorCode.EXCEED_MAX_ROWS,
                    String.format("데이터 크기: %,d건 (최대: %,d건). Stream API를 사용하세요: ExcelExporter.excel().write(Stream)",
                            data.size(), MAX_ROWS_FOR_LIST_API));
        }
    }

    protected void validateMapData(Map<String, ?> sheetData) {
        if (sheetData == null || sheetData.isEmpty()) {
            throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
        }
    }

    protected Map<String, Stream<?>> convertMapToStreams(Map<String, ?> sheetData) {
        return sheetData.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Object value = entry.getValue();
                            if (value instanceof List) {
                                return ((List<?>) value).stream();
                            } else if (value instanceof Stream) {
                                return (Stream<?>) value;
                            } else {
                                throw new ExcelExporterException(
                                        ErrorCode.WORKBOOK_CREATION_FAILED,
                                        "Map values must be List or Stream, but was: " +
                                                (value == null ? "null" : value.getClass().getName())
                                );
                            }
                        }
                ));
    }

    protected <Q, R, E> List<E> transformData(Q queryParams,
                                              ExcelExporter.ExcelDataProvider<Q, R> dataProvider,
                                              Function<R, E> converter) {
        List<R> responseData = dataProvider.getExcelData(queryParams);
        return responseData.stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    protected void writeWorkbookAndHandleErrors(OutputStream outputStream,
                                                WorkbookSupplier workbookSupplier) {
        try (SXSSFWorkbook wb = workbookSupplier.get()) {
            wb.write(outputStream);
        } catch (ExcelExporterException ex) {
            throw ex;
        } catch (IOException ioEx) {
            throw new ExcelExporterException(ErrorCode.IO_ERROR, ioEx);
        } catch (Exception ex) {
            throw new ExcelExporterException(
                    ErrorCode.WORKBOOK_CREATION_FAILED,
                    "Excel 생성 중 예상치 못한 오류 발생",
                    ex
            );
        }
    }

    @FunctionalInterface
    protected interface WorkbookSupplier {
        SXSSFWorkbook get() throws ExcelExporterException;
    }
}
