package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.testdto.PersonDTO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExcelExporterIOExceptionTest {

    @Test
    void excelFromList_outputStreamThrowsIOException_wrapsInExcelExporterException() {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("Test", 1, new BigDecimal("1.00"))
        );

        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Simulated IO failure");
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                throw new IOException("Simulated IO failure");
            }
        };

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(failingOutputStream, "test.xlsx", list)
        );

        assertEquals(ErrorCode.IO_ERROR, exception.getErrorCode());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    void excelFromStream_outputStreamThrowsIOException_wrapsInExcelExporterException() {
        Stream<PersonDTO> stream = Stream.of(
                new PersonDTO("Test", 1, new BigDecimal("1.00"))
        );

        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Simulated IO failure");
            }
        };

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> ExcelExporter.excelFromStream(failingOutputStream, "test.xlsx", stream)
        );

        assertEquals(ErrorCode.IO_ERROR, exception.getErrorCode());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    void excelFromList_multiSheet_outputStreamThrowsIOException_wrapsInExcelExporterException() {
        Map<String, List<?>> sheetData = new LinkedHashMap<>();
        sheetData.put("sheet1", Collections.singletonList(
                new PersonDTO("Test", 1, new BigDecimal("1.00"))
        ));

        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Simulated IO failure");
            }
        };

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(failingOutputStream, "test.xlsx", sheetData)
        );

        assertEquals(ErrorCode.IO_ERROR, exception.getErrorCode());
    }

    @Test
    void excelFromStream_multiSheet_outputStreamThrowsIOException_wrapsInExcelExporterException() {
        Map<String, Stream<?>> sheetStreams = new LinkedHashMap<>();
        sheetStreams.put("sheet1", Stream.of(
                new PersonDTO("Test", 1, new BigDecimal("1.00"))
        ));

        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Simulated IO failure");
            }
        };

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> ExcelExporter.excelFromStream(failingOutputStream, "test.xlsx", sheetStreams)
        );

        assertEquals(ErrorCode.IO_ERROR, exception.getErrorCode());
    }


    @Test
    void excelFromList_withDataProvider_outputStreamThrowsIOException_wrapsException() {
        List<String> sourceData = Arrays.asList("Alice");

        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Simulated IO failure");
            }
        };

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(
                        failingOutputStream,
                        "test.xlsx",
                        "queryParam",
                        queryParams -> sourceData,
                        sourceName -> new PersonDTO(sourceName, 30, new BigDecimal("100.00"))
                )
        );

        assertEquals(ErrorCode.IO_ERROR, exception.getErrorCode());
    }
}
