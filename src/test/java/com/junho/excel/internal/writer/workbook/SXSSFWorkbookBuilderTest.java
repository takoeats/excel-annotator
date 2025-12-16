package com.junho.excel.internal.writer.workbook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.metadata.ExcelMetadata;
import com.junho.excel.internal.writer.RowWriter;
import com.junho.excel.internal.writer.SheetWriteContext;
import com.junho.excel.internal.writer.SheetWriteRequest;
import com.junho.excel.internal.writer.SheetWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SXSSFWorkbookBuilderTest {

  private SXSSFWorkbookBuilder builder;

  @BeforeEach
  void setUp() {
    SheetWriter sheetWriter = new SheetWriter(new RowWriter());
    builder = new SXSSFWorkbookBuilder(sheetWriter);
  }

  @Test
  void createWorkbookAndWrite_withSingleSheet_createsWorkbook() throws Exception {
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

    try (SXSSFWorkbook wb = builder.createWorkbookAndWrite(context)) {
      assertNotNull(wb);
      assertEquals(1, wb.getNumberOfSheets());
      assertEquals("Test Sheet", wb.getSheetAt(0).getSheetName());
    }
  }

  @Test
  void createWorkbookAndWrite_withMultipleSheets_createsAllSheets() throws Exception {
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

    try (SXSSFWorkbook wb = builder.createWorkbookAndWrite(context)) {
      assertNotNull(wb);
      assertEquals(2, wb.getNumberOfSheets());
    }
  }

  @Test
  void createWorkbookAndWrite_withColumnBasedSplit_adjustsBufferSize() throws Exception {
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

    try (SXSSFWorkbook wb = builder.createWorkbookAndWrite(context)) {
      assertNotNull(wb);
      assertEquals(2, wb.getNumberOfSheets());
    }
  }

  @Test
  void createWorkbookAndWrite_withLargeDataset_handlesCorrectly() throws Exception {
    List<TestDTO> largeData = new ArrayList<>();
    for (int i = 0; i < 10000; i++) {
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

    try (SXSSFWorkbook wb = builder.createWorkbookAndWrite(context)) {
      assertNotNull(wb);
      assertEquals(1, wb.getNumberOfSheets());
      Sheet sheet = wb.getSheetAt(0);
      assertEquals(10001, sheet.getPhysicalNumberOfRows());
    }
  }

  @Test
  void createWorkbookAndWrite_withNullIterator_throwsException() {
    assertThrows(IllegalStateException.class, () -> {
      ExcelMetadata<TestDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);

      SheetWriteRequest.<TestDTO>builder()
          .dataIterator(null)
          .metadata(metadata)
          .build();
    });
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
  @ExcelSheet("Multi")
  public static class MultiSheetDTO {

    @ExcelColumn(header = "Column A", order = 1, sheetName = "Sheet A")
    private String colA;

    @ExcelColumn(header = "Column B", order = 2, sheetName = "Sheet B")
    private String colB;
  }
}
