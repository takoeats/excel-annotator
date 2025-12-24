package io.github.takoeats.excelannotator.internal.builder;

import io.github.takoeats.excelannotator.ExcelExporter;
import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.util.FileNameProcessor;
import io.github.takoeats.excelannotator.internal.util.ResponseHeaderHandler;
import io.github.takoeats.excelannotator.internal.writer.ExcelWriter;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
class ResponseExcelBuilderImpl extends AbstractExcelBuilder implements ExcelBuilder {

    private static final String XLSX = ".xlsx";

    private final HttpServletResponse response;
    private String fileName = DEFAULT_FILE_NAME;

    @Override
    public ExcelBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    public <T> String write(List<T> data) {
        validateData(data);
        return executeWrite(outputStream -> {
            ExcelWriter writer = new ExcelWriter();
            writeWorkbookAndHandleErrors(outputStream, () -> writer.write(data));
        });
    }

    @Override
    public <T> String write(Stream<T> dataStream) {
        return executeWrite(outputStream -> {
            ExcelWriter writer = new ExcelWriter();
            writeWorkbookAndHandleErrors(outputStream, () -> writer.write(dataStream));
        });
    }

    @Override
    public String write(Map<String, ?> sheetData) {
        validateMapData(sheetData);
        Map<String, Stream<?>> streamMap = convertMapToStreams(sheetData);
        return executeWrite(outputStream -> {
            ExcelWriter writer = new ExcelWriter();
            writeWorkbookAndHandleErrors(outputStream, () -> writer.writeWithStreams(streamMap));
        });
    }

    @Override
    public <Q, R, E> String write(Q queryParams,
                                  ExcelExporter.ExcelDataProvider<Q, R> dataProvider,
                                  Function<R, E> converter) {
        List<E> excelData = transformData(queryParams, dataProvider, converter);
        return write(excelData);
    }

    private String executeWrite(OutputStreamWriter writer) {
        try {
            String sanitized = FileNameProcessor.sanitizeFileName(fileName);
            String processed = FileNameProcessor.processFileName(sanitized, XLSX);
            String encoded = FileNameProcessor.urlEncodeRFC5987(processed);

            ResponseHeaderHandler.setResponseHeaders(response,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "download.xlsx",
                    encoded);

            writer.write(response.getOutputStream());
            return processed;
        } catch (IOException ioEx) {
            throw new ExcelExporterException(ErrorCode.IO_ERROR, ioEx);
        }
    }

    @FunctionalInterface
    private interface OutputStreamWriter {
        void write(OutputStream outputStream) throws IOException;
    }
}
