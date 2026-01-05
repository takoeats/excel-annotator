package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StreamAPIEdgeCaseTest {

    @BeforeAll
    static void setUp() {
        IOUtils.setByteArrayMaxOverride(200_000_000);
    }

    @Test
    void streamAPI_emptyStream_throwsException() {
        Stream<StreamTestDTO> emptyStream = Stream.empty();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromStream(baos, "test.xlsx", emptyStream));
    }

    @Test
    void streamAPI_nullStream_throwsException() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Stream<StreamTestDTO> nullStream = null;

        assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromStream(baos, "test.xlsx", nullStream));
    }

    @Test
    void streamAPI_singleElement_createsWorkbook() throws Exception {
        Stream<StreamTestDTO> stream = Stream.of(new StreamTestDTO("Single"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ExcelExporter.excelFromStream(baos, "test.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(1, wb.getNumberOfSheets());
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(2, sheet.getPhysicalNumberOfRows());

        DataFormatter fmt = new DataFormatter();
        Row dataRow = sheet.getRow(1);
        assertEquals("Single", fmt.formatCellValue(dataRow.getCell(0)));
        wb.close();
    }

    @Test
    void streamAPI_largeDataset_memoryEfficiency() throws Exception {
        int rowCount = 100000;
        Stream<StreamTestDTO> stream = IntStream.range(0, rowCount)
                .mapToObj(i -> new StreamTestDTO("Item" + i));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        assertTrue(sheet.getPhysicalNumberOfRows() > 1000);
        wb.close();
    }

    @Test
    void streamAPI_streamConsumedOnce_cannotReuse() {
        List<StreamTestDTO> data = new ArrayList<>();
        data.add(new StreamTestDTO("Test1"));
        data.add(new StreamTestDTO("Test2"));

        Stream<StreamTestDTO> stream = data.stream();

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos1, "test1.xlsx", stream);

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ExcelExporterException exception = assertThrows(ExcelExporterException.class,
                () -> ExcelExporter.excelFromStream(baos2, "test2.xlsx", stream));
        assertEquals(ErrorCode.STREAM_ALREADY_CONSUMED, exception.getErrorCode());
    }

    @Test
    void streamAPI_infiniteStreamWithLimit_handlesCorrectly() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        Stream<StreamTestDTO> infiniteStream = Stream.generate(
                () -> new StreamTestDTO("Item" + counter.incrementAndGet())
        ).limit(1000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", infiniteStream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(1001, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void streamAPI_parallelStream_processesCorrectly() throws Exception {
        int rowCount = 10000;
        Stream<StreamTestDTO> parallelStream = IntStream.range(0, rowCount)
                .parallel()
                .mapToObj(i -> new StreamTestDTO("Parallel" + i));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", parallelStream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(rowCount + 1, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void streamAPI_filteredStream_onlyProcessedElements() throws Exception {
        Stream<StreamTestDTO> stream = IntStream.range(0, 100)
                .mapToObj(i -> new StreamTestDTO("Item" + i))
                .filter(dto -> dto.getName().contains("1") || dto.getName().contains("2"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        assertTrue(sheet.getPhysicalNumberOfRows() > 1);
        assertTrue(sheet.getPhysicalNumberOfRows() < 101);
        wb.close();
    }

    @Test
    void streamAPI_nullElementsInStream_handlesGracefully() {
        Stream<StreamTestDTO> streamWithNulls = Stream.of(
                new StreamTestDTO("First"),
                null,
                new StreamTestDTO("Third")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertDoesNotThrow(() ->
                ExcelExporter.excelFromStream(baos, "test.xlsx", streamWithNulls)
        );
    }

    @Test
    void streamAPI_distinctStream_noDuplicates() throws Exception {
        Stream<StreamTestDTO> stream = Stream.of(
                new StreamTestDTO("A"),
                new StreamTestDTO("B"),
                new StreamTestDTO("A"),
                new StreamTestDTO("C")
        ).distinct();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        assertTrue(sheet.getPhysicalNumberOfRows() <= 5);
        wb.close();
    }

    @Test
    void streamAPI_sortedStream_maintainsOrder() throws Exception {
        Stream<StreamTestDTO> stream = Stream.of(
                new StreamTestDTO("Zebra"),
                new StreamTestDTO("Apple"),
                new StreamTestDTO("Mango")
        ).sorted(Comparator.comparing(StreamTestDTO::getName));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        DataFormatter fmt = new DataFormatter();
        assertEquals("Apple", fmt.formatCellValue(sheet.getRow(1).getCell(0)));
        assertEquals("Mango", fmt.formatCellValue(sheet.getRow(2).getCell(0)));
        assertEquals("Zebra", fmt.formatCellValue(sheet.getRow(3).getCell(0)));
        wb.close();
    }

    @Test
    void streamAPI_mappedStream_transformsCorrectly() throws Exception {
        Stream<String> rawStream = Stream.of("a", "b", "c");
        Stream<StreamTestDTO> mappedStream = rawStream.map(s -> new StreamTestDTO(s.toUpperCase()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", mappedStream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);

        DataFormatter fmt = new DataFormatter();
        assertEquals("A", fmt.formatCellValue(sheet.getRow(1).getCell(0)));
        assertEquals("B", fmt.formatCellValue(sheet.getRow(2).getCell(0)));
        assertEquals("C", fmt.formatCellValue(sheet.getRow(3).getCell(0)));
        wb.close();
    }

    @Test
    void streamAPI_flatMappedStream_flattensCorrectly() throws Exception {
        Stream<List<StreamTestDTO>> nestedStream = Stream.of(
                java.util.Arrays.asList(new StreamTestDTO("A1"), new StreamTestDTO("A2")),
                java.util.Arrays.asList(new StreamTestDTO("B1"), new StreamTestDTO("B2"))
        );

        Stream<StreamTestDTO> flatStream = nestedStream.flatMap(List::stream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", flatStream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(5, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void streamAPI_peekStream_doesNotAffectOutput() throws Exception {
        AtomicInteger peekCount = new AtomicInteger(0);
        Stream<StreamTestDTO> stream = Stream.of(
                new StreamTestDTO("X"),
                new StreamTestDTO("Y")
        ).peek(dto -> peekCount.incrementAndGet());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "test.xlsx", stream);

        assertTrue(peekCount.get() >= 2);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(3, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    @Tag("performance")
    void streamAPI_exceedsOneMillionRows_createsMultipleSheets() throws Exception {
        int totalRows = 1500000;
        Stream<StreamTestDTO> largeStream = IntStream.range(0, totalRows)
                .mapToObj(i -> new StreamTestDTO("Item" + i));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "large.xlsx", largeStream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(2, wb.getNumberOfSheets(), "150만 건 데이터는 2개 시트로 분리되어야 함");

        Sheet sheet1 = wb.getSheetAt(0);
        Sheet sheet2 = wb.getSheetAt(1);

        assertEquals("StreamTest", sheet1.getSheetName());
        assertEquals("StreamTest2", sheet2.getSheetName());

        assertEquals(1000001, sheet1.getPhysicalNumberOfRows(), "첫 번째 시트는 헤더 1행 + 데이터 100만 행");
        assertEquals(500001, sheet2.getPhysicalNumberOfRows(), "두 번째 시트는 헤더 1행 + 데이터 50만 행");

        DataFormatter fmt = new DataFormatter();
        assertEquals("Name", fmt.formatCellValue(sheet1.getRow(0).getCell(0)));
        assertEquals("Item0", fmt.formatCellValue(sheet1.getRow(1).getCell(0)));

        assertEquals("Name", fmt.formatCellValue(sheet2.getRow(0).getCell(0)));
        assertEquals("Item1000000", fmt.formatCellValue(sheet2.getRow(1).getCell(0)));

        wb.close();
    }

    @Test
    @Tag("performance")
    void streamAPI_exactlyOneMillionRows_singleSheet() throws Exception {
        int totalRows = 1000000;
        Stream<StreamTestDTO> stream = IntStream.range(0, totalRows)
                .mapToObj(i -> new StreamTestDTO("Item" + i));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "exact.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(1, wb.getNumberOfSheets(), "정확히 100만 건은 단일 시트");

        Sheet sheet = wb.getSheetAt(0);
        assertEquals("StreamTest", sheet.getSheetName());
        assertEquals(1000001, sheet.getPhysicalNumberOfRows(), "헤더 1행 + 데이터 100만 행");

        wb.close();
    }

    @Test
    @Tag("performance")
    void streamAPI_exactlyOneMillionRows_withMergedHeader_correctRowCount() throws Exception {
        int totalRows = 1000000;
        Stream<MergedHeaderTestDTO> stream = IntStream.range(0, totalRows)
                .mapToObj(i -> new MergedHeaderTestDTO("Item" + i, "item" + i + "@test.com"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "merged.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(1, wb.getNumberOfSheets(), "정확히 100만 건은 단일 시트");

        Sheet sheet = wb.getSheetAt(0);
        assertEquals("MergedHeaderTest", sheet.getSheetName());
        assertEquals(1000002, sheet.getPhysicalNumberOfRows(), "병합 헤더 2행 + 데이터 100만 행 = 1000002");

        wb.close();
    }

    @Test
    @Tag("performance")
    void streamAPI_overOneMillionRows_withMergedHeader_splitCorrectly() throws Exception {
        int totalRows = 1000001;
        Stream<MergedHeaderTestDTO> stream = IntStream.range(0, totalRows)
                .mapToObj(i -> new MergedHeaderTestDTO("Item" + i, "item" + i + "@test.com"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "merged_split.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(2, wb.getNumberOfSheets(), "100만 건 초과 시 시트 분할");

        Sheet sheet1 = wb.getSheetAt(0);
        assertEquals("MergedHeaderTest", sheet1.getSheetName());
        assertEquals(1000002, sheet1.getPhysicalNumberOfRows(), "첫 번째 시트: 병합 헤더 2행 + 데이터 100만 행");

        Sheet sheet2 = wb.getSheetAt(1);
        assertEquals("MergedHeaderTest2", sheet2.getSheetName());
        assertEquals(3, sheet2.getPhysicalNumberOfRows(), "두 번째 시트: 병합 헤더 2행 + 데이터 1행");

        wb.close();
    }

    @Test
    @Tag("performance")
    void streamAPI_exactlyOneMillionRows_noHeader_correctRowCount() throws Exception {
        int totalRows = 1000000;
        Stream<NoHeaderTestDTO> stream = IntStream.range(0, totalRows)
                .mapToObj(i -> new NoHeaderTestDTO("Item" + i));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "no_header.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(1, wb.getNumberOfSheets(), "정확히 100만 건은 단일 시트");

        Sheet sheet = wb.getSheetAt(0);
        assertEquals("NoHeaderTest", sheet.getSheetName());
        assertEquals(1000000, sheet.getPhysicalNumberOfRows(), "헤더 없음 + 데이터 100만 행 = 1000000");

        wb.close();
    }

    @Test
    @Tag("performance")
    void streamAPI_overOneMillionRows_noHeader_splitCorrectly() throws Exception {
        int totalRows = 1000001;
        Stream<NoHeaderTestDTO> stream = IntStream.range(0, totalRows)
                .mapToObj(i -> new NoHeaderTestDTO("Item" + i));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "no_header_split.xlsx", stream);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(2, wb.getNumberOfSheets(), "100만 건 초과 시 시트 분할");

        Sheet sheet1 = wb.getSheetAt(0);
        assertEquals("NoHeaderTest", sheet1.getSheetName());
        assertEquals(1000000, sheet1.getPhysicalNumberOfRows(), "첫 번째 시트: 데이터 100만 행");

        Sheet sheet2 = wb.getSheetAt(1);
        assertEquals("NoHeaderTest2", sheet2.getSheetName());
        assertEquals(1, sheet2.getPhysicalNumberOfRows(), "두 번째 시트: 데이터 1행");

        wb.close();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("StreamTest")
    public static class StreamTestDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StreamTestDTO that = (StreamTestDTO) o;
            return name != null && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("MergedHeaderTest")
    public static class MergedHeaderTestDTO {
        @ExcelColumn(header = "Name", order = 1, mergeHeader = "User Info")
        private String name;

        @ExcelColumn(header = "Email", order = 2, mergeHeader = "User Info")
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "NoHeaderTest", hasHeader = false)
    public static class NoHeaderTestDTO {
        @ExcelColumn(order = 1)
        private String name;
    }
}
