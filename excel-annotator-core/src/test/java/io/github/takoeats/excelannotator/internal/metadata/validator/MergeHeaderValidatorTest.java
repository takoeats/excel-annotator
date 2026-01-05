package io.github.takoeats.excelannotator.internal.metadata.validator;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import io.github.takoeats.excelannotator.masking.Masking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MergeHeaderValidator 테스트")
public class MergeHeaderValidatorTest {

    @Test
    @DisplayName("validateOrderContinuity - null 리스트는 예외를 던지지 않는다")
    void validateOrderContinuity_withNullList_doesNotThrow() {
        assertDoesNotThrow(() -> MergeHeaderValidator.validateOrderContinuity(null));
    }

    @Test
    @DisplayName("validateOrderContinuity - 빈 리스트는 예외를 던지지 않는다")
    void validateOrderContinuity_withEmptyList_doesNotThrow() {
        List<ColumnInfo> emptyList = new ArrayList<>();
        assertDoesNotThrow(() -> MergeHeaderValidator.validateOrderContinuity(emptyList));
    }

    @Test
    @DisplayName("validateOrderContinuity - mergeHeader가 없으면 검증을 건너뛴다")
    void validateOrderContinuity_withNoMergeHeader_doesNotThrow() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field2 = TestDTO.class.getDeclaredField("field2");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field1, "Header1", 1, null),
                createColumnInfo(field2, "Header2", 2, null)
        );

        assertDoesNotThrow(() -> MergeHeaderValidator.validateOrderContinuity(columnInfos));
    }

    @Test
    @DisplayName("validateOrderContinuity - 단일 mergeHeader (orders.size() < 2)는 검증을 건너뛴다")
    void validateOrderContinuity_withSingleMergeHeader_doesNotThrow() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field2 = TestDTO.class.getDeclaredField("field2");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field1, "Header1", 1, "Group1"),
                createColumnInfo(field2, "Header2", 2, null)
        );

        assertDoesNotThrow(() -> MergeHeaderValidator.validateOrderContinuity(columnInfos));
    }

    @Test
    @DisplayName("validateOrderContinuity - 연속된 order를 가진 mergeHeader는 예외를 던지지 않는다")
    void validateOrderContinuity_withContinuousOrders_doesNotThrow() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field2 = TestDTO.class.getDeclaredField("field2");
        Field field3 = TestDTO.class.getDeclaredField("field3");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field1, "Header1", 1, "Group1"),
                createColumnInfo(field2, "Header2", 2, "Group1"),
                createColumnInfo(field3, "Header3", 3, "Group1")
        );

        assertDoesNotThrow(() -> MergeHeaderValidator.validateOrderContinuity(columnInfos));
    }

    @Test
    @DisplayName("validateOrderContinuity - order gap이 있는 mergeHeader는 예외를 던진다")
    void validateOrderContinuity_withOrderGap_throwsException() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field2 = TestDTO.class.getDeclaredField("field2");
        Field field3 = TestDTO.class.getDeclaredField("field3");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field1, "Header1", 1, "Group1"),
                createColumnInfo(field2, "Header2", 2, null),
                createColumnInfo(field3, "Header3", 3, "Group1")
        );

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> MergeHeaderValidator.validateOrderContinuity(columnInfos)
        );

        assertEquals(ErrorCode.MERGE_HEADER_ORDER_GAP, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("병합 헤더 'Group1'"));
        assertTrue(exception.getMessage().contains("order=2"));
        assertTrue(exception.getMessage().contains("Header2"));
    }

    @Test
    @DisplayName("validateOrderContinuity - 여러 mergeHeader 그룹이 있을 때 각각 검증한다")
    void validateOrderContinuity_withMultipleMergeHeaderGroups_validatesEach() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field2 = TestDTO.class.getDeclaredField("field2");
        Field field3 = TestDTO.class.getDeclaredField("field3");
        Field field4 = TestDTO.class.getDeclaredField("field4");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field1, "Header1", 1, "GroupA"),
                createColumnInfo(field2, "Header2", 2, "GroupA"),
                createColumnInfo(field3, "Header3", 3, "GroupB"),
                createColumnInfo(field4, "Header4", 4, "GroupB")
        );

        assertDoesNotThrow(() -> MergeHeaderValidator.validateOrderContinuity(columnInfos));
    }

    @Test
    @DisplayName("validateOrderContinuity - 정렬되지 않은 order도 올바르게 검증한다")
    void validateOrderContinuity_withUnsortedOrders_validatesCorrectly() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field2 = TestDTO.class.getDeclaredField("field2");
        Field field3 = TestDTO.class.getDeclaredField("field3");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field3, "Header3", 3, "Group1"),
                createColumnInfo(field1, "Header1", 1, "Group1"),
                createColumnInfo(field2, "Header2", 2, "Group1")
        );

        assertDoesNotThrow(() -> MergeHeaderValidator.validateOrderContinuity(columnInfos));
    }

    @Test
    @DisplayName("validateOrderContinuity - gap이 있는 정렬되지 않은 order는 예외를 던진다")
    void validateOrderContinuity_withUnsortedOrdersAndGap_throwsException() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field2 = TestDTO.class.getDeclaredField("field2");
        Field field3 = TestDTO.class.getDeclaredField("field3");
        Field field4 = TestDTO.class.getDeclaredField("field4");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field4, "Header4", 4, "Group1"),
                createColumnInfo(field1, "Header1", 1, "Group1"),
                createColumnInfo(field2, "Header2", 2, null)
        );

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> MergeHeaderValidator.validateOrderContinuity(columnInfos)
        );

        assertEquals(ErrorCode.MERGE_HEADER_ORDER_GAP, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("병합 헤더 'Group1'"));
    }

    @Test
    @DisplayName("validateOrderContinuity - 여러 gap이 있을 때 첫 번째 gap에서 예외를 던진다")
    void validateOrderContinuity_withMultipleGaps_throwsAtFirstGap() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field2 = TestDTO.class.getDeclaredField("field2");
        Field field3 = TestDTO.class.getDeclaredField("field3");
        Field field4 = TestDTO.class.getDeclaredField("field4");
        Field field5 = TestDTO.class.getDeclaredField("field5");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field1, "Header1", 1, "Group1"),
                createColumnInfo(field2, "Header2", 2, null),
                createColumnInfo(field3, "Header3", 3, null),
                createColumnInfo(field4, "Header4", 4, "Group1"),
                createColumnInfo(field5, "Header5", 5, "Group1")
        );

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> MergeHeaderValidator.validateOrderContinuity(columnInfos)
        );

        assertTrue(exception.getMessage().contains("order=2"));
    }

    @Test
    @DisplayName("validateOrderContinuity - order가 중간에 빠진 경우 해당 컬럼을 찾지 못하면 'unknown'으로 표시한다")
    void validateOrderContinuity_withMissingOrderColumn_showsUnknown() throws NoSuchFieldException {
        Field field1 = TestDTO.class.getDeclaredField("field1");
        Field field3 = TestDTO.class.getDeclaredField("field3");

        List<ColumnInfo> columnInfos = Arrays.asList(
                createColumnInfo(field1, "Header1", 1, "Group1"),
                createColumnInfo(field3, "Header3", 3, "Group1")
        );

        ExcelExporterException exception = assertThrows(
                ExcelExporterException.class,
                () -> MergeHeaderValidator.validateOrderContinuity(columnInfos)
        );

        assertTrue(exception.getMessage().contains("order=2"));
        assertTrue(exception.getMessage().contains("'unknown'"));
    }

    private ColumnInfo createColumnInfo(Field field, String header, int order, String mergeHeader) {
        return ColumnInfo.builder()
                .field(field)
                .header(header)
                .order(order)
                .width(100)
                .format("")
                .mergeHeader(mergeHeader)
                .masking(Masking.NONE)
                .build();
    }

    public static class TestDTO {
        private String field1;
        private String field2;
        private String field3;
        private String field4;
        private String field5;

        public String getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
        }

        public String getField3() {
            return field3;
        }

        public String getField4() {
            return field4;
        }

        public String getField5() {
            return field5;
        }
    }
}
