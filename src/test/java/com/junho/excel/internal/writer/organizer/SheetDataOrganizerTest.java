package com.junho.excel.internal.writer.organizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.SheetGroupInfo;
import com.junho.excel.internal.writer.adapter.DataStreamAdapter;
import com.junho.excel.internal.writer.validation.ExcelDataValidator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SheetDataOrganizerTest {

  private SheetDataOrganizer organizer;

  @BeforeEach
  void setUp() {
    DataStreamAdapter streamAdapter = new DataStreamAdapter();
    ExcelDataValidator dataValidator = new ExcelDataValidator();
    organizer = new SheetDataOrganizer(streamAdapter, dataValidator);
  }

  @Test
  void groupSheetDataFromStreams_singleSheet_groupsCorrectly() {
    Map<String, Stream<?>> streamMap = new LinkedHashMap<>();
    streamMap.put("key1", Stream.of(new SheetADTO("data1"), new SheetADTO("data2")));

    Map<String, SheetGroupInfo> result = organizer.groupSheetDataFromStreams(streamMap);

    assertEquals(1, result.size());
    assertTrue(result.containsKey("Sheet A"));
  }

  @Test
  void groupSheetDataFromStreams_multipleSheetsWithSameName_mergesIntoOneGroup() {
    Map<String, Stream<?>> streamMap = new LinkedHashMap<>();
    streamMap.put("key1", Stream.of(new SheetADTO("data1")));
    streamMap.put("key2", Stream.of(new SheetADTO("data2")));

    Map<String, SheetGroupInfo> result = organizer.groupSheetDataFromStreams(streamMap);

    assertEquals(1, result.size());
    assertTrue(result.containsKey("Sheet A"));
    assertEquals(2, result.get("Sheet A").getEntries().size());
  }

  @Test
  void groupSheetDataFromStreams_multipleSheetsWithDifferentNames_createsSeparateGroups() {
    Map<String, Stream<?>> streamMap = new LinkedHashMap<>();
    streamMap.put("key1", Stream.of(new SheetADTO("data1")));
    streamMap.put("key2", Stream.of(new SheetBDTO("data2")));

    Map<String, SheetGroupInfo> result = organizer.groupSheetDataFromStreams(streamMap);

    assertEquals(2, result.size());
    assertTrue(result.containsKey("Sheet A"));
    assertTrue(result.containsKey("Sheet B"));
  }

  @Test
  void groupSheetDataFromStreams_emptyStream_throwsException() {
    Map<String, Stream<?>> streamMap = new LinkedHashMap<>();
    streamMap.put("key1", Stream.empty());

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> organizer.groupSheetDataFromStreams(streamMap)
    );

    assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
    assertTrue(exception.getMessage().contains("key1"));
  }

  @Test
  void groupSheetDataFromStreams_preservesOrder_usesLinkedHashMap() {
    Map<String, Stream<?>> streamMap = new LinkedHashMap<>();
    streamMap.put("key1", Stream.of(new SheetADTO("data1")));
    streamMap.put("key2", Stream.of(new SheetBDTO("data2")));
    streamMap.put("key3", Stream.of(new SheetCDTO("data3")));

    Map<String, SheetGroupInfo> result = organizer.groupSheetDataFromStreams(streamMap);

    List<String> sheetNames = new ArrayList<>(result.keySet());
    assertEquals("Sheet A", sheetNames.get(0));
    assertEquals("Sheet B", sheetNames.get(1));
    assertEquals("Sheet C", sheetNames.get(2));
  }

  @Test
  void groupSheetDataFromStreams_withOrderedSheets_capturesOrder() {
    Map<String, Stream<?>> streamMap = new LinkedHashMap<>();
    streamMap.put("key1", Stream.of(new OrderedSheetDTO("data1")));

    Map<String, SheetGroupInfo> result = organizer.groupSheetDataFromStreams(streamMap);

    assertTrue(result.containsKey("Ordered Sheet"));
    assertEquals(10, result.get("Ordered Sheet").getOrder());
  }

  @Test
  void sortSheetsByOrder_withoutOrder_placedFirst() {
    Map<String, SheetGroupInfo> groupedData = new LinkedHashMap<>();
    groupedData.put("Sheet A", new SheetGroupInfo(Integer.MIN_VALUE));
    groupedData.put("Sheet B", new SheetGroupInfo(1));

    List<Map.Entry<String, SheetGroupInfo>> result = organizer.sortSheetsByOrder(groupedData);

    assertEquals(2, result.size());
    assertEquals("Sheet A", result.get(0).getKey());
    assertEquals("Sheet B", result.get(1).getKey());
  }

  @Test
  void sortSheetsByOrder_multipleWithoutOrder_maintainsInsertionOrder() {
    Map<String, SheetGroupInfo> groupedData = new LinkedHashMap<>();
    groupedData.put("Sheet A", new SheetGroupInfo(Integer.MIN_VALUE));
    groupedData.put("Sheet B", new SheetGroupInfo(Integer.MIN_VALUE));
    groupedData.put("Sheet C", new SheetGroupInfo(Integer.MIN_VALUE));

    List<Map.Entry<String, SheetGroupInfo>> result = organizer.sortSheetsByOrder(groupedData);

    assertEquals(3, result.size());
    assertEquals("Sheet A", result.get(0).getKey());
    assertEquals("Sheet B", result.get(1).getKey());
    assertEquals("Sheet C", result.get(2).getKey());
  }

  @Test
  void sortSheetsByOrder_withOrderOnly_sortsByOrder() {
    Map<String, SheetGroupInfo> groupedData = new LinkedHashMap<>();
    groupedData.put("Sheet C", new SheetGroupInfo(3));
    groupedData.put("Sheet A", new SheetGroupInfo(1));
    groupedData.put("Sheet B", new SheetGroupInfo(2));

    List<Map.Entry<String, SheetGroupInfo>> result = organizer.sortSheetsByOrder(groupedData);

    assertEquals(3, result.size());
    assertEquals("Sheet A", result.get(0).getKey());
    assertEquals("Sheet B", result.get(1).getKey());
    assertEquals("Sheet C", result.get(2).getKey());
  }

  @Test
  void sortSheetsByOrder_mixedOrderAndNoOrder_noOrderFirst() {
    Map<String, SheetGroupInfo> groupedData = new LinkedHashMap<>();
    groupedData.put("Ordered 1", new SheetGroupInfo(1));
    groupedData.put("No Order A", new SheetGroupInfo(Integer.MIN_VALUE));
    groupedData.put("Ordered 2", new SheetGroupInfo(2));
    groupedData.put("No Order B", new SheetGroupInfo(Integer.MIN_VALUE));

    List<Map.Entry<String, SheetGroupInfo>> result = organizer.sortSheetsByOrder(groupedData);

    assertEquals(4, result.size());
    assertEquals("No Order A", result.get(0).getKey());
    assertEquals("No Order B", result.get(1).getKey());
    assertEquals("Ordered 1", result.get(2).getKey());
    assertEquals("Ordered 2", result.get(3).getKey());
  }

  @Test
  void sortSheetsByOrder_duplicateOrders_throwsException() {
    Map<String, SheetGroupInfo> groupedData = new LinkedHashMap<>();
    groupedData.put("Sheet A", new SheetGroupInfo(1));
    groupedData.put("Sheet B", new SheetGroupInfo(1));

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> organizer.sortSheetsByOrder(groupedData)
    );

    assertEquals(ErrorCode.DUPLICATE_SHEET_ORDER, exception.getErrorCode());
  }

  @Test
  void sortSheetsByOrder_emptyMap_returnsEmptyList() {
    Map<String, SheetGroupInfo> emptyMap = new LinkedHashMap<>();

    List<Map.Entry<String, SheetGroupInfo>> result = organizer.sortSheetsByOrder(emptyMap);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void sortSheetsByOrder_negativeOrders_sortedCorrectly() {
    Map<String, SheetGroupInfo> groupedData = new LinkedHashMap<>();
    groupedData.put("Sheet C", new SheetGroupInfo(1));
    groupedData.put("Sheet A", new SheetGroupInfo(-1));
    groupedData.put("Sheet B", new SheetGroupInfo(0));

    List<Map.Entry<String, SheetGroupInfo>> result = organizer.sortSheetsByOrder(groupedData);

    assertEquals(3, result.size());
    assertEquals("Sheet A", result.get(0).getKey());
    assertEquals("Sheet B", result.get(1).getKey());
    assertEquals("Sheet C", result.get(2).getKey());
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Sheet A")
  public static class SheetADTO {

    @ExcelColumn(header = "Data", order = 1)
    private String data;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Sheet B")
  public static class SheetBDTO {

    @ExcelColumn(header = "Data", order = 1)
    private String data;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet("Sheet C")
  public static class SheetCDTO {

    @ExcelColumn(header = "Data", order = 1)
    private String data;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ExcelSheet(value = "Ordered Sheet", order = 10)
  public static class OrderedSheetDTO {

    @ExcelColumn(header = "Data", order = 1)
    private String data;
  }
}
