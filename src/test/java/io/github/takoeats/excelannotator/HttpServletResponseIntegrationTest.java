package io.github.takoeats.excelannotator;

import static io.github.takoeats.excelannotator.util.ExcelAssertions.assertExcelFileValid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.takoeats.excelannotator.testdto.PersonDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpServletResponseIntegrationTest {

    private static final String EXCEL_CONTENT_TYPE =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Mock
    private HttpServletResponse response;

    private ByteArrayOutputStream baos;
    private ServletOutputStream servletOutputStream;

    @BeforeEach
    void setUp() throws IOException {
        baos = new ByteArrayOutputStream();
        servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                baos.write(b);
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    void downloadExcel_setsCorrectContentType() throws IOException {
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Alice", 30, new BigDecimal("1000.00")),
            new PersonDTO("Bob", 40, new BigDecimal("2000.00"))
        );

        ExcelExporter.excelFromList(response, "test.xlsx", data);

        verify(response).setContentType(EXCEL_CONTENT_TYPE);
    }

    @Test
    void downloadExcel_setsContentDispositionHeader() throws IOException {
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Test", 25, new BigDecimal("500.00"))
        );

        ExcelExporter.excelFromList(response, "report.xlsx", data);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("Content-Disposition"), headerCaptor.capture());
        String contentDisposition = headerCaptor.getValue();
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.startsWith("attachment"));
        assertTrue(contentDisposition.contains("filename="));
        assertTrue(contentDisposition.contains("filename*=UTF-8''"));
    }

    @Test
    void downloadExcel_encodesKoreanFileName() throws IOException {
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("테스트", 30, new BigDecimal("1000.00"))
        );

        ExcelExporter.excelFromList(response, "한글파일명.xlsx", data);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("Content-Disposition"), headerCaptor.capture());
        String contentDisposition = headerCaptor.getValue();
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.contains("filename*=UTF-8''"));
        assertTrue(contentDisposition.contains("%ED") || contentDisposition.contains("%ED%95%9C"));
    }

    @Test
    void downloadExcel_setCacheControlHeader() throws IOException {
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Test", 30, new BigDecimal("1000.00"))
        );

        ExcelExporter.excelFromList(response, "test.xlsx", data);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("Cache-Control"), headerCaptor.capture());
        String cacheControl = headerCaptor.getValue();
        assertNotNull(cacheControl);
        assertTrue(cacheControl.contains("no-store"));
        assertTrue(cacheControl.contains("no-cache"));
        assertTrue(cacheControl.contains("must-revalidate"));
    }

    @Test
    void downloadExcel_writesExcelToOutputStream() throws IOException {
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Alice", 30, new BigDecimal("1000.00")),
            new PersonDTO("Bob", 40, new BigDecimal("2000.00"))
        );

        ExcelExporter.excelFromList(response, "test.xlsx", data);

        byte[] excelBytes = baos.toByteArray();
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);
        assertExcelFileValid(excelBytes);
    }

    @Test
    void downloadExcel_respectsUserSetCacheControl() throws IOException {
        when(response.getHeader("Cache-Control")).thenReturn("public, max-age=3600");
        List<PersonDTO> data = Arrays.asList(
            new PersonDTO("Test", 30, new BigDecimal("1000.00"))
        );

        ExcelExporter.excelFromList(response, "test.xlsx", data);

        verify(response, never()).setHeader(eq("Cache-Control"), anyString());
    }
}
