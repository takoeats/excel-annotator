package io.github.takoeats.excelannotator.internal.metadata.extractor;

import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import io.github.takoeats.excelannotator.masking.Masking;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
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

    private static class TestDTO {
        private String name;
        private Integer age;
        private Date birthDate;
        private Boolean active;
        private String noGetter;
        private String voidReturn;
        private String withParam;

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
    }
}
