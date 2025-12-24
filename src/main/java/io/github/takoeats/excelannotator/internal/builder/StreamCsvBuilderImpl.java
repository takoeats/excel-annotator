package io.github.takoeats.excelannotator.internal.builder;

import io.github.takoeats.excelannotator.internal.util.FileNameProcessor;
import io.github.takoeats.excelannotator.internal.writer.CsvWriter;
import lombok.RequiredArgsConstructor;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
class StreamCsvBuilderImpl extends AbstractCsvBuilder implements CsvBuilder {

    private static final String CSV = ".csv";

    private final OutputStream outputStream;
    private String fileName = DEFAULT_FILE_NAME;

    @Override
    public CsvBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    public <T> String write(List<T> data) {
        CsvWriter writer = new CsvWriter();
        writer.write(outputStream, data);
        return getProcessedFileName();
    }

    @Override
    public <T> String write(Stream<T> dataStream) {
        CsvWriter writer = new CsvWriter();
        writer.write(outputStream, dataStream);
        return getProcessedFileName();
    }

    private String getProcessedFileName() {
        String sanitized = FileNameProcessor.sanitizeFileName(fileName);
        return FileNameProcessor.processFileName(sanitized, CSV);
    }
}
