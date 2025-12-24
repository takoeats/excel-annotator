package io.github.takoeats.excelannotator.internal.builder;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.util.FileNameProcessor;
import io.github.takoeats.excelannotator.internal.util.ResponseHeaderHandler;
import io.github.takoeats.excelannotator.internal.writer.CsvWriter;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
class ResponseCsvBuilderImpl extends AbstractCsvBuilder implements CsvBuilder {

    private static final String CSV = ".csv";

    private final HttpServletResponse response;
    private String fileName = DEFAULT_FILE_NAME;

    @Override
    public CsvBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    public <T> String write(List<T> data) {
        return executeWrite(outputStream -> {
            CsvWriter writer = new CsvWriter();
            writer.write(outputStream, data);
        });
    }

    @Override
    public <T> String write(Stream<T> dataStream) {
        return executeWrite(outputStream -> {
            CsvWriter writer = new CsvWriter();
            writer.write(outputStream, dataStream);
        });
    }

    private String executeWrite(OutputStreamWriter writer) {
        try {
            String sanitized = FileNameProcessor.sanitizeFileName(fileName);
            String processed = FileNameProcessor.processFileName(sanitized, CSV);
            String encoded = FileNameProcessor.urlEncodeRFC5987(processed);

            ResponseHeaderHandler.setResponseHeaders(response,
                    "text/csv; charset=UTF-8",
                    "download.csv",
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
