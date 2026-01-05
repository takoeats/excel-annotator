package io.github.takoeats.excelannotator.internal.builder;

import io.github.takoeats.excelannotator.ExcelExporter;
import io.github.takoeats.excelannotator.internal.util.FileNameProcessor;
import io.github.takoeats.excelannotator.internal.writer.ExcelWriter;
import lombok.RequiredArgsConstructor;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
class StreamExcelBuilderImpl extends AbstractExcelBuilder implements ExcelBuilder {

    private static final String XLSX = ".xlsx";

    private final OutputStream outputStream;
    private String fileName = DEFAULT_FILE_NAME;

    @Override
    public ExcelBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    public <T> String write(List<T> data) {
        validateData(data);
        ExcelWriter writer = new ExcelWriter();
        writeWorkbookAndHandleErrors(outputStream, () -> writer.write(data));
        return getProcessedFileName();
    }

    @Override
    public <T> String write(Stream<T> dataStream) {
        ExcelWriter writer = new ExcelWriter();
        writeWorkbookAndHandleErrors(outputStream, () -> writer.write(dataStream));
        return getProcessedFileName();
    }

    @Override
    public String write(Map<String, ?> sheetData) {
        validateMapData(sheetData);
        Map<String, Stream<?>> streamMap = convertMapToStreams(sheetData);
        ExcelWriter writer = new ExcelWriter();
        writeWorkbookAndHandleErrors(outputStream, () -> writer.writeWithStreams(streamMap));
        return getProcessedFileName();
    }

    @Override
    public <Q, R, E> String write(Q queryParams,
                                  ExcelExporter.ExcelDataProvider<Q, R> dataProvider,
                                  Function<R, E> converter) {
        List<E> excelData = transformData(queryParams, dataProvider, converter);
        return write(excelData);
    }

    private String getProcessedFileName() {
        String sanitized = FileNameProcessor.sanitizeFileName(fileName);
        return FileNameProcessor.processFileName(sanitized, XLSX);
    }
}
