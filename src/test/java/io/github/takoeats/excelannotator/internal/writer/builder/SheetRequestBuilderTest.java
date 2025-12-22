package io.github.takoeats.excelannotator.internal.writer.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.internal.ExcelMetadataFactory;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.internal.SheetDataEntry;
import io.github.takoeats.excelannotator.internal.writer.SheetWriteRequest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SheetRequestBuilderTest {

  private SheetRequestBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new SheetRequestBuilder();
  }

  @Test
  void createRequest_withValidParams_createsRequest() {
    List<SimpleDTO> data = Arrays.asList(
        new SimpleDTO("data1"),
        new SimpleDTO("data2")
    );
    Iterator<SimpleDTO> iterator = data.iterator();
    ExcelMetadata<SimpleDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        SimpleDTO.class);

    SheetWriteRequest<SimpleDTO> request = builder.createRequest(iterator, metadata);

    assertNotNull(request);
    assertNotNull(request.getDataIterator());
    assertNotNull(request.getMetadata());
    assertEquals("Simple Sheet", request.getMetadata().getSheetName());
  }

  @Test
  void createRequestsForSingleEntry_withSingleSheetDTO_createsSingleRequest() {
    List<SimpleDTO> data = Arrays.asList(new SimpleDTO("test"));
    SheetDataEntry entry = new SheetDataEntry(data.iterator(), SimpleDTO.class);

    List<SheetWriteRequest<?>> requests = builder.createRequestsForSingleEntry(
        "Custom Name", entry);

    assertEquals(1, requests.size());
    assertEquals("Custom Name", requests.get(0).getMetadata().getSheetName());
  }

  @Test
  void createRequestsForSingleEntry_withMultiSheetDTO_createsMultipleRequests() {
    List<MultiSheetDTO> data = Arrays.asList(
        new MultiSheetDTO("col1", "col2")
    );
    SheetDataEntry entry = new SheetDataEntry(data.iterator(), MultiSheetDTO.class);

    List<SheetWriteRequest<?>> requests = builder.createRequestsForSingleEntry(
        "Ignored", entry);

    assertEquals(2, requests.size());
    assertTrue(requests.stream().anyMatch(r -> "Sheet A".equals(r.getMetadata().getSheetName())));
    assertTrue(requests.stream().anyMatch(r -> "Sheet B".equals(r.getMetadata().getSheetName())));
  }

  @Test
  void createRequestsForSingleEntry_withInvalidSheetName_sanitizesName() {
    List<SimpleDTO> data = Arrays.asList(new SimpleDTO("test"));
    SheetDataEntry entry = new SheetDataEntry(data.iterator(), SimpleDTO.class);

    List<SheetWriteRequest<?>> requests = builder.createRequestsForSingleEntry(
        "Invalid:Name*", entry);

    assertEquals(1, requests.size());
    String sheetName = requests.get(0).getMetadata().getSheetName();
    assertNotNull(sheetName);
    assertTrue(!sheetName.contains(":"));
    assertTrue(!sheetName.contains("*"));
  }

  @Test
  void createRequestsForSingleEntry_preservesMetadata() {
    List<SimpleDTO> data = Arrays.asList(new SimpleDTO("test"));
    SheetDataEntry entry = new SheetDataEntry(data.iterator(), SimpleDTO.class);

    List<SheetWriteRequest<?>> requests = builder.createRequestsForSingleEntry(
        "Custom", entry);

    assertEquals(1, requests.size());
    ExcelMetadata<?> metadata = requests.get(0).getMetadata();
    assertTrue(metadata.hasHeader());
    assertEquals(1, metadata.getHeaders().size());
  }

  @Test
  void createRequestForMergedData_withMultipleEntries_createsMergedRequest() {
    List<SimpleDTO> data1 = Arrays.asList(new SimpleDTO("data1"));
    List<AnotherDTO> data2 = Arrays.asList(new AnotherDTO("data2"));

    List<SheetDataEntry> entries = Arrays.asList(
        new SheetDataEntry(data1.iterator(), SimpleDTO.class),
        new SheetDataEntry(data2.iterator(), AnotherDTO.class)
    );

    SheetWriteRequest<?> request = builder.createRequestForMergedData(
        "Merged Sheet", entries, true);

    assertNotNull(request);
    assertEquals("Merged Sheet", request.getMetadata().getSheetName());
    assertNotNull(request.getDataIterator());
  }

  @Test
  void createRequestForMergedData_withLinkedHashMap_preservesOrder() {
    List<SimpleDTO> data1 = Arrays.asList(new SimpleDTO("data1"));
    List<AnotherDTO> data2 = Arrays.asList(new AnotherDTO("data2"));

    List<SheetDataEntry> entries = Arrays.asList(
        new SheetDataEntry(data1.iterator(), SimpleDTO.class),
        new SheetDataEntry(data2.iterator(), AnotherDTO.class)
    );

    SheetWriteRequest<?> request = builder.createRequestForMergedData(
        "Ordered Sheet", entries, true);

    assertNotNull(request);
    assertEquals("Ordered Sheet", request.getMetadata().getSheetName());
  }

  @Test
  void createRequest_withEmptyIterator_createsValidRequest() {
    List<SimpleDTO> emptyData = Arrays.asList();
    Iterator<SimpleDTO> iterator = emptyData.iterator();
    ExcelMetadata<SimpleDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(
        SimpleDTO.class);

    SheetWriteRequest<SimpleDTO> request = builder.createRequest(iterator, metadata);

    assertNotNull(request);
    assertNotNull(request.getDataIterator());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Simple Sheet")
  public static class SimpleDTO {

    @ExcelColumn(header = "Data", order = 1)
    private String data;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Another Sheet")
  public static class AnotherDTO {

    @ExcelColumn(header = "Value", order = 1)
    private String value;
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
