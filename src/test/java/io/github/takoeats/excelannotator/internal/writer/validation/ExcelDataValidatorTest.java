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

  @Test
  void validateAndGetIterator_withIllegalStateExceptionWithNullMessage_throwsWorkbookCreationFailed() {
    Stream<String> faultyStream = new Stream<String>() {
      @Override
      public Iterator<String> iterator() {
        throw new IllegalStateException();
      }

      @Override
      public java.util.Spliterator<String> spliterator() {
        return null;
      }

      @Override
      public boolean isParallel() {
        return false;
      }

      @Override
      public Stream<String> sequential() {
        return null;
      }

      @Override
      public Stream<String> parallel() {
        return null;
      }

      @Override
      public Stream<String> unordered() {
        return null;
      }

      @Override
      public Stream<String> onClose(Runnable closeHandler) {
        return null;
      }

      @Override
      public void close() {
      }

      @Override
      public Stream<String> filter(java.util.function.Predicate<? super String> predicate) {
        return null;
      }

      @Override
      public <R> Stream<R> map(java.util.function.Function<? super String, ? extends R> mapper) {
        return null;
      }

      @Override
      public java.util.stream.IntStream mapToInt(
          java.util.function.ToIntFunction<? super String> mapper) {
        return null;
      }

      @Override
      public java.util.stream.LongStream mapToLong(
          java.util.function.ToLongFunction<? super String> mapper) {
        return null;
      }

      @Override
      public java.util.stream.DoubleStream mapToDouble(
          java.util.function.ToDoubleFunction<? super String> mapper) {
        return null;
      }

      @Override
      public <R> Stream<R> flatMap(
          java.util.function.Function<? super String, ? extends Stream<? extends R>> mapper) {
        return null;
      }

      @Override
      public java.util.stream.IntStream flatMapToInt(
          java.util.function.Function<? super String, ? extends java.util.stream.IntStream> mapper) {
        return null;
      }

      @Override
      public java.util.stream.LongStream flatMapToLong(
          java.util.function.Function<? super String, ? extends java.util.stream.LongStream> mapper) {
        return null;
      }

      @Override
      public java.util.stream.DoubleStream flatMapToDouble(
          java.util.function.Function<? super String, ? extends java.util.stream.DoubleStream> mapper) {
        return null;
      }

      @Override
      public Stream<String> distinct() {
        return null;
      }

      @Override
      public Stream<String> sorted() {
        return null;
      }

      @Override
      public Stream<String> sorted(java.util.Comparator<? super String> comparator) {
        return null;
      }

      @Override
      public Stream<String> peek(java.util.function.Consumer<? super String> action) {
        return null;
      }

      @Override
      public Stream<String> limit(long maxSize) {
        return null;
      }

      @Override
      public Stream<String> skip(long n) {
        return null;
      }

      @Override
      public void forEach(java.util.function.Consumer<? super String> action) {
      }

      @Override
      public void forEachOrdered(java.util.function.Consumer<? super String> action) {
      }

      @Override
      public Object[] toArray() {
        return new Object[0];
      }

      @Override
      public <A> A[] toArray(java.util.function.IntFunction<A[]> generator) {
        return null;
      }

      @Override
      public String reduce(String identity,
          java.util.function.BinaryOperator<String> accumulator) {
        return null;
      }

      @Override
      public java.util.Optional<String> reduce(
          java.util.function.BinaryOperator<String> accumulator) {
        return null;
      }

      @Override
      public <U> U reduce(U identity,
          java.util.function.BiFunction<U, ? super String, U> accumulator,
          java.util.function.BinaryOperator<U> combiner) {
        return null;
      }

      @Override
      public <R> R collect(java.util.function.Supplier<R> supplier,
          java.util.function.BiConsumer<R, ? super String> accumulator,
          java.util.function.BiConsumer<R, R> combiner) {
        return null;
      }

      @Override
      public <R, A> R collect(java.util.stream.Collector<? super String, A, R> collector) {
        return null;
      }

      @Override
      public java.util.Optional<String> min(java.util.Comparator<? super String> comparator) {
        return null;
      }

      @Override
      public java.util.Optional<String> max(java.util.Comparator<? super String> comparator) {
        return null;
      }

      @Override
      public long count() {
        return 0;
      }

      @Override
      public boolean anyMatch(java.util.function.Predicate<? super String> predicate) {
        return false;
      }

      @Override
      public boolean allMatch(java.util.function.Predicate<? super String> predicate) {
        return false;
      }

      @Override
      public boolean noneMatch(java.util.function.Predicate<? super String> predicate) {
        return false;
      }

      @Override
      public java.util.Optional<String> findFirst() {
        return null;
      }

      @Override
      public java.util.Optional<String> findAny() {
        return null;
      }
    };

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateAndGetIterator(faultyStream)
    );

    assertEquals(ErrorCode.WORKBOOK_CREATION_FAILED, exception.getErrorCode());
  }

  @Test
  void validateAndGetIterator_withIllegalStateExceptionWithOtherMessage_throwsWorkbookCreationFailed() {
    Stream<String> faultyStream = new Stream<String>() {
      @Override
      public Iterator<String> iterator() {
        throw new IllegalStateException("Some other error message");
      }

      @Override
      public java.util.Spliterator<String> spliterator() {
        return null;
      }

      @Override
      public boolean isParallel() {
        return false;
      }

      @Override
      public Stream<String> sequential() {
        return null;
      }

      @Override
      public Stream<String> parallel() {
        return null;
      }

      @Override
      public Stream<String> unordered() {
        return null;
      }

      @Override
      public Stream<String> onClose(Runnable closeHandler) {
        return null;
      }

      @Override
      public void close() {
      }

      @Override
      public Stream<String> filter(java.util.function.Predicate<? super String> predicate) {
        return null;
      }

      @Override
      public <R> Stream<R> map(java.util.function.Function<? super String, ? extends R> mapper) {
        return null;
      }

      @Override
      public java.util.stream.IntStream mapToInt(
          java.util.function.ToIntFunction<? super String> mapper) {
        return null;
      }

      @Override
      public java.util.stream.LongStream mapToLong(
          java.util.function.ToLongFunction<? super String> mapper) {
        return null;
      }

      @Override
      public java.util.stream.DoubleStream mapToDouble(
          java.util.function.ToDoubleFunction<? super String> mapper) {
        return null;
      }

      @Override
      public <R> Stream<R> flatMap(
          java.util.function.Function<? super String, ? extends Stream<? extends R>> mapper) {
        return null;
      }

      @Override
      public java.util.stream.IntStream flatMapToInt(
          java.util.function.Function<? super String, ? extends java.util.stream.IntStream> mapper) {
        return null;
      }

      @Override
      public java.util.stream.LongStream flatMapToLong(
          java.util.function.Function<? super String, ? extends java.util.stream.LongStream> mapper) {
        return null;
      }

      @Override
      public java.util.stream.DoubleStream flatMapToDouble(
          java.util.function.Function<? super String, ? extends java.util.stream.DoubleStream> mapper) {
        return null;
      }

      @Override
      public Stream<String> distinct() {
        return null;
      }

      @Override
      public Stream<String> sorted() {
        return null;
      }

      @Override
      public Stream<String> sorted(java.util.Comparator<? super String> comparator) {
        return null;
      }

      @Override
      public Stream<String> peek(java.util.function.Consumer<? super String> action) {
        return null;
      }

      @Override
      public Stream<String> limit(long maxSize) {
        return null;
      }

      @Override
      public Stream<String> skip(long n) {
        return null;
      }

      @Override
      public void forEach(java.util.function.Consumer<? super String> action) {
      }

      @Override
      public void forEachOrdered(java.util.function.Consumer<? super String> action) {
      }

      @Override
      public Object[] toArray() {
        return new Object[0];
      }

      @Override
      public <A> A[] toArray(java.util.function.IntFunction<A[]> generator) {
        return null;
      }

      @Override
      public String reduce(String identity,
          java.util.function.BinaryOperator<String> accumulator) {
        return null;
      }

      @Override
      public java.util.Optional<String> reduce(
          java.util.function.BinaryOperator<String> accumulator) {
        return null;
      }

      @Override
      public <U> U reduce(U identity,
          java.util.function.BiFunction<U, ? super String, U> accumulator,
          java.util.function.BinaryOperator<U> combiner) {
        return null;
      }

      @Override
      public <R> R collect(java.util.function.Supplier<R> supplier,
          java.util.function.BiConsumer<R, ? super String> accumulator,
          java.util.function.BiConsumer<R, R> combiner) {
        return null;
      }

      @Override
      public <R, A> R collect(java.util.stream.Collector<? super String, A, R> collector) {
        return null;
      }

      @Override
      public java.util.Optional<String> min(java.util.Comparator<? super String> comparator) {
        return null;
      }

      @Override
      public java.util.Optional<String> max(java.util.Comparator<? super String> comparator) {
        return null;
      }

      @Override
      public long count() {
        return 0;
      }

      @Override
      public boolean anyMatch(java.util.function.Predicate<? super String> predicate) {
        return false;
      }

      @Override
      public boolean allMatch(java.util.function.Predicate<? super String> predicate) {
        return false;
      }

      @Override
      public boolean noneMatch(java.util.function.Predicate<? super String> predicate) {
        return false;
      }

      @Override
      public java.util.Optional<String> findFirst() {
        return null;
      }

      @Override
      public java.util.Optional<String> findAny() {
        return null;
      }
    };

    ExcelExporterException exception = assertThrows(
        ExcelExporterException.class,
        () -> validator.validateAndGetIterator(faultyStream)
    );

    assertEquals(ErrorCode.WORKBOOK_CREATION_FAILED, exception.getErrorCode());
  }
}
