package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.testdto.CustomerPartADTO;
import io.github.takoeats.excelannotator.testdto.CustomerPartBDTO;
import io.github.takoeats.excelannotator.testdto.PersonDTO;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FluentAPITest {

    @Test
    void responseExcelBuilder_withList_writesToResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Arrays.asList(
                new PersonDTO("Alice", 30, new BigDecimal("1000.00")),
                new PersonDTO("Bob", 25, new BigDecimal("2000.00"))
        );

        ExcelExporter.excel(response)
                .fileName("test.xlsx")
                .write(data);

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                response.getContentType());

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(content));
        assertEquals(1, wb.getNumberOfSheets());
        Sheet sheet = wb.getSheetAt(0);
        assertEquals("Persons", sheet.getSheetName());
        assertEquals(3, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void responseExcelBuilder_withStream_writesToResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        Stream<PersonDTO> dataStream = Stream.of(
                new PersonDTO("Charlie", 35, new BigDecimal("1500.00")),
                new PersonDTO("Diana", 28, new BigDecimal("1800.00"))
        );

        ExcelExporter.excel(response)
                .fileName("stream_test.xlsx")
                .write(dataStream);

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(content));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(3, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void responseExcelBuilder_withMultiSheet_writesToResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, List<?>> sheetData = new LinkedHashMap<>();
        sheetData.put("customers", Collections.singletonList(
                new CustomerPartADTO("C001", "Kim")
        ));
        sheetData.put("orders", Collections.singletonList(
                new CustomerPartBDTO("kim@test.com", "010-1234-5678")
        ));

        ExcelExporter.excel(response)
                .fileName("multi_sheet.xlsx")
                .write(sheetData);

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(content));
        assertEquals(1, wb.getNumberOfSheets());
        wb.close();
    }

    @Test
    void responseExcelBuilder_withDataProvider_writesToResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        String queryParams = "active";
        ExcelExporter.ExcelDataProvider<String, PersonDTO> provider =
                param -> Collections.singletonList(
                        new PersonDTO("Provider", 40, new BigDecimal("3000.00"))
                );

        ExcelExporter.excel(response)
                .fileName("provider_test.xlsx")
                .write(queryParams, provider, p -> p);

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(content));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(2, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void responseExcelBuilder_withoutFileName_usesDefaultName() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Collections.singletonList(
                new PersonDTO("Default", 20, new BigDecimal("500.00"))
        );

        ExcelExporter.excel(response).write(data);

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);
        String disposition = response.getHeader("Content-Disposition");
        assertNotNull(disposition);
        assertTrue(disposition.contains("download"));
    }

    @Test
    void streamExcelBuilder_withList_returnsFileName() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<PersonDTO> data = Arrays.asList(
                new PersonDTO("Eve", 32, new BigDecimal("2500.00")),
                new PersonDTO("Frank", 29, new BigDecimal("2200.00"))
        );

        String fileName = ExcelExporter.excel(baos)
                .fileName("output.xlsx")
                .write(data);

        assertEquals("output.xlsx", fileName);
        assertTrue(baos.toByteArray().length > 0);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(3, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void streamExcelBuilder_withStream_returnsFileName() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Stream<PersonDTO> dataStream = Stream.of(
                new PersonDTO("Grace", 27, new BigDecimal("1900.00"))
        );

        String fileName = ExcelExporter.excel(baos)
                .fileName("stream_output.xlsx")
                .write(dataStream);

        assertEquals("stream_output.xlsx", fileName);
        assertTrue(baos.toByteArray().length > 0);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(1, wb.getNumberOfSheets());
        wb.close();
    }

    @Test
    void streamExcelBuilder_withMultiSheet_returnsFileName() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<String, Stream<?>> sheetData = new LinkedHashMap<>();
        sheetData.put("sheet1", Stream.of(new CustomerPartADTO("C002", "Lee")));
        sheetData.put("sheet2", Stream.of(new CustomerPartBDTO("lee@test.com", "010-9876-5432")));

        String fileName = ExcelExporter.excel(baos)
                .fileName("multi_stream.xlsx")
                .write(sheetData);

        assertEquals("multi_stream.xlsx", fileName);
        assertTrue(baos.toByteArray().length > 0);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(1, wb.getNumberOfSheets());
        wb.close();
    }

    @Test
    void streamExcelBuilder_withDataProvider_returnsFileName() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Integer queryParams = 100;
        ExcelExporter.ExcelDataProvider<Integer, PersonDTO> provider =
                limit -> Collections.singletonList(
                        new PersonDTO("Provider2", 45, new BigDecimal("4000.00"))
                );

        String fileName = ExcelExporter.excel(baos)
                .fileName("provider_stream.xlsx")
                .write(queryParams, provider, p -> p);

        assertEquals("provider_stream.xlsx", fileName);
        assertTrue(baos.toByteArray().length > 0);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(2, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void streamExcelBuilder_withoutFileName_generatesTimestampedName() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<PersonDTO> data = Collections.singletonList(
                new PersonDTO("Default2", 22, new BigDecimal("600.00"))
        );

        String fileName = ExcelExporter.excel(baos).write(data);

        assertNotNull(fileName);
        assertTrue(fileName.startsWith("download_"));
        assertTrue(fileName.endsWith(".xlsx"));
        assertTrue(baos.toByteArray().length > 0);
    }

    @Test
    void responseCsvBuilder_withList_writesToResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Arrays.asList(
                new PersonDTO("Helen", 31, new BigDecimal("2100.00")),
                new PersonDTO("Ian", 26, new BigDecimal("1700.00"))
        );

        ExcelExporter.csv(response)
                .fileName("test.csv")
                .write(data);

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);
        assertEquals("text/csv; charset=UTF-8", response.getContentType());

        String csvContent = new String(content);
        assertTrue(csvContent.contains("Name"));
        assertTrue(csvContent.contains("Helen"));
        assertTrue(csvContent.contains("Ian"));
    }

    @Test
    void responseCsvBuilder_withStream_writesToResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        Stream<PersonDTO> dataStream = Stream.of(
                new PersonDTO("Jack", 33, new BigDecimal("2300.00"))
        );

        ExcelExporter.csv(response)
                .fileName("stream_test.csv")
                .write(dataStream);

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);

        String csvContent = new String(content);
        assertTrue(csvContent.contains("Jack"));
    }

    @Test
    void responseCsvBuilder_withoutFileName_usesDefaultName() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Collections.singletonList(
                new PersonDTO("Default CSV", 24, new BigDecimal("700.00"))
        );

        ExcelExporter.csv(response).write(data);

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);
        String disposition = response.getHeader("Content-Disposition");
        assertNotNull(disposition);
        assertTrue(disposition.contains("download"));
    }

    @Test
    void streamCsvBuilder_withList_returnsFileName() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<PersonDTO> data = Arrays.asList(
                new PersonDTO("Kelly", 30, new BigDecimal("2000.00")),
                new PersonDTO("Leo", 28, new BigDecimal("1900.00"))
        );

        String fileName = ExcelExporter.csv(baos)
                .fileName("output.csv")
                .write(data);

        assertEquals("output.csv", fileName);
        assertTrue(baos.toByteArray().length > 0);

        String csvContent = new String(baos.toByteArray());
        assertTrue(csvContent.contains("Kelly"));
        assertTrue(csvContent.contains("Leo"));
    }

    @Test
    void streamCsvBuilder_withStream_returnsFileName() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Stream<PersonDTO> dataStream = Stream.of(
                new PersonDTO("Mia", 29, new BigDecimal("2100.00"))
        );

        String fileName = ExcelExporter.csv(baos)
                .fileName("stream_output.csv")
                .write(dataStream);

        assertEquals("stream_output.csv", fileName);
        assertTrue(baos.toByteArray().length > 0);

        String csvContent = new String(baos.toByteArray());
        assertTrue(csvContent.contains("Mia"));
    }

    @Test
    void streamCsvBuilder_withoutFileName_generatesTimestampedName() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<PersonDTO> data = Collections.singletonList(
                new PersonDTO("Default CSV2", 25, new BigDecimal("800.00"))
        );

        String fileName = ExcelExporter.csv(baos).write(data);

        assertNotNull(fileName);
        assertTrue(fileName.startsWith("download_"));
        assertTrue(fileName.endsWith(".csv"));
        assertTrue(baos.toByteArray().length > 0);
    }

    @Test
    void fluentAPI_methodChaining_worksCorrectly() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<PersonDTO> data = Collections.singletonList(
                new PersonDTO("Chained", 35, new BigDecimal("3000.00"))
        );

        String fileName = ExcelExporter
                .excel(baos)
                .fileName("chained.xlsx")
                .write(data);

        assertEquals("chained.xlsx", fileName);
        assertTrue(baos.toByteArray().length > 0);
    }

    @Test
    void fluentAPI_multipleBuilders_workIndependently() throws Exception {
        ByteArrayOutputStream excelBaos = new ByteArrayOutputStream();
        ByteArrayOutputStream csvBaos = new ByteArrayOutputStream();
        List<PersonDTO> data = Collections.singletonList(
                new PersonDTO("Independent", 40, new BigDecimal("3500.00"))
        );

        String excelFileName = ExcelExporter.excel(excelBaos)
                .fileName("excel.xlsx")
                .write(data);

        String csvFileName = ExcelExporter.csv(csvBaos)
                .fileName("csv.csv")
                .write(data);

        assertEquals("excel.xlsx", excelFileName);
        assertEquals("csv.csv", csvFileName);
        assertTrue(excelBaos.toByteArray().length > 0);
        assertTrue(csvBaos.toByteArray().length > 0);
    }

    private static class MockHttpServletResponse implements HttpServletResponse {
        private final Map<String, String> headers = new HashMap<>();
        private String contentType;
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private final ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }
        };
        private boolean committed = false;

        @Override
        public void setContentType(String type) {
            this.contentType = type;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public void setHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name);
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return Collections.emptyList();
        }

        @Override
        public Collection<String> getHeaderNames() {
            return Collections.emptyList();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return servletOutputStream;
        }

        @Override
        public void flushBuffer() throws IOException {
            committed = true;
            outputStream.flush();
        }

        @Override
        public void reset() {
            headers.clear();
            contentType = null;
            outputStream.reset();
            committed = false;
        }

        public byte[] getContentAsByteArray() {
            return outputStream.toByteArray();
        }

        @Override
        public boolean isCommitted() {
            return committed;
        }

        @Override
        public void addCookie(Cookie cookie) {
        }

        @Override
        public boolean containsHeader(String name) {
            return headers.containsKey(name);
        }

        @Override
        public String encodeURL(String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return url;
        }

        @Override
        public String encodeUrl(String url) {
            return url;
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return url;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
        }

        @Override
        public void sendError(int sc) throws IOException {
        }

        @Override
        public void sendRedirect(String location) throws IOException {
        }

        @Override
        public void setDateHeader(String name, long date) {
        }

        @Override
        public void addDateHeader(String name, long date) {
        }

        @Override
        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
        }

        @Override
        public void addIntHeader(String name, int value) {
        }

        @Override
        public void setStatus(int sc) {
        }

        @Override
        public void setStatus(int sc, String sm) {
        }

        @Override
        public int getStatus() {
            return 200;
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public void setContentLengthLong(long len) {
        }

        @Override
        public void setBufferSize(int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public void setLocale(Locale loc) {
        }

        @Override
        public Locale getLocale() {
            return Locale.getDefault();
        }
    }
}
