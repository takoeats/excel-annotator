package io.github.takoeats.excelannotator.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExcelMetadataCache 테스트")
class ExcelMetadataCacheTest {

  @BeforeEach
  void setUp() {
    ExcelMetadataCache.clearCache();
  }

  @AfterEach
  void tearDown() {
    ExcelMetadataCache.clearCache();
  }

  @ExcelSheet("TestSheet")
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TestDTO {

    @ExcelColumn(header = "Name", order = 1)
    private String name;

    @ExcelColumn(header = "Age", order = 2)
    private int age;
  }

  @ExcelSheet("AnotherSheet")
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AnotherDTO {

    @ExcelColumn(header = "Value", order = 1)
    private String value;
  }

  @Test
  @DisplayName("첫 호출 시 메타데이터 생성 및 캐싱")
  void getMetadata_firstCall_createsAndCachesMetadata() {
    assertEquals(0, ExcelMetadataCache.getCacheSize());

    ExcelMetadata<TestDTO> metadata = ExcelMetadataCache.getMetadata(TestDTO.class);

    assertNotNull(metadata);
    assertEquals(1, ExcelMetadataCache.getCacheSize());
    assertEquals("TestSheet", metadata.getSheetName());
    assertEquals(2, metadata.getHeaders().size());
  }

  @Test
  @DisplayName("같은 클래스로 두 번 호출 시 캐시된 인스턴스 반환")
  void getMetadata_sameClassCalledTwice_returnsCachedInstance() {
    ExcelMetadata<TestDTO> metadata1 = ExcelMetadataCache.getMetadata(TestDTO.class);
    ExcelMetadata<TestDTO> metadata2 = ExcelMetadataCache.getMetadata(TestDTO.class);

    assertSame(metadata1, metadata2);
    assertEquals(1, ExcelMetadataCache.getCacheSize());
  }

  @Test
  @DisplayName("다른 클래스는 별도 캐시 엔트리 생성")
  void getMetadata_differentClasses_createsSeparateCacheEntries() {
    ExcelMetadata<TestDTO> metadata1 = ExcelMetadataCache.getMetadata(TestDTO.class);
    ExcelMetadata<AnotherDTO> metadata2 = ExcelMetadataCache.getMetadata(AnotherDTO.class);

    assertNotNull(metadata1);
    assertNotNull(metadata2);
    assertEquals(2, ExcelMetadataCache.getCacheSize());
    assertEquals("TestSheet", metadata1.getSheetName());
    assertEquals("AnotherSheet", metadata2.getSheetName());
  }

  @Test
  @DisplayName("clearCache 호출 시 모든 캐시 제거")
  void clearCache_removesAllCachedMetadata() {
    ExcelMetadataCache.getMetadata(TestDTO.class);
    ExcelMetadataCache.getMetadata(AnotherDTO.class);
    assertEquals(2, ExcelMetadataCache.getCacheSize());

    ExcelMetadataCache.clearCache();

    assertEquals(0, ExcelMetadataCache.getCacheSize());
  }

  @Test
  @DisplayName("clearCache 후 재호출 시 새 인스턴스 생성")
  void clearCache_afterClear_createsNewInstance() {
    ExcelMetadata<TestDTO> metadata1 = ExcelMetadataCache.getMetadata(TestDTO.class);

    ExcelMetadataCache.clearCache();

    ExcelMetadata<TestDTO> metadata2 = ExcelMetadataCache.getMetadata(TestDTO.class);

    assertNotNull(metadata1);
    assertNotNull(metadata2);
    assertEquals(1, ExcelMetadataCache.getCacheSize());
  }

  @Test
  @DisplayName("getCacheSize - 빈 캐시는 0 반환")
  void getCacheSize_emptyCache_returnsZero() {
    assertEquals(0, ExcelMetadataCache.getCacheSize());
  }

  @Test
  @DisplayName("getCacheSize - 캐시된 항목 개수 정확히 반환")
  void getCacheSize_withCachedItems_returnsCorrectCount() {
    ExcelMetadataCache.getMetadata(TestDTO.class);
    assertEquals(1, ExcelMetadataCache.getCacheSize());

    ExcelMetadataCache.getMetadata(AnotherDTO.class);
    assertEquals(2, ExcelMetadataCache.getCacheSize());

    ExcelMetadataCache.getMetadata(TestDTO.class);
    assertEquals(2, ExcelMetadataCache.getCacheSize());
  }

