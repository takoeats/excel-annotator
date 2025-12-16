package com.junho.excel;
import com.junho.excel.internal.metadata.SheetInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.ExcelMetadataFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SheetOrderTest {

    @Nested
    @DisplayName("List API - Sheet Order Tests")
    class ListApiSheetOrderTests {

        @Test
        @DisplayName("order가 지정된 시트들은 order 순서대로 정렬된다")
        void sheetsWithOrder_sortedByOrder() throws Exception {
            Map<String, List<?>> sheetData = new LinkedHashMap<>();
            sheetData.put("third", Collections.singletonList(new OrderedSheet3DTO("C")));
            sheetData.put("first", Collections.singletonList(new OrderedSheet1DTO("A")));
            sheetData.put("second", Collections.singletonList(new OrderedSheet2DTO("B")));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

            Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
            assertEquals(3, wb.getNumberOfSheets());
            assertEquals("시트1", wb.getSheetAt(0).getSheetName());
            assertEquals("시트2", wb.getSheetAt(1).getSheetName());
            assertEquals("시트3", wb.getSheetAt(2).getSheetName());
            wb.close();
        }

        @Test
        @DisplayName("order가 없는 시트들은 입력 순서대로, order가 있는 시트들은 그 뒤에 정렬된다")
        void mixedOrderSheets_unorderedFirstThenOrdered() throws Exception {
            Map<String, List<?>> sheetData = new LinkedHashMap<>();
            sheetData.put("ordered2", Collections.singletonList(new OrderedSheet2DTO("B")));
            sheetData.put("unordered1", Collections.singletonList(new UnorderedSheet1DTO("X")));
            sheetData.put("ordered1", Collections.singletonList(new OrderedSheet1DTO("A")));
            sheetData.put("unordered2", Collections.singletonList(new UnorderedSheet2DTO("Y")));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

            Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
            assertEquals(4, wb.getNumberOfSheets());
            assertEquals("미정렬시트1", wb.getSheetAt(0).getSheetName());
            assertEquals("미정렬시트2", wb.getSheetAt(1).getSheetName());
            assertEquals("시트1", wb.getSheetAt(2).getSheetName());
            assertEquals("시트2", wb.getSheetAt(3).getSheetName());
            wb.close();
        }

        @Test
        @DisplayName("동일한 order 값을 가진 시트가 있으면 예외가 발생한다")
        void duplicateOrder_throwsException() {
            Map<String, List<?>> sheetData = new LinkedHashMap<>();
            sheetData.put("dup1", Collections.singletonList(new DuplicateOrder1DTO("A")));
            sheetData.put("dup2", Collections.singletonList(new DuplicateOrder2DTO("B")));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromList(baos, "test.xlsx", sheetData));

            assertEquals(ErrorCode.DUPLICATE_SHEET_ORDER, exception.getErrorCode());
            assertTrue(exception.getMessage().contains("중복된 시트 order"));
        }

        @Test
        @DisplayName("같은 시트명의 여러 DTO가 있을 때 첫 번째 DTO의 order를 사용한다")
        void sameSheetName_usesFirstDtoOrder() throws Exception {
            Map<String, List<?>> sheetData = new LinkedHashMap<>();
            sheetData.put("unordered", Collections.singletonList(new UnorderedSheet1DTO("X")));
            sheetData.put("merged1", Collections.singletonList(new MergedSheetOrder10DTO("A", "B")));
            sheetData.put("merged2", Collections.singletonList(new MergedSheetOrder20DTO("C", "D")));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

            Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
            assertEquals(2, wb.getNumberOfSheets());
            assertEquals("미정렬시트1", wb.getSheetAt(0).getSheetName());
            assertEquals("병합시트", wb.getSheetAt(1).getSheetName());
            wb.close();
        }

        @Test
        @DisplayName("order가 없는 시트만 있을 때 기존 순서 유지")
        void onlyUnorderedSheets_maintainsInputOrder() throws Exception {
            Map<String, List<?>> sheetData = new LinkedHashMap<>();
            sheetData.put("second", Collections.singletonList(new UnorderedSheet2DTO("Y")));
            sheetData.put("first", Collections.singletonList(new UnorderedSheet1DTO("X")));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

            Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
            assertEquals(2, wb.getNumberOfSheets());
            assertEquals("미정렬시트2", wb.getSheetAt(0).getSheetName());
            assertEquals("미정렬시트1", wb.getSheetAt(1).getSheetName());
            wb.close();
        }

        @Test
        @DisplayName("음수 order 값도 정상 동작한다")
        void negativeOrder_worksCorrectly() throws Exception {
            Map<String, List<?>> sheetData = new LinkedHashMap<>();
            sheetData.put("positive", Collections.singletonList(new PositiveOrderDTO("A")));
            sheetData.put("negative", Collections.singletonList(new NegativeOrderDTO("B")));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ExcelExporter.excelFromList(baos, "test.xlsx", sheetData);

            Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
            assertEquals(2, wb.getNumberOfSheets());
            assertEquals("음수순서시트", wb.getSheetAt(0).getSheetName());
            assertEquals("양수순서시트", wb.getSheetAt(1).getSheetName());
            wb.close();
        }
    }


    @Nested
    @DisplayName("ExcelMetadataFactory - SheetInfo Tests")
    class SheetInfoTests {

        @Test
        @DisplayName("@ExcelSheet.order가 설정된 경우 SheetInfo에서 order를 가져올 수 있다")
        void extractSheetInfo_withOrder() {
            SheetInfo sheetInfo = ExcelMetadataFactory.extractSheetInfo(OrderedSheet1DTO.class);

            assertEquals("시트1", sheetInfo.getName());
            assertTrue(sheetInfo.hasOrder());
            assertEquals(1, sheetInfo.getOrder());
        }

        @Test
        @DisplayName("@ExcelSheet.order가 미설정된 경우 hasOrder는 false를 반환한다")
        void extractSheetInfo_withoutOrder() {
            SheetInfo sheetInfo = ExcelMetadataFactory.extractSheetInfo(UnorderedSheet1DTO.class);

            assertEquals("미정렬시트1", sheetInfo.getName());
            assertEquals(Integer.MIN_VALUE, sheetInfo.getOrder());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "시트1", order = 1)
    public static class OrderedSheet1DTO {
        @ExcelColumn(header = "컬럼A", order = 1)
        private String columnA;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "시트2", order = 2)
    public static class OrderedSheet2DTO {
        @ExcelColumn(header = "컬럼B", order = 1)
        private String columnB;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "시트3", order = 3)
    public static class OrderedSheet3DTO {
        @ExcelColumn(header = "컬럼C", order = 1)
        private String columnC;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("미정렬시트1")
    public static class UnorderedSheet1DTO {
        @ExcelColumn(header = "컬럼X", order = 1)
        private String columnX;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("미정렬시트2")
    public static class UnorderedSheet2DTO {
        @ExcelColumn(header = "컬럼Y", order = 1)
        private String columnY;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "중복시트1", order = 5)
    public static class DuplicateOrder1DTO {
        @ExcelColumn(header = "컬럼A", order = 1)
        private String columnA;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "중복시트2", order = 5)
    public static class DuplicateOrder2DTO {
        @ExcelColumn(header = "컬럼B", order = 1)
        private String columnB;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "병합시트", order = 10)
    public static class MergedSheetOrder10DTO {
        @ExcelColumn(header = "필드1", order = 1)
        private String field1;
        @ExcelColumn(header = "필드2", order = 2)
        private String field2;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "병합시트", order = 20)
    public static class MergedSheetOrder20DTO {
        @ExcelColumn(header = "필드3", order = 3)
        private String field3;
        @ExcelColumn(header = "필드4", order = 4)
        private String field4;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "양수순서시트", order = 100)
    public static class PositiveOrderDTO {
        @ExcelColumn(header = "데이터", order = 1)
        private String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "음수순서시트", order = -100)
    public static class NegativeOrderDTO {
        @ExcelColumn(header = "데이터", order = 1)
        private String data;
    }
}
