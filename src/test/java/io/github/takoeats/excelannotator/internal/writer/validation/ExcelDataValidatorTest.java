package io.github.takoeats.excelannotator.internal.writer.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.SheetGroupInfo;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExcelDataValidatorTest {

  private ExcelDataValidator validator;

  @BeforeEach
  void setUp() {
    validator = new ExcelDataValidator();
  }

  @Test
  void validateDataNotEmpty_withValidList_doesNotThrow() {
    List<String> list = Arrays.asList("data1", "data2");
    validator.validateDataNotEmpty(list);
  }

  @Test
  void validateDataNotEmpty_withNullList_throwsException() {
    List<String> nullList = null;

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateDataNotEmpty(nullList)
    );

    assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
  }

  @Test
  void validateDataNotEmpty_withEmptyList_throwsException() {
    List<String> emptyList = Collections.emptyList();

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateDataNotEmpty(emptyList)
    );

    assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
  }

  @Test
  void validateDataNotEmpty_withSingleElementList_doesNotThrow() {
    List<String> singleList = Collections.singletonList("single");
    validator.validateDataNotEmpty(singleList);
  }

  @Test
  void validateDataNotEmpty_withValidMap_doesNotThrow() {
    Map<String, String> map = new HashMap<>();
    map.put("key1", "value1");
    validator.validateDataNotEmpty(map);
  }

  @Test
  void validateDataNotEmpty_withNullMap_throwsException() {
    Map<String, String> nullMap = null;

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateDataNotEmpty(nullMap)
    );

    assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
  }

  @Test
  void validateDataNotEmpty_withEmptyMap_throwsException() {
    Map<String, String> emptyMap = Collections.emptyMap();

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateDataNotEmpty(emptyMap)
    );

    assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
  }

  @Test
  void validateAndGetIterator_withValidStream_returnsIterator() {
    Stream<String> stream = Stream.of("data1", "data2", "data3");

    Iterator<String> iterator = validator.validateAndGetIterator(stream);

    assertNotNull(iterator);
    assertTrue(iterator.hasNext());
    assertEquals("data1", iterator.next());
  }

  @Test
  void validateAndGetIterator_withEmptyStream_throwsException() {
    Stream<String> emptyStream = Stream.empty();

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateAndGetIterator(emptyStream)
    );

    assertEquals(ErrorCode.EMPTY_DATA, exception.getErrorCode());
  }

  @Test
  void validateAndGetIterator_withConsumedStream_throwsStreamAlreadyConsumedException() {
    List<String> list = Arrays.asList("data1", "data2");
    Stream<String> stream = list.stream();
    stream.count();

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateAndGetIterator(stream)
    );

    assertEquals(ErrorCode.STREAM_ALREADY_CONSUMED, exception.getErrorCode());
  }

  @Test
  void validateAndGetIterator_withSingleElementStream_returnsIterator() {
    Stream<String> stream = Stream.of("single");

    Iterator<String> iterator = validator.validateAndGetIterator(stream);

    assertTrue(iterator.hasNext());
    assertEquals("single", iterator.next());
  }

  @Test
  void validateDuplicateSheetOrders_withNoDuplicates_doesNotThrow() {
    List<Map.Entry<String, SheetGroupInfo>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("Sheet1", new SheetGroupInfo(1)));
    entries.add(new AbstractMap.SimpleEntry<>("Sheet2", new SheetGroupInfo(2)));
    entries.add(new AbstractMap.SimpleEntry<>("Sheet3", new SheetGroupInfo(3)));

    validator.validateDuplicateSheetOrders(entries);
  }

  @Test
  void validateDuplicateSheetOrders_withDuplicateOrders_throwsException() {
    List<Map.Entry<String, SheetGroupInfo>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("Sheet1", new SheetGroupInfo(1)));
    entries.add(new AbstractMap.SimpleEntry<>("Sheet2", new SheetGroupInfo(2)));
    entries.add(new AbstractMap.SimpleEntry<>("Sheet3", new SheetGroupInfo(1)));

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateDuplicateSheetOrders(entries)
    );

    assertEquals(ErrorCode.DUPLICATE_SHEET_ORDER, exception.getErrorCode());
    assertTrue(exception.getMessage().contains("1"));
    assertTrue(exception.getMessage().contains("Sheet3"));
  }

  @Test
  void validateDuplicateSheetOrders_withEmptyList_doesNotThrow() {
    List<Map.Entry<String, SheetGroupInfo>> emptyList = Collections.emptyList();
    validator.validateDuplicateSheetOrders(emptyList);
  }

  @Test
  void validateDuplicateSheetOrders_withSingleEntry_doesNotThrow() {
    List<Map.Entry<String, SheetGroupInfo>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("Sheet1", new SheetGroupInfo(1)));

    validator.validateDuplicateSheetOrders(entries);
  }

  @Test
  void validateDuplicateSheetOrders_withMultipleDuplicates_throwsOnFirstDuplicate() {
    List<Map.Entry<String, SheetGroupInfo>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("Sheet1", new SheetGroupInfo(1)));
    entries.add(new AbstractMap.SimpleEntry<>("Sheet2", new SheetGroupInfo(1)));
    entries.add(new AbstractMap.SimpleEntry<>("Sheet3", new SheetGroupInfo(2)));
    entries.add(new AbstractMap.SimpleEntry<>("Sheet4", new SheetGroupInfo(2)));

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateDuplicateSheetOrders(entries)
    );

    assertEquals(ErrorCode.DUPLICATE_SHEET_ORDER, exception.getErrorCode());
  }

  @Test
  void validateDuplicateSheetOrders_withNegativeOrders_checksForDuplicates() {
    List<Map.Entry<String, SheetGroupInfo>> entries = new ArrayList<>();
    entries.add(new AbstractMap.SimpleEntry<>("Sheet1", new SheetGroupInfo(-1)));
    entries.add(new AbstractMap.SimpleEntry<>("Sheet2", new SheetGroupInfo(-1)));

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateDuplicateSheetOrders(entries)
    );

    assertEquals(ErrorCode.DUPLICATE_SHEET_ORDER, exception.getErrorCode());
  }
}
