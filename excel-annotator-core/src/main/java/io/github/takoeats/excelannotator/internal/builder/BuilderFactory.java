package io.github.takoeats.excelannotator.internal.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuilderFactory {

    public static ExcelBuilder createExcelBuilder(HttpServletResponse response) {
        return new ResponseExcelBuilderImpl(response);
    }

    public static ExcelBuilder createExcelBuilder(OutputStream outputStream) {
        return new StreamExcelBuilderImpl(outputStream);
    }

    public static CsvBuilder createCsvBuilder(HttpServletResponse response) {
        return new ResponseCsvBuilderImpl(response);
    }

    public static CsvBuilder createCsvBuilder(OutputStream outputStream) {
        return new StreamCsvBuilderImpl(outputStream);
    }

}
