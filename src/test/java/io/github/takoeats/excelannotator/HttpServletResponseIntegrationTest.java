package io.github.takoeats.excelannotator;

import static io.github.takoeats.excelannotator.util.ExcelAssertions.assertExcelFileValid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.takoeats.excelannotator.testdto.PersonDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

class HttpServletResponseIntegrationTest {

    private static final String EXCEL_CONTENT_TYPE =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Test
    void downloadExcel_setsCorrectContentType() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Alice", 30, new BigDecimal("1000.00")),
            new PersonDTO("Bob", 40, new BigDecimal("2000.00"))
        );

        ExcelExporter.excelFromList(response, "test.xlsx", data);

        assertEquals(EXCEL_CONTENT_TYPE, response.getContentType());
    }

    @Test
    void downloadExcel_setsContentDispositionHeader() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Test", 25, new BigDecimal("500.00"))
        );

        ExcelExporter.excelFromList(response, "report.xlsx", data);

        String contentDisposition = response.getHeader("Content-Disposition");
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.startsWith("attachment"));
        assertTrue(contentDisposition.contains("filename="));
        assertTrue(contentDisposition.contains("filename*=UTF-8''"));
    }

    @Test
    void downloadExcel_encodesKoreanFileName() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("테스트", 30, new BigDecimal("1000.00"))
        );

        ExcelExporter.excelFromList(response, "한글파일명.xlsx", data);

        String contentDisposition = response.getHeader("Content-Disposition");
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.contains("filename*=UTF-8''"));
        assertTrue(contentDisposition.contains("%ED") || contentDisposition.contains("%ED%95%9C"));
    }

    @Test
    void downloadExcel_setCacheControlHeader() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Test", 30, new BigDecimal("1000.00"))
        );

        ExcelExporter.excelFromList(response, "test.xlsx", data);

        String cacheControl = response.getHeader("Cache-Control");
        assertNotNull(cacheControl);
        assertTrue(cacheControl.contains("no-store"));
        assertTrue(cacheControl.contains("no-cache"));
        assertTrue(cacheControl.contains("must-revalidate"));
    }

    @Test
    void downloadExcel_writesExcelToOutputStream() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Alice", 30, new BigDecimal("1000.00")),
            new PersonDTO("Bob", 40, new BigDecimal("2000.00"))
        );

        ExcelExporter.excelFromList(response, "test.xlsx", data);

        byte[] excelBytes = response.getOutputStreamContent();
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);
        assertExcelFileValid(excelBytes);
    }

    @Test
    void downloadExcel_respectsUserSetCacheControl() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setHeader("Cache-Control", "public, max-age=3600");
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Test", 30, new BigDecimal("1000.00"))
        );

        ExcelExporter.excelFromList(response, "test.xlsx", data);

        String cacheControl = response.getHeader("Cache-Control");
        assertEquals("public, max-age=3600", cacheControl);
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
            // Empty for Test
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

        public byte[] getOutputStreamContent() {
            return outputStream.toByteArray();
        }

        @Override
        public boolean isCommitted() {
            return committed;
        }

        @Override public void addCookie(Cookie cookie) {}
        @Override public boolean containsHeader(String name) { return headers.containsKey(name); }
        @Override public String encodeURL(String url) { return url; }
        @Override public String encodeRedirectURL(String url) { return url; }
        @Override public String encodeUrl(String url) { return url; }
        @Override public String encodeRedirectUrl(String url) { return url; }
        @Override public void sendError(int sc, String msg) throws IOException {}
        @Override public void sendError(int sc) throws IOException {}
        @Override public void sendRedirect(String location) throws IOException {}
        @Override public void setDateHeader(String name, long date) {}
        @Override public void addDateHeader(String name, long date) {}
        @Override public void addHeader(String name, String value) { headers.put(name, value); }
        @Override public void setIntHeader(String name, int value) {}
        @Override public void addIntHeader(String name, int value) {}
        @Override public void setStatus(int sc) {}
        @Override public void setStatus(int sc, String sm) {}
        @Override public int getStatus() { return 200; }
        @Override public String getCharacterEncoding() { return "UTF-8"; }
        @Override public PrintWriter getWriter() throws IOException { return null; }
        @Override public void setCharacterEncoding(String charset) {}
        @Override public void setContentLength(int len) {}
        @Override public void setContentLengthLong(long len) {}
        @Override public void setBufferSize(int size) {}
        @Override public int getBufferSize() { return 0; }
        @Override public void resetBuffer() {}
        @Override public void setLocale(Locale loc) {}
        @Override public Locale getLocale() { return Locale.getDefault(); }
    }
}
