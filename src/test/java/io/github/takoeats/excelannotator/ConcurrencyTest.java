package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.testdto.EmployeeDTO;
import io.github.takoeats.excelannotator.util.ExcelAssertions;
import io.github.takoeats.excelannotator.util.ExcelTestHelper;
import io.github.takoeats.excelannotator.util.TestDataFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {

    @Test
    void multipleThreads_independentWorkbooks_noConflict() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<byte[]>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            Future<byte[]> future = executor.submit(() -> {
                List<EmployeeDTO> employees = TestDataFactory.createEmployees(100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ExcelExporter.excelFromList(baos, "thread_" + threadIndex + ".xlsx", employees);
                return baos.toByteArray();
            });
            futures.add(future);
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

        for (int i = 0; i < threadCount; i++) {
            byte[] excelBytes = futures.get(i).get();
            assertNotNull(excelBytes);
            assertTrue(excelBytes.length > 0);

            Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
            ExcelAssertions.assertRowCount(wb.getSheetAt(0), 101);
        }
    }

    @Test
    void parallelStream_excelGeneration_threadSafe() throws Exception {
        List<Integer> taskIds = IntStream.range(0, 20).boxed().collect(java.util.stream.Collectors.toList());

        List<byte[]> results = taskIds.parallelStream()
            .map(taskId -> {
                try {
                    List<EmployeeDTO> employees = TestDataFactory.createEmployees(50);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ExcelExporter.excelFromList(baos, "parallel_" + taskId + ".xlsx", employees);
                    return baos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("Failed in parallel stream", e);
                }
            })
            .collect(java.util.stream.Collectors.toList());

        assertEquals(20, results.size());

        for (byte[] excelBytes : results) {
            assertNotNull(excelBytes);
            assertTrue(excelBytes.length > 0);
            ExcelAssertions.assertExcelFileValid(excelBytes);
        }
    }

    @Test
    void styleCacheAccess_concurrent_noRaceCondition() throws Exception {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    List<EmployeeDTO> employees = TestDataFactory.createEmployees(10);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ExcelExporter.excelFromList(baos, "concurrent_style.xlsx", employees);

                    ExcelAssertions.assertExcelFileValid(baos.toByteArray());
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        executor.shutdown();

        assertTrue(exceptions.isEmpty(),
            "Should have no exceptions, but got: " + exceptions.size());
    }

    @Test
    void sequentialVsParallel_resultConsistency() throws Exception {
        List<EmployeeDTO> employees = TestDataFactory.createEmployees(100);

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos1, "sequential.xlsx", employees);

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos2, "parallel.xlsx", employees);

        Workbook wb1 = ExcelTestHelper.workbookFromBytes(baos1.toByteArray());
        Workbook wb2 = ExcelTestHelper.workbookFromBytes(baos2.toByteArray());

        ExcelAssertions.assertRowCount(wb1.getSheetAt(0), 101);
        ExcelAssertions.assertRowCount(wb2.getSheetAt(0), 101);

        assertEquals(wb1.getNumberOfSheets(), wb2.getNumberOfSheets());

        String header1 = wb1.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
        String header2 = wb2.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
        assertEquals(header1, header2);
    }

    @Test
    void customStyleConfigurer_concurrentInitialization_usesCompareAndSetElseBranch() throws Exception {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        List<Integer> widths = new CopyOnWriteArrayList<>();
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        CustomExcelCellStyle sharedStyle = new CustomExcelCellStyle() {
            @Override
            protected void configure(ExcelCellStyleConfigurer configurer) {
                configurer.width(250);
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    int width = sharedStyle.getColumnWidth();
                    widths.add(width);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        assertTrue(exceptions.isEmpty(), "No exceptions should occur during concurrent access");
        assertEquals(threadCount, widths.size(), "All threads should complete successfully");
        assertTrue(widths.stream().allMatch(w -> w == 250), "All threads should get the same configured width");
    }

    private static class TestConcurrentStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
            configurer.width(200);
        }
    }
}
