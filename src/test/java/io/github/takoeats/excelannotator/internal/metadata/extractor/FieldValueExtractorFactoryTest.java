package io.github.takoeats.excelannotator.internal.metadata.extractor;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import io.github.takoeats.excelannotator.masking.Masking;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class FieldValueExtractorFactoryTest {

    @Test
    void createExtractor_withStringField_returnsExtractorThatConvertsToString() throws NoSuchFieldException {
        Field nameField = TestDTO.class.getDeclaredField("name");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Name")
                .order(0)
                .width(100)
                .format("")
                .field(nameField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        dto.setName("John Doe");

        Object result = extractor.apply(dto);
        assertEquals("John Doe", result);
        assertInstanceOf(String.class, result);
    }

    @Test
    void createExtractor_withNumberField_returnsExtractorThatPreservesNumber() throws NoSuchFieldException {
        Field ageField = TestDTO.class.getDeclaredField("age");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Age")
                .order(0)
                .width(50)
                .format("")
                .field(ageField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        dto.setAge(30);

        Object result = extractor.apply(dto);
        assertEquals(30, result);
        assertInstanceOf(Integer.class, result);
    }

    @Test
    void createExtractor_withDateField_returnsExtractorThatPreservesDate() throws NoSuchFieldException {
        Field birthDateField = TestDTO.class.getDeclaredField("birthDate");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("BirthDate")
                .order(0)
                .width(100)
                .format("")
                .field(birthDateField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        Date now = new Date();
        dto.setBirthDate(now);

        Object result = extractor.apply(dto);
        assertEquals(now, result);
        assertInstanceOf(Date.class, result);
    }

    @Test
    void createExtractor_withBooleanField_returnsExtractorThatPreservesBoolean() throws NoSuchFieldException {
        Field activeField = TestDTO.class.getDeclaredField("active");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Active")
                .order(0)
                .width(50)
                .format("")
                .field(activeField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        dto.setActive(true);

        Object result = extractor.apply(dto);
        assertEquals(true, result);
        assertInstanceOf(Boolean.class, result);
    }

    @Test
    void createExtractor_withNullObject_returnsNull() throws NoSuchFieldException {
        Field nameField = TestDTO.class.getDeclaredField("name");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Name")
                .order(0)
                .width(100)
                .format("")
                .field(nameField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        Object result = extractor.apply(null);
        assertNull(result);
    }

    @Test
    void createExtractor_withNullFieldValue_returnsNull() throws NoSuchFieldException {
        Field nameField = TestDTO.class.getDeclaredField("name");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Name")
                .order(0)
                .width(100)
                .format("")
                .field(nameField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        dto.setName(null);

        Object result = extractor.apply(dto);
        assertNull(result);
    }

    @Test
    void createExtractor_withNoGetter_throwsException() throws NoSuchFieldException {
        Field noGetterField = TestDTO.class.getDeclaredField("noGetter");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("NoGetter")
                .order(0)
                .width(100)
                .format("")
                .field(noGetterField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        assertThrows(ExcelExporterException.class, () -> extractor.apply(dto));
    }

    @Test
    void createExtractor_withVoidReturnGetter_returnsNull() throws NoSuchFieldException {
        Field voidReturnField = TestDTO.class.getDeclaredField("voidReturn");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("VoidReturn")
                .order(0)
                .width(100)
                .format("")
                .field(voidReturnField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        Object result = extractor.apply(dto);
        assertNull(result);
    }

    @Test
    void createExtractor_withParameterizedGetter_throwsException() throws NoSuchFieldException {
        Field withParamField = TestDTO.class.getDeclaredField("withParam");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("WithParam")
                .order(0)
                .width(100)
                .format("")
                .field(withParamField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        assertThrows(ExcelExporterException.class, () -> extractor.apply(dto));
    }

    @Test
    void createExtractor_withCapitalVoidReturnGetter_returnsNull() throws NoSuchFieldException {
        Field capitalVoidField = TestDTO.class.getDeclaredField("capitalVoidReturn");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("CapitalVoidReturn")
                .order(0)
                .width(100)
                .format("")
                .field(capitalVoidField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        Object result = extractor.apply(dto);
        assertNull(result);
    }

    @Test
    void createExtractor_withInvocationTargetException_throwsExcelExporterException() throws NoSuchFieldException {
        Field throwingField = TestDTO.class.getDeclaredField("throwingGetter");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("ThrowingGetter")
                .order(0)
                .width(100)
                .format("")
                .field(throwingField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> extractor.apply(dto)
        );

        assertEquals(ErrorCode.FIELD_ACCESS_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("필드 값 추출 실패"));
    }

    @Test
    void createExtractor_withLocalDateField_preservesLocalDate() throws NoSuchFieldException {
        Field localDateField = TestDTO.class.getDeclaredField("localDate");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("LocalDate")
                .order(0)
                .width(100)
                .format("")
                .field(localDateField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        LocalDate now = LocalDate.now();
        dto.setLocalDate(now);

        Object result = extractor.apply(dto);
        assertEquals(now, result);
        assertInstanceOf(LocalDate.class, result);
    }

    @Test
    void createExtractor_withLocalDateTimeField_preservesLocalDateTime() throws NoSuchFieldException {
        Field localDateTimeField = TestDTO.class.getDeclaredField("localDateTime");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("LocalDateTime")
                .order(0)
                .width(100)
                .format("")
                .field(localDateTimeField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        LocalDateTime now = LocalDateTime.now();
        dto.setLocalDateTime(now);

        Object result = extractor.apply(dto);
        assertEquals(now, result);
        assertInstanceOf(LocalDateTime.class, result);
    }

    @Test
    void createExtractor_withBigDecimalField_preservesBigDecimal() throws NoSuchFieldException {
        Field bigDecimalField = TestDTO.class.getDeclaredField("amount");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("Amount")
                .order(0)
                .width(100)
                .format("")
                .field(bigDecimalField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        BigDecimal amount = new BigDecimal("12345.67");
        dto.setAmount(amount);

        Object result = extractor.apply(dto);
        assertEquals(amount, result);
        assertInstanceOf(BigDecimal.class, result);
    }

    @Test
    void createExtractor_withCustomObjectField_convertsToString() throws NoSuchFieldException {
        Field customObjectField = TestDTO.class.getDeclaredField("customObject");
        ColumnInfo columnInfo = ColumnInfo.builder()
                .header("CustomObject")
                .order(0)
                .width(100)
                .format("")
                .field(customObjectField)
                .masking(Masking.NONE)
                .build();

        Function<TestDTO, Object> extractor = FieldValueExtractorFactory.createExtractor(columnInfo);

        TestDTO dto = new TestDTO();
        CustomObject customObject = new CustomObject("TestValue");
        dto.setCustomObject(customObject);

        Object result = extractor.apply(dto);
        assertEquals("CustomObject(value=TestValue)", result);
        assertInstanceOf(String.class, result);
    }

    private static class TestDTO {
        private String name;
        private Integer age;
        private Date birthDate;
        private Boolean active;
        private String noGetter;
        private String voidReturn;
        private String withParam;
        private String capitalVoidReturn;
        private String throwingGetter;
        private LocalDate localDate;
        private LocalDateTime localDateTime;
        private BigDecimal amount;
        private CustomObject customObject;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Date getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(Date birthDate) {
            this.birthDate = birthDate;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public void getVoidReturn() {
        }

        public String getWithParam(String param) {
            return param;
        }

        public Void getCapitalVoidReturn() {
            return null;
        }

        public String getThrowingGetter() {
            throw new RuntimeException("Intentional exception for testing");
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public CustomObject getCustomObject() {
            return customObject;
        }

        public void setCustomObject(CustomObject customObject) {
            this.customObject = customObject;
        }
    }

    private static class CustomObject {
        private final String value;

        public CustomObject(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "CustomObject(value=" + value + ")";
        }
    }
}
