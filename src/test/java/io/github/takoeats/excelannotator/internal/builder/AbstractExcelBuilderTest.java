package io.github.takoeats.excelannotator.internal.builder;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AbstractExcelBuilder 테스트")
public class AbstractExcelBuilderTest {

    private TestableExcelBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new TestableExcelBuilder();
    }

    @DisplayName("validateMapData - null 데이터는 예외를 발생시킨다")
    @Test
    void validateMapData_withNullData_throwsException() {
        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> builder.validateMapData(null)
        );

        assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
    }

    @DisplayName("validateMapData - 빈 Map은 예외를 발생시킨다")
    @Test
    void validateMapData_withEmptyMap_throwsException() {
        Map<String, List<String>> emptyMap = new HashMap<>();

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> builder.validateMapData(emptyMap)
        );

        assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
    }

    @DisplayName("validateMapData - 유효한 데이터는 예외를 발생시키지 않는다")
    @Test
    void validateMapData_withValidData_doesNotThrowException() {
        Map<String, List<String>> validMap = new HashMap<>();
        validMap.put("sheet1", Arrays.asList("data1", "data2"));

        assertDoesNotThrow(() -> builder.validateMapData(validMap));
    }

    @DisplayName("convertMapToStreams - List 타입 값을 Stream으로 변환한다")
    @Test
    void convertMapToStreams_withListValue_convertsToStream() {
        Map<String, Object> input = new HashMap<>();
        List<String> listData = Arrays.asList("item1", "item2", "item3");
        input.put("sheet1", listData);

        Map<String, Stream<?>> result = builder.convertMapToStreams(input);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("sheet1"));

        List<?> collected = result.get("sheet1").collect(java.util.stream.Collectors.toList());
        assertEquals(3, collected.size());
        assertEquals("item1", collected.get(0));
        assertEquals("item2", collected.get(1));
        assertEquals("item3", collected.get(2));
    }

    @DisplayName("convertMapToStreams - Stream 타입 값을 그대로 반환한다")
    @Test
    void convertMapToStreams_withStreamValue_returnsAsIs() {
        Map<String, Object> input = new HashMap<>();
        Stream<String> streamData = Stream.of("item1", "item2", "item3");
        input.put("sheet1", streamData);

        Map<String, Stream<?>> result = builder.convertMapToStreams(input);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("sheet1"));

        List<?> collected = result.get("sheet1").collect(java.util.stream.Collectors.toList());
        assertEquals(3, collected.size());
    }

    @DisplayName("convertMapToStreams - null 값은 예외를 발생시킨다")
    @Test
    void convertMapToStreams_withNullValue_throwsException() {
        Map<String, Object> input = new HashMap<>();
        input.put("sheet1", null);

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> builder.convertMapToStreams(input)
        );

        assertEquals(ErrorCode.WORKBOOK_CREATION_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Map values must be List or Stream, but was: null"));
    }

    @DisplayName("convertMapToStreams - List나 Stream이 아닌 타입은 예외를 발생시킨다")
    @Test
    void convertMapToStreams_withInvalidType_throwsException() {
        Map<String, Object> input = new HashMap<>();
        input.put("sheet1", "InvalidStringValue");

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> builder.convertMapToStreams(input)
        );

        assertEquals(ErrorCode.WORKBOOK_CREATION_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Map values must be List or Stream, but was: java.lang.String"));
    }

    @DisplayName("convertMapToStreams - 다양한 타입의 잘못된 값들은 예외를 발생시킨다")
    @Test
    void convertMapToStreams_withVariousInvalidTypes_throwsException() {
        Map<String, Object> inputWithInteger = new HashMap<>();
        inputWithInteger.put("sheet1", 123);

        ExcelExporterException intException = assertThrows(
                ExcelExporterException.class,
                () -> builder.convertMapToStreams(inputWithInteger)
        );
        assertTrue(intException.getMessage().contains("java.lang.Integer"));

        Map<String, Object> inputWithSet = new HashMap<>();
        inputWithSet.put("sheet1", new HashSet<>(Arrays.asList("a", "b")));

        ExcelExporterException setException = assertThrows(
                ExcelExporterException.class,
                () -> builder.convertMapToStreams(inputWithSet)
        );
        assertTrue(setException.getMessage().contains("java.util.HashSet"));

        Map<String, Object> inputWithArray = new HashMap<>();
        inputWithArray.put("sheet1", new String[]{"a", "b", "c"});

        ExcelExporterException arrayException = assertThrows(
                ExcelExporterException.class,
                () -> builder.convertMapToStreams(inputWithArray)
        );
        assertTrue(arrayException.getMessage().contains("[Ljava.lang.String"));
    }

    @DisplayName("convertMapToStreams - 여러 sheet를 동시에 변환한다")
    @Test
    void convertMapToStreams_withMultipleSheets_convertsAll() {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("sheet1", Arrays.asList("a", "b"));
        input.put("sheet2", Stream.of("x", "y"));
        input.put("sheet3", Collections.singletonList("z"));

        Map<String, Stream<?>> result = builder.convertMapToStreams(input);

        assertEquals(3, result.size());
        assertTrue(result.containsKey("sheet1"));
        assertTrue(result.containsKey("sheet2"));
        assertTrue(result.containsKey("sheet3"));
    }

    static class TestableExcelBuilder extends AbstractExcelBuilder {
        @Override
        public void validateMapData(Map<String, ?> sheetData) {
            super.validateMapData(sheetData);
        }

        @Override
        public Map<String, Stream<?>> convertMapToStreams(Map<String, ?> sheetData) {
            return super.convertMapToStreams(sheetData);
        }
    }
}
