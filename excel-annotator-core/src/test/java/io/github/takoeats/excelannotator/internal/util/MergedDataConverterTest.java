package io.github.takoeats.excelannotator.internal.util;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.internal.SheetDataEntry;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MergedDataConverter 테스트")
class MergedDataConverterTest {

    @Getter
    @ExcelSheet("TestSheet")
    public static class DataA {
        @ExcelColumn(header = "A1", order = 1)
        private String a1;
        @ExcelColumn(header = "A2", order = 2)
        private String a2;

        public DataA(String a1, String a2) {
            this.a1 = a1;
            this.a2 = a2;
        }
    }

    @Getter
    @ExcelSheet("TestSheet")
    public static class DataB {
        @ExcelColumn(header = "B1", order = 3)
        private String b1;
        @ExcelColumn(header = "B2", order = 4)
        private String b2;

        public DataB(String b1, String b2) {
            this.b1 = b1;
            this.b2 = b2;
        }
    }

    @Test
    @DisplayName("Iterator.next() - hasNext() 체크 없이 호출 시 NoSuchElementException 발생")
    void next_withoutHasNextCheck_shouldThrowNoSuchElementException() {
        List<DataA> dataAList = Arrays.asList(
                new DataA("A1", "A2")
        );

        List<SheetDataEntry> entries = new ArrayList<>();
        entries.add(new SheetDataEntry(dataAList.iterator(), DataA.class));

        MergedDataConverter.MergedDataResult result =
                MergedDataConverter.convertToMergedData("TestSheet", entries, false);
        Iterator<Map<String, Object>> iterator = result.getDataIterator();

        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    @DisplayName("다른 길이의 데이터 병합 - else 블록 도달 검증")
    void mergeData_withDifferentLengths_shouldHandleElseBlock() {
        List<DataA> dataAList = Arrays.asList(
                new DataA("A1-1", "A2-1"),
                new DataA("A1-2", "A2-2")
        );

        List<DataB> dataBList = Arrays.asList(
                new DataB("B1-1", "B2-1"),
                new DataB("B1-2", "B2-2"),
                new DataB("B1-3", "B2-3"),
                new DataB("B1-4", "B2-4")
        );

        List<SheetDataEntry> entries = new ArrayList<>();
        entries.add(new SheetDataEntry(dataAList.iterator(), DataA.class));
        entries.add(new SheetDataEntry(dataBList.iterator(), DataB.class));

        MergedDataConverter.MergedDataResult result =
                MergedDataConverter.convertToMergedData("TestSheet", entries, false);
        Iterator<Map<String, Object>> iterator = result.getDataIterator();

        int rowCount = 0;
        while (iterator.hasNext()) {
            Map<String, Object> row = iterator.next();
            rowCount++;

            if (rowCount <= 2) {
                assertNotNull(row.get("0"));
                assertNotNull(row.get("1"));
                assertNotNull(row.get("2"));
                assertNotNull(row.get("3"));
            } else {
                assertNull(row.get("0"));
                assertNull(row.get("1"));
                assertNotNull(row.get("2"));
                assertNotNull(row.get("3"));
            }
        }

        assertEquals(4, rowCount);
    }

    @Test
    @DisplayName("순차 병합 - 모든 행의 컬럼 인덱스가 올바르게 배치되는지 확인")
    void mergeDataSequentially_shouldMaintainCorrectColumnIndices() {
        List<DataA> dataAList = Arrays.asList(
                new DataA("A1-1", "A2-1")
        );

        List<DataB> dataBList = Arrays.asList(
                new DataB("B1-1", "B2-1")
        );

        List<SheetDataEntry> entries = new ArrayList<>();
        entries.add(new SheetDataEntry(dataAList.iterator(), DataA.class));
        entries.add(new SheetDataEntry(dataBList.iterator(), DataB.class));

        MergedDataConverter.MergedDataResult result =
                MergedDataConverter.convertToMergedData("TestSheet", entries, false);
        Iterator<Map<String, Object>> iterator = result.getDataIterator();

        assertTrue(iterator.hasNext());
        Map<String, Object> row = iterator.next();

        assertEquals("A1-1", row.get("0"));
        assertEquals("A2-1", row.get("1"));
        assertEquals("B1-1", row.get("2"));
        assertEquals("B2-1", row.get("3"));
    }

    @Test
    @DisplayName("order 충돌 없이 병합 - 데이터 길이가 다를 때")
    void mergeDataByOrder_withDifferentLengths_shouldHandleCorrectly() {
        List<DataA> dataAList = Arrays.asList(
                new DataA("A1-1", "A2-1"),
                new DataA("A1-2", "A2-2"),
                new DataA("A1-3", "A2-3")
        );

        List<DataB> dataBList = Arrays.asList(
                new DataB("B1-1", "B2-1")
        );

        List<SheetDataEntry> entries = new ArrayList<>();
        entries.add(new SheetDataEntry(dataAList.iterator(), DataA.class));
        entries.add(new SheetDataEntry(dataBList.iterator(), DataB.class));

        MergedDataConverter.MergedDataResult result =
                MergedDataConverter.convertToMergedData("TestSheet", entries, false);
        Iterator<Map<String, Object>> iterator = result.getDataIterator();

        int rowCount = 0;
        while (iterator.hasNext()) {
            Map<String, Object> row = iterator.next();
            rowCount++;

            if (rowCount == 1) {
                assertEquals("A1-1", row.get("0"));
                assertEquals("A2-1", row.get("1"));
                assertEquals("B1-1", row.get("2"));
                assertEquals("B2-1", row.get("3"));
            } else if (rowCount == 2) {
                assertEquals("A1-2", row.get("0"));
                assertEquals("A2-2", row.get("1"));
                assertNull(row.get("2"));
                assertNull(row.get("3"));
            } else if (rowCount == 3) {
                assertEquals("A1-3", row.get("0"));
                assertEquals("A2-3", row.get("1"));
                assertNull(row.get("2"));
                assertNull(row.get("3"));
            }
        }

        assertEquals(3, rowCount);
    }
}
