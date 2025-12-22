package io.github.takoeats.excelannotator.internal.writer.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataStreamAdapterTest {

  private DataStreamAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new DataStreamAdapter();
  }

  @Test
  void prependToIterator_withMultipleElements_returnsCorrectOrder() {
    List<String> list = new ArrayList<>(Arrays.asList("second", "third"));
    Iterator<String> iterator = list.iterator();

    Iterator<String> result = adapter.prependToIterator("first", iterator);

    assertTrue(result.hasNext());
    assertEquals("first", result.next());
    assertTrue(result.hasNext());
    assertEquals("second", result.next());
    assertTrue(result.hasNext());
    assertEquals("third", result.next());
    assertFalse(result.hasNext());
  }

  @Test
  void prependToIterator_withEmptyIterator_returnsSingleElement() {
    Iterator<String> emptyIterator = Collections.emptyIterator();

    Iterator<String> result = adapter.prependToIterator("only", emptyIterator);

    assertTrue(result.hasNext());
    assertEquals("only", result.next());
    assertFalse(result.hasNext());
  }

  @Test
  void prependToIterator_hasNextCalledMultipleTimes_remainsConsistent() {
    List<String> list = new ArrayList<>(Arrays.asList("second"));
    Iterator<String> iterator = list.iterator();

    Iterator<String> result = adapter.prependToIterator("first", iterator);

    assertTrue(result.hasNext());
    assertTrue(result.hasNext());
    assertTrue(result.hasNext());
    assertEquals("first", result.next());
  }

  @Test
  void prependToIterator_nextCalledWithoutHasNext_worksCorrectly() {
    List<String> list = new ArrayList<>(Arrays.asList("second"));
    Iterator<String> iterator = list.iterator();

    Iterator<String> result = adapter.prependToIterator("first", iterator);

    assertEquals("first", result.next());
    assertEquals("second", result.next());
  }

  @Test
  void prependToIterator_nextCalledWhenExhausted_throwsNoSuchElementException() {
    Iterator<String> emptyIterator = Collections.emptyIterator();

    Iterator<String> result = adapter.prependToIterator("only", emptyIterator);

    assertEquals("only", result.next());
    assertThrows(NoSuchElementException.class, result::next);
  }

  @Test
  void prependToIterator_withNullFirstItem_worksCorrectly() {
    List<String> list = new ArrayList<>(Arrays.asList("second"));
    Iterator<String> iterator = list.iterator();

    Iterator<String> result = adapter.prependToIterator(null, iterator);

    assertTrue(result.hasNext());
    assertEquals(null, result.next());
    assertEquals("second", result.next());
    assertFalse(result.hasNext());
  }

  @Test
  void prependToIterator_withSingleElementIterator_returnsTwo() {
    List<String> list = new ArrayList<>(Arrays.asList("second"));
    Iterator<String> iterator = list.iterator();

    Iterator<String> result = adapter.prependToIterator("first", iterator);

    List<String> collected = new ArrayList<>();
    result.forEachRemaining(collected::add);

    assertEquals(Arrays.asList("first", "second"), collected);
  }

  @Test
  void prependToIterator_usedWithDifferentTypes_worksCorrectly() {
    List<Integer> list = new ArrayList<>(Arrays.asList(2, 3, 4));
    Iterator<Integer> iterator = list.iterator();

    Iterator<Integer> result = adapter.prependToIterator(1, iterator);

    assertEquals(1, result.next());
    assertEquals(2, result.next());
    assertEquals(3, result.next());
    assertEquals(4, result.next());
    assertFalse(result.hasNext());
  }

  @Test
  void prependToIterator_firstItemReturnedOnlyOnce() {
    List<String> list = new ArrayList<>(Arrays.asList("second"));
    Iterator<String> iterator = list.iterator();

    Iterator<String> result = adapter.prependToIterator("first", iterator);

    assertEquals("first", result.next());
    assertEquals("second", result.next());
    assertFalse(result.hasNext());
  }
}
