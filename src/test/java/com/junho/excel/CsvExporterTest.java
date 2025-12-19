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
        assertEquals("report.csv", returnedName);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertTrue(lines.length >= 3);
        assertEquals("\"Name\",\"Age\",\"Salary\"", lines[0]);
        assertEquals("\"Alice\",\"30\",\"123.45\"", lines[1]);
        assertEquals("\"Bob\",\"40\",\"67.89\"", lines[2]);
    }

    @Test
    void csvFromStream_withValidData_generatesCsv() throws Exception {
        Stream<PersonDTO> stream = Stream.of(
                new PersonDTO("Charlie", 25, new BigDecimal("50000.00")),
                new PersonDTO("David", 35, new BigDecimal("75000.00"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.csvFromStream(baos, "stream.csv", stream);

        assertEquals("stream.csv", returnedName);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertTrue(lines.length >= 3);
        assertEquals("\"Name\",\"Age\",\"Salary\"", lines[0]);
        assertEquals("\"Charlie\",\"25\",\"50000.00\"", lines[1]);
        assertEquals("\"David\",\"35\",\"75000.00\"", lines[2]);
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

        assertEquals("\"Name\",\"Age\",\"Salary\"", lines[0]);
        assertEquals("\"Simple\",\"1\",\"100\"", lines[1]);
        assertEquals("\"Contains,comma\",\"2\",\"200\"", lines[2]);
        assertEquals("\"Contains\"\"quote\",\"3\",\"300\"", lines[3]);
        assertTrue(lines[4].startsWith("\"Contains"));
        assertTrue(lines[5].startsWith("\"Mixed"));
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

        assertEquals("\"Name\",\"Age\",\"Salary\"", lines[0]);
        assertEquals("\"Alice\",\"\",\"100\"", lines[1]);
        assertEquals("\"\",\"30\",\"\"", lines[2]);
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

        assertEquals("\"B\",\"C\",\"A\"", lines[0]);
        assertEquals("\"First\",\"Second\",\"Third\"", lines[1]);
    }

    @Test
    void csvFromList_fileNameWithoutExtension_appendsCsvExtension() {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("Test", 1, new BigDecimal("1"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.csvFromList(baos, "noExtension", list);

        assertEquals("noExtension.csv", returnedName);
    }

    @Test
    void csvFromList_fileNameWithXlsxExtension_replacesWithCsv() {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("Test", 1, new BigDecimal("1"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.csvFromList(baos, "report.xlsx", list);

        assertEquals("report.csv", returnedName);
    }

    @Test
    void csvFromList_withLeadingSpaces_escapesWithQuotes() throws Exception {
        List<PersonDTO> list = Arrays.asList(
                new PersonDTO(" LeadingSpace", 1, new BigDecimal("100")),
                new PersonDTO("NoSpace", 2, new BigDecimal("200"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "leading.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertEquals("\"Name\",\"Age\",\"Salary\"", lines[0]);
        assertEquals("\" LeadingSpace\",\"1\",\"100\"", lines[1]);
        assertEquals("\"NoSpace\",\"2\",\"200\"", lines[2]);
    }

    @Test
    void csvFromList_withTrailingSpaces_escapesWithQuotes() throws Exception {
        List<PersonDTO> list = Arrays.asList(
                new PersonDTO("TrailingSpace ", 1, new BigDecimal("100")),
                new PersonDTO("NoSpace", 2, new BigDecimal("200"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "trailing.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertEquals("\"Name\",\"Age\",\"Salary\"", lines[0]);
        assertEquals("\"TrailingSpace \",\"1\",\"100\"", lines[1]);
        assertEquals("\"NoSpace\",\"2\",\"200\"", lines[2]);
    }

    @Test
    void csvFromList_withBothLeadingAndTrailingSpaces_escapesWithQuotes() throws Exception {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO(" BothSpaces ", 1, new BigDecimal("100"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "both.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertEquals("\"Name\",\"Age\",\"Salary\"", lines[0]);
        assertEquals("\" BothSpaces \",\"1\",\"100\"", lines[1]);
    }

    @Test
    void csvFromList_rfc4180Compliance_allFieldsQuoted() throws Exception {
        List<PersonDTO> list = Arrays.asList(
                new PersonDTO("Standard", 1, new BigDecimal("100")),
                new PersonDTO("With,Comma", 2, new BigDecimal("200")),
                new PersonDTO("With\"Quote", 3, new BigDecimal("300")),
                new PersonDTO(" WithSpace ", 4, new BigDecimal("400"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "rfc4180.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        for (String line : lines) {
            String[] fields = parseRfc4180Line(line);
            for (String field : fields) {
                assertTrue(field.startsWith("\"") && field.endsWith("\""),
                        "RFC 4180: All fields must be quoted. Found unquoted field: " + field);
            }
        }

        assertEquals("\"Name\",\"Age\",\"Salary\"", lines[0]);
        assertEquals("\"Standard\",\"1\",\"100\"", lines[1]);
        assertEquals("\"With,Comma\",\"2\",\"200\"", lines[2]);
        assertEquals("\"With\"\"Quote\",\"3\",\"300\"", lines[3]);
        assertEquals("\" WithSpace \",\"4\",\"400\"", lines[4]);
    }

    @Test
    void csvFromList_rfc4180Compliance_crlf() throws Exception {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("Test", 1, new BigDecimal("100"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "crlf.csv", list);

        byte[] bytes = baos.toByteArray();
        String csv = new String(bytes, StandardCharsets.UTF_8);

        assertTrue(csv.contains("\r\n"), "RFC 4180: Lines must end with CRLF (\\r\\n)");
        assertTrue(!csv.contains("\n\r"), "RFC 4180: Should not contain \\n\\r");
        assertTrue(!csv.matches(".*[^\\r]\\n.*"), "RFC 4180: \\n must always be preceded by \\r");
    }

    @Test
    void csvFromList_rfc4180Compliance_doubleQuoteEscaping() throws Exception {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("Test\"With\"Quotes", 1, new BigDecimal("100"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "quotes.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());
        String[] lines = csv.split("\r\n");

        assertTrue(lines[1].contains("\"Test\"\"With\"\"Quotes\""),
                "RFC 4180: Double quotes must be escaped as \"\"");
        assertEquals("\"Test\"\"With\"\"Quotes\",\"1\",\"100\"", lines[1]);
    }

    @Test
    void csvFromList_rfc4180Compliance_embeddedNewlines() throws Exception {
        List<PersonDTO> list = Arrays.asList(
                new PersonDTO("Line1\nLine2", 1, new BigDecimal("100")),
                new PersonDTO("Normal", 2, new BigDecimal("200")),
                new PersonDTO("Line1\r\nLine2", 3, new BigDecimal("300"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "newlines.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());

        assertTrue(csv.contains("\"Line1\nLine2\""),
                "RFC 4180: Newlines inside quoted fields must be preserved");
        assertTrue(csv.contains("\"Line1\r\nLine2\""),
                "RFC 4180: CRLF inside quoted fields must be preserved");

        String[] records = parseRfc4180Csv(csv);
        assertEquals(4, records.length, "Should have 4 records (1 header + 3 data rows)");

        assertTrue(records[1].contains("Line1\nLine2"), "First data record should contain embedded newline");
        assertTrue(records[2].contains("Normal"), "Second data record should be normal");
        assertTrue(records[3].contains("Line1\r\nLine2"), "Third data record should contain embedded CRLF");
    }

    @Test
    void csvFromList_rfc4180Compliance_allSpecialCharacters() throws Exception {
        List<PersonDTO> list = Collections.singletonList(
                new PersonDTO("Has,Comma\nAnd\"Quote\r\nAndCRLF", 99, new BigDecimal("999.99"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.csvFromList(baos, "complex.csv", list);

        String csv = readCsvWithoutBom(baos.toByteArray());

        assertTrue(csv.contains("\"Has,Comma\nAnd\"\"Quote\r\nAndCRLF\""),
                "RFC 4180: All special characters must be properly escaped and quoted");

        String[] records = parseRfc4180Csv(csv);
        assertEquals(2, records.length, "Should have 2 records (1 header + 1 data row)");
    }

    private String[] parseRfc4180Csv(String csv) {
        java.util.List<String> records = new java.util.ArrayList<>();
        StringBuilder currentRecord = new StringBuilder();
        boolean insideQuotes = false;
        boolean lastWasCarriageReturn = false;

        for (int i = 0; i < csv.length(); i++) {
            char c = csv.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
                currentRecord.append(c);
            } else if (c == '\r') {
                currentRecord.append(c);
                lastWasCarriageReturn = true;
            } else if (c == '\n') {
                if (!insideQuotes && lastWasCarriageReturn) {
                    records.add(currentRecord.toString());
                    currentRecord = new StringBuilder();
                } else {
                    currentRecord.append(c);
                }
                lastWasCarriageReturn = false;
            } else {
                currentRecord.append(c);
                lastWasCarriageReturn = false;
            }
        }

        if (currentRecord.length() > 0) {
            records.add(currentRecord.toString());
        }

        return records.toArray(new String[0]);
    }

    private String[] parseRfc4180Line(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
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
