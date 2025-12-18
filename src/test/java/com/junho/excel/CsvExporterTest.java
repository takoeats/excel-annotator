package com.junho.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.testdto.PersonDTO;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

class CsvExporterTest {

    @Test
    void csvFromList_withValidData_generatesCsvWithCorrectOrder() throws Exception {
        List<PersonDTO> list = Arrays.asList(
                new PersonDTO("Alice", 30, new BigDecimal("123.45")),
                new PersonDTO("Bob", 40, new BigDecimal("67.89"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.csvFromList(baos, "report.csv", list);

        assertNotNull(returnedName);
        assertTrue(returnedName.startsWith("report_"));
        assertTrue(returnedName.endsWith(".csv"));

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertTrue(lines.length >= 3);
        assertEquals("Name,Age,Salary", lines[0]);
        assertEquals("Alice,30,123.45", lines[1]);
        assertEquals("Bob,40,67.89", lines[2]);
    }

    @Test
    void csvFromStream_withValidData_generatesCsv() throws Exception {
        Stream<PersonDTO> stream = Stream.of(
                new PersonDTO("Charlie", 25, new BigDecimal("50000.00")),
                new PersonDTO("David", 35, new BigDecimal("75000.00"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.csvFromStream(baos, "stream.csv", stream);

        assertTrue(returnedName.startsWith("stream_"));
        assertTrue(returnedName.endsWith(".csv"));

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertTrue(lines.length >= 3);
        assertEquals("Name,Age,Salary", lines[0]);
        assertEquals("Charlie,25,50000.00", lines[1]);
        assertEquals("David,35,75000.00", lines[2]);
    }

    @Test
    void csvFromList_withSpecialCharacters_escapesCorrectly() throws Exception {
        List<PersonDTO> list = Arrays.asList(
                new PersonDTO("Simple", 1, new BigDecimal("100")),
                new PersonDTO("Contains,comma", 2, new BigDecimal("200")),
                new PersonDTO("Contains\"quote", 3, new BigDecimal("300")),
                new PersonDTO("Contains\nNewline", 4, new BigDecimal("400")),
                new PersonDTO("Mixed,\"All", 5, new BigDecimal("500"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "escape.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertEquals("Name,Age,Salary", lines[0]);
        assertEquals("Simple,1,100", lines[1]);
        assertEquals("\"Contains,comma\",2,200", lines[2]);
        assertEquals("\"Contains\"\"quote\",3,300", lines[3]);
        assertTrue(lines[4].contains("\"Contains"));
        assertTrue(lines[5].contains("\"Mixed"));
    }

    @Test
    void csvFromList_withUtf8Bom_includesBomAtStart() throws Exception {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("한글", 1, new BigDecimal("1000"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "korean.csv", list);

        byte[] bytes = baos.toByteArray();
        assertTrue(bytes.length >= 3);
        assertEquals((byte) 0xEF, bytes[0]);
        assertEquals((byte) 0xBB, bytes[1]);
        assertEquals((byte) 0xBF, bytes[2]);
    }

    @Test
    void csvFromList_withEmptyData_throwsException() {
        List<PersonDTO> emptyList = Collections.emptyList();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.csvFromList(baos, "empty.csv", emptyList));
    }

    @Test
    void csvFromList_withNullData_throwsException() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.csvFromList(baos, "null.csv", null));
    }

    @Test
    void csvFromStream_withEmptyStream_throwsException() {
        Stream<PersonDTO> emptyStream = Stream.empty();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.csvFromStream(baos, "empty.csv", emptyStream));
    }

    @Test
    void csvFromList_withNullValues_handlesGracefully() throws Exception {
        List<PersonDTO> list = Arrays.asList(
                new PersonDTO("Alice", null, new BigDecimal("100")),
                new PersonDTO(null, 30, null)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "nullable.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertEquals("Name,Age,Salary", lines[0]);
        assertEquals("Alice,,100", lines[1]);
        assertEquals(",30,", lines[2]);
    }

    @Test
    void csvFromList_columnOrder_respectsExcelColumnOrder() throws Exception {
        List<OrderedDTO> list = Collections.singletonList(
                new OrderedDTO("Third", "First", "Second")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "ordered.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertEquals("B,C,A", lines[0]);
        assertEquals("First,Second,Third", lines[1]);
    }

    @Test
    void csvFromList_fileNameWithoutExtension_appendsCsvExtension() {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("Test", 1, new BigDecimal("1"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.csvFromList(baos, "noExtension", list);

        assertTrue(returnedName.endsWith(".csv"));
        assertTrue(returnedName.contains("noExtension_"));
    }

    @Test
    void csvFromList_fileNameWithXlsxExtension_replacesWithCsv() {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("Test", 1, new BigDecimal("1"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.csvFromList(baos, "report.xlsx", list);

        assertTrue(returnedName.endsWith(".csv"));
        assertTrue(returnedName.startsWith("report_"));
    }

    private String readCsvWithoutBom(byte[] bytes) {
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            return new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @ExcelSheet("OrderTest")
    @Getter
    @AllArgsConstructor
    public static class OrderedDTO {
        @ExcelColumn(header = "A", order = 3)
        private String fieldA;

        @ExcelColumn(header = "B", order = 1)
        private String fieldB;

        @ExcelColumn(header = "C", order = 2)
        private String fieldC;
    }
}
