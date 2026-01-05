package io.github.takoeats.excelannotator.internal.writer;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.internal.ExcelMetadataFactory;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.internal.metadata.SheetInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SheetWriterTest {

    private SXSSFWorkbook workbook;
    private SheetWriter sheetWriter;

    @BeforeEach
    void setUp() {
        workbook = new SXSSFWorkbook();
        sheetWriter = new SheetWriter(new RowWriter());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (workbook != null) {
            workbook.close();
        }
    }

    @Test
    void write_withSingleSheetRequest_createsOneSheet() {
        List<TestDTO> data = Arrays.asList(
                new TestDTO("Alice", 30),
                new TestDTO("Bob", 25)
        );

        ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
        SheetWriteRequest<TestDTO> request = SheetWriteRequest.<TestDTO>builder()
                .dataIterator(data.iterator())
                .metadata(metadata)
                .build();

        SheetWriteContext<TestDTO> context = SheetWriteContext.forRowBasedSheets(
                Collections.singletonList(request)
        );

        sheetWriter.write(workbook, context);

        assertEquals(1, workbook.getNumberOfSheets());
        assertEquals("Test Sheet", workbook.getSheetAt(0).getSheetName());
    }

    @Test
    void write_withMultipleSheetRequests_createsMultipleSheets() {
        List<TestDTO> data1 = Arrays.asList(new TestDTO("Alice", 30));
        List<AnotherDTO> data2 = Arrays.asList(new AnotherDTO("X"));

        ExcelMetadata<TestDTO> metadata1 = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
        ExcelMetadata<AnotherDTO> metadata2 = ExcelMetadataFactory.extractExcelMetadata(
                AnotherDTO.class);

        SheetWriteRequest<TestDTO> request1 = SheetWriteRequest.<TestDTO>builder()
                .dataIterator(data1.iterator())
                .metadata(metadata1)
                .build();

        SheetWriteRequest<AnotherDTO> request2 = SheetWriteRequest.<AnotherDTO>builder()
                .dataIterator(data2.iterator())
                .metadata(metadata2)
                .build();

        SheetWriteContext<?> context = SheetWriteContext.forRowBasedSheets(
                Arrays.asList(request1, request2)
        );

        sheetWriter.write(workbook, context);

        assertEquals(2, workbook.getNumberOfSheets());
        assertEquals("Test Sheet", workbook.getSheetAt(0).getSheetName());
        assertEquals("Another Sheet", workbook.getSheetAt(1).getSheetName());
    }

    @Test
    void write_withData_writesHeaderAndData() {
        List<TestDTO> data = Arrays.asList(
                new TestDTO("Alice", 30),
                new TestDTO("Bob", 25)
        );

        ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
        SheetWriteRequest<TestDTO> request = SheetWriteRequest.<TestDTO>builder()
                .dataIterator(data.iterator())
                .metadata(metadata)
                .build();

        SheetWriteContext<TestDTO> context = SheetWriteContext.forRowBasedSheets(
                Collections.singletonList(request)
        );

        sheetWriter.write(workbook, context);

        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter fmt = new DataFormatter();

        assertEquals("Name", fmt.formatCellValue(sheet.getRow(0).getCell(0)));
        assertEquals("Age", fmt.formatCellValue(sheet.getRow(0).getCell(1)));

        assertEquals("Alice", fmt.formatCellValue(sheet.getRow(1).getCell(0)));
        assertEquals("30", fmt.formatCellValue(sheet.getRow(1).getCell(1)));
    }

    @Test
    void write_withNoHeaderMetadata_doesNotWriteHeader() {
        List<NoHeaderDTO> data = Arrays.asList(new NoHeaderDTO("data"));

        ExcelMetadata<NoHeaderDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
                NoHeaderDTO.class);
        SheetWriteRequest<NoHeaderDTO> request = SheetWriteRequest.<NoHeaderDTO>builder()
                .dataIterator(data.iterator())
                .metadata(metadata)
                .build();

        SheetWriteContext<NoHeaderDTO> context = SheetWriteContext.forRowBasedSheets(
                Collections.singletonList(request)
        );

        sheetWriter.write(workbook, context);

        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter fmt = new DataFormatter();

        assertEquals("data", fmt.formatCellValue(sheet.getRow(0).getCell(0)));
    }

    @Test
    void write_columnBasedSplit_createsMultipleSheets() {
        List<MultiSheetDTO> data = Arrays.asList(
                new MultiSheetDTO("A1", "B1"),
                new MultiSheetDTO("A2", "B2")
        );

        List<ExcelMetadata<MultiSheetDTO>> metadataList = new ArrayList<>(
                ExcelMetadataFactory.extractMultiSheetMetadata(MultiSheetDTO.class).values()
        );

        SheetWriteContext<MultiSheetDTO> context = SheetWriteContext.forColumnBasedSheets(
                data.iterator(),
                metadataList
        );

        sheetWriter.write(workbook, context);

        assertEquals(2, workbook.getNumberOfSheets());
        assertTrue(context.isColumnBasedSplit());
    }

    @Test
    void write_withLargeDataset_handlesCorrectly() {
        List<TestDTO> largeData = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeData.add(new TestDTO("Person" + i, 20 + (i % 50)));
        }

        ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
        SheetWriteRequest<TestDTO> request = SheetWriteRequest.<TestDTO>builder()
                .dataIterator(largeData.iterator())
                .metadata(metadata)
                .build();

        SheetWriteContext<TestDTO> context = SheetWriteContext.forRowBasedSheets(
                Collections.singletonList(request)
        );

        sheetWriter.write(workbook, context);

        assertEquals(1, workbook.getNumberOfSheets());
        Sheet sheet = workbook.getSheetAt(0);
        assertEquals(1001, sheet.getPhysicalNumberOfRows());
    }

    @Test
    void write_sheetNameWithSpecialChars_sanitizesCorrectly() {
        List<TestDTO> data = Arrays.asList(new TestDTO("Test", 30));

        ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);
        ExcelMetadata<TestDTO> modifiedMetadata = ExcelMetadata.<TestDTO>builder()
                .headers(metadata.getHeaders())
                .extractors(metadata.getExtractors())
                .columnWidths(metadata.getColumnWidths())
                .columnInfos(metadata.getColumnInfos())
                .sheetInfo(SheetInfo.builder()
                        .name("Invalid:Name*")
                        .hasHeader(true)
                        .build())
                .build();

        SheetWriteRequest<TestDTO> request = SheetWriteRequest.<TestDTO>builder()
                .dataIterator(data.iterator())
                .metadata(modifiedMetadata)
                .build();

        SheetWriteContext<TestDTO> context = SheetWriteContext.forRowBasedSheets(
                Collections.singletonList(request)
        );

        sheetWriter.write(workbook, context);

        Sheet sheet = workbook.getSheetAt(0);
        String sheetName = sheet.getSheetName();
        assertFalse(sheetName.contains(":"));
        assertFalse(sheetName.contains("*"));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Test Sheet")
    public static class TestDTO {

        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(header = "Age", order = 2)
        private Integer age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Another Sheet")
    public static class AnotherDTO {

        @ExcelColumn(header = "Data", order = 1)
        private String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet(value = "NoHeader", hasHeader = false)
    public static class NoHeaderDTO {

        @ExcelColumn(header = "Data", order = 1)
        private String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("Multi")
    public static class MultiSheetDTO {

        @ExcelColumn(header = "Column A", order = 1, sheetName = "Sheet A")
        private String colA;

        @ExcelColumn(header = "Column B", order = 2, sheetName = "Sheet B")
        private String colB;
    }
}
