package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlingComprehensiveTest {

    @Test
    void errorCode_E001_emptyDataList() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", Collections.emptyList()));

        assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
        assertTrue(exception
                .getMessage()
                .contains("데이터"));
    }

    @Test
    void errorCode_E001_nullDataList() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<ValidDTO> nullList = null;

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", nullList));

        assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
    }

    @Test
    void errorCode_E001_emptyStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        java.util.stream.Stream<ValidDTO> emptyStream = java.util.stream.Stream.empty();

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromStream(baos, "test.xlsx", emptyStream));

        assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
    }

    @Test
    void errorCode_E001_multiSheetEmptyMap() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<String, List<?>> emptyMap = new LinkedHashMap<>();

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", emptyMap));

        assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
    }

    @Test
    void errorCode_E001_multiSheetNullListValue() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<String, List<?>> mapWithNull = new LinkedHashMap<>();
        mapWithNull.put("sheet1", null);

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", mapWithNull));

        assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
    }


    @Test
    void errorCode_E009_missingExcelSheetAnnotation() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<MissingExcelSheetDTO> data = Collections.singletonList(new MissingExcelSheetDTO("test"));

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", data));

        assertEquals(ErrorCode.METADATA_EXTRACTION_FAILED, exception.getErrorCode());
    }

    @Test
    void errorCode_E009_multiSheetMissingAnnotation() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<String, List<?>> sheetData = new LinkedHashMap<>();
        sheetData.put("invalid", Collections.singletonList(new MissingExcelSheetDTO("test")));

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", sheetData));

        assertEquals(ErrorCode.METADATA_EXTRACTION_FAILED, exception.getErrorCode());
    }

    @Test
    void errorCode_E010_missingGetter() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<MissingGetterDTO> data = Collections.singletonList(new MissingGetterDTO("test"));

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", data));

        assertEquals(ErrorCode.FIELD_ACCESS_FAILED, exception.getErrorCode());
        assertTrue(exception
                .getMessage()
                .contains("필드") ||
                exception
                        .getMessage()
                        .contains("getter"));
    }

    @Test
    void errorCode_E006_privateConstructorStyle() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<PrivateConstructorStyleDTO> data = Collections.singletonList(
                new PrivateConstructorStyleDTO("test")
        );

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", data));

        assertEquals(ErrorCode.STYLE_INSTANTIATION_FAILED, exception.getErrorCode());
        assertTrue(exception
                .getMessage()
                .contains("스타일") ||
                exception
                        .getMessage()
                        .contains("생성자"));
    }

    @Test
    void errorCode_E006_noArgConstructorMissingStyle() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<NoArgConstructorMissingStyleDTO> data = Collections.singletonList(
                new NoArgConstructorMissingStyleDTO("test")
        );

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", data));

        assertEquals(ErrorCode.STYLE_INSTANTIATION_FAILED, exception.getErrorCode());
    }

    @Test
    void exceptionMessage_containsErrorCode() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", Collections.emptyList()));

        assertNotNull(exception.getCode());
        assertTrue(exception
                .getCode()
                .startsWith("E"));
        assertTrue(exception
                .getMessage()
                .contains(exception.getCode()));
    }

    @Test
    void exceptionMessage_containsErrorDescription() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<MissingExcelSheetDTO> data = Collections.singletonList(new MissingExcelSheetDTO("test"));

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", data));

        assertNotNull(exception.getMessage());
        assertFalse(exception
                .getMessage()
                .isEmpty());
    }

    @Test
    void exception_propagatesOriginalCause() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<MissingGetterDTO> data = Collections.singletonList(new MissingGetterDTO("test"));

        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", data));

        assertNotNull(exception.getCause());
    }

    @Test
    void errorHandling_multipleErrorsInSequence() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", Collections.emptyList()));

        assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx",
                        Collections.singletonList(new MissingExcelSheetDTO("test"))));

        assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromStream(baos, "test.xlsx",
                        Stream.empty()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("ValidSheet")
    public static class ValidDTO {

        @ExcelColumn(header = "Data", order = 1)
        private String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissingExcelSheetDTO {

        @ExcelColumn(header = "Data", order = 1)
        private String data;
    }

    @ExcelSheet("MissingGetterSheet")
    public static class MissingGetterDTO {

        @ExcelColumn(header = "Data", order = 1)
        private final String data;

        public MissingGetterDTO(String data) {
            this.data = data;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("PrivateConstructorStyleSheet")
    public static class PrivateConstructorStyleDTO {

        @ExcelColumn(header = "Data", order = 1, columnStyle = PrivateConstructorStyle.class)
        private String data;
    }

    public static class PrivateConstructorStyle extends CustomExcelCellStyle {

        private PrivateConstructorStyle() {
        }

        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("NoArgConstructorMissingStyleSheet")
    public static class NoArgConstructorMissingStyleDTO {

        @ExcelColumn(header = "Data", order = 1, columnStyle = NoArgConstructorMissingStyle.class)
        private String data;
    }

    //for test NoArgConstructorMissing
    @SuppressWarnings({"java:S1068", "java:S1186"})
    public static class NoArgConstructorMissingStyle extends CustomExcelCellStyle {


        private final String param;

        public NoArgConstructorMissingStyle(String param) {
            this.param = param;
        }

        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }
}