  @Test
  @DisplayName("동시성 테스트 - 여러 스레드가 동시에 같은 클래스 요청")
  void concurrentAccess_sameClass_threadSafe() throws InterruptedException {
    int threadCount = 20;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);
    List<ExcelMetadata<TestDTO>> results = new ArrayList<>();

    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        try {
          startLatch.await();
          ExcelMetadata<TestDTO> metadata = ExcelMetadataCache.getMetadata(TestDTO.class);
          synchronized (results) {
            results.add(metadata);
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } finally {
          doneLatch.countDown();
        }
      });
    }

    startLatch.countDown();
    doneLatch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    assertEquals(threadCount, results.size());
    assertEquals(1, ExcelMetadataCache.getCacheSize());

    ExcelMetadata<TestDTO> firstMetadata = results.get(0);
    for (ExcelMetadata<TestDTO> metadata : results) {
      assertSame(firstMetadata, metadata);
    }
  }

  @Test
  @DisplayName("동시성 테스트 - 여러 스레드가 다른 클래스 요청")
  void concurrentAccess_differentClasses_threadSafe() throws InterruptedException {
    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      final int index = i;
      executor.submit(() -> {
        try {
          startLatch.await();
          if (index % 2 == 0) {
            ExcelMetadataCache.getMetadata(TestDTO.class);
          } else {
            ExcelMetadataCache.getMetadata(AnotherDTO.class);
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } finally {
          doneLatch.countDown();
        }
      });
    }

    startLatch.countDown();
    doneLatch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    assertEquals(2, ExcelMetadataCache.getCacheSize());
  }

  @Test
  @DisplayName("동시성 테스트 - clearCache 동시 호출")
  void concurrentAccess_clearCache_threadSafe() throws InterruptedException {
    ExcelMetadataCache.getMetadata(TestDTO.class);
    ExcelMetadataCache.getMetadata(AnotherDTO.class);

    int threadCount = 5;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        try {
          startLatch.await();
          ExcelMetadataCache.clearCache();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } finally {
          doneLatch.countDown();
        }
      });
    }

    startLatch.countDown();
    doneLatch.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    assertEquals(0, ExcelMetadataCache.getCacheSize());
  }

  @Test
  @DisplayName("반복 호출 - 캐시 히트율 확인")
  void repeatedCalls_utilizesCache() {
    for (int i = 0; i < 100; i++) {
      ExcelMetadataCache.getMetadata(TestDTO.class);
    }

    assertEquals(1, ExcelMetadataCache.getCacheSize());

    ExcelMetadata<TestDTO> metadata = ExcelMetadataCache.getMetadata(TestDTO.class);
    assertNotNull(metadata);
  }

  @Test
  @DisplayName("메타데이터 내용 검증 - 헤더")
  void getMetadata_verifyHeaders() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataCache.getMetadata(TestDTO.class);

    List<String> headers = metadata.getHeaders();
    assertEquals(2, headers.size());
    assertEquals("Name", headers.get(0));
    assertEquals("Age", headers.get(1));
  }

  @Test
  @DisplayName("메타데이터 내용 검증 - 시트 이름")
  void getMetadata_verifySheetName() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataCache.getMetadata(TestDTO.class);

    assertEquals("TestSheet", metadata.getSheetName());
  }

  @Test
  @DisplayName("메타데이터 내용 검증 - Extractor 개수")
  void getMetadata_verifyExtractorCount() {
    ExcelMetadata<TestDTO> metadata = ExcelMetadataCache.getMetadata(TestDTO.class);

    assertEquals(2, metadata.getExtractors().size());
  }

  @Test
  @DisplayName("유틸리티 클래스 - 인스턴스화 불가")
  void utilityClass_cannotBeInstantiated() {
    try {
      java.lang.reflect.Constructor<ExcelMetadataCache> constructor =
          ExcelMetadataCache.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
      org.junit.jupiter.api.Assertions.fail("Should have thrown AssertionError");
    } catch (java.lang.reflect.InvocationTargetException e) {
      org.junit.jupiter.api.Assertions.assertTrue(e.getCause() instanceof AssertionError);
      org.junit.jupiter.api.Assertions.assertTrue(
          e.getCause().getMessage().contains("Utility class cannot be instantiated")
      );
    } catch (Exception e) {
      org.junit.jupiter.api.Assertions.fail("Unexpected exception: " + e.getClass().getName());
    }
  }
}
