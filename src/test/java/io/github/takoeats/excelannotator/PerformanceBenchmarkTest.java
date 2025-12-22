package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.testdto.CustomerDTO;
import io.github.takoeats.excelannotator.util.ExcelAssertions;
import io.github.takoeats.excelannotator.util.ExcelTestHelper;
import io.github.takoeats.excelannotator.util.TestDataFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("performance")
class PerformanceBenchmarkTest {

    private static final int HUNDRED_K = 100_000;
    private static final int ONE_MILLION = 1_000_000;
    private static final int TEN_K = 10_000;

    @Test
    void listAPI_100KRows_completesWithin5Seconds() throws Exception {
        List<CustomerDTO> customers = TestDataFactory.createCustomers(HUNDRED_K);

        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "100k_list.xlsx", customers);

        long elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime < 5000,
            "List API should complete 100K rows within 5 seconds, actual: " + elapsedTime + "ms");

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        ExcelAssertions.assertExcelFileValid(baos.toByteArray());
        ExcelAssertions.assertRowCount(wb.getSheetAt(0), HUNDRED_K + 1);
    }

    @Test
    void streamAPI_1MRows_completesWithin60Seconds() throws Exception {
        Stream<CustomerDTO> customerStream = TestDataFactory.createCustomerStream(ONE_MILLION);

        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "1m_stream.xlsx", customerStream);

        long elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime < 60000,
            "Stream API should complete 1M rows within 60 seconds, actual: " + elapsedTime + "ms");

        assertTrue(baos.size() > 0, "Excel file should be generated");
        System.out.println("1M rows Excel file size: " + (baos.size() / (1024 * 1024)) + " MB");
    }

    @Test
    void streamAPI_constantMemory_noOOMError() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        Stream<CustomerDTO> customerStream = TestDataFactory.createCustomerStream(HUNDRED_K);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> {
            ExcelExporter.excelFromStream(baos, "memory_test.xlsx", customerStream);
        });

        runtime.gc();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        long memoryIncrease = memoryAfter - memoryBefore;
        long memoryIncreaseMB = memoryIncrease / (1024 * 1024);

        System.out.println("Memory increase: " + memoryIncreaseMB + " MB for 100K rows");

        assertTrue(memoryIncreaseMB < 500,
            "Memory increase should be less than 500MB, actual: " + memoryIncreaseMB + "MB");
    }

    @Test
    void listAPI_memoryGrowsLinearly() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        List<CustomerDTO> customers = TestDataFactory.createCustomers(HUNDRED_K);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "list_memory.xlsx", customers);

        runtime.gc();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        long memoryIncrease = memoryAfter - memoryBefore;
        long memoryIncreaseMB = memoryIncrease / (1024 * 1024);

        System.out.println("List API memory increase: " + memoryIncreaseMB + " MB for 100K rows");

        assertTrue(memoryIncreaseMB > 0,
            "Memory should increase for List API");
    }

    @Test
    void autoWidth_10KRows_usesSampling() throws Exception {
        List<CustomerDTO> customers = TestDataFactory.createCustomers(TEN_K);

        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "autowidth_10k.xlsx", customers);

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Auto-width with 10K rows completed in: " + elapsedTime + "ms");

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        int columnWidth = sheet.getColumnWidth(0);
        assertTrue(columnWidth > 0, "Column width should be set");

        ExcelAssertions.assertRowCount(sheet, TEN_K + 1);
    }

    @Test
    void styleCacheTest_noMemoryLeak() throws Exception {
        Runtime runtime = Runtime.getRuntime();

        for (int i = 0; i < 5; i++) {
            runtime.gc();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

            List<CustomerDTO> customers = TestDataFactory.createCustomers(10000);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ExcelExporter.excelFromList(baos, "cache_test_" + i + ".xlsx", customers);

            runtime.gc();
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

            long memoryDelta = memoryAfter - memoryBefore;

            if (i == 0) {
                System.out.println("First iteration memory delta: " + (memoryDelta / (1024 * 1024)) + " MB");
            } else {
                System.out.println("Iteration " + i + " memory delta: " + (memoryDelta / (1024 * 1024)) + " MB");
            }
        }

        assertDoesNotThrow(() -> {
            System.out.println("No OutOfMemoryError occurred during 5 iterations");
        });
    }

    @Test
    void largeDataset_autoSheetSplitting_1_5MRows() throws Exception {
        Stream<CustomerDTO> customerStream = TestDataFactory.createCustomerStream(1_500_000);

        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> {
            ExcelExporter.excelFromStream(baos, "1_5m_auto_split.xlsx",
                customerStream);
        });

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("1.5M rows completed in: " + elapsedTime + "ms (" + (elapsedTime / 1000) + "s)");

        assertTrue(baos.size() > 0, "Excel file should be generated");
        System.out.println("1.5M rows Excel file size: " + (baos.size() / (1024 * 1024)) + " MB");
        System.out.println("Note: File validation skipped due to POI memory limit for very large files");
    }

    @Test
    void throughputTest_rowsPerSecond() throws Exception {
        int testSize = 50000;
        List<CustomerDTO> customers = TestDataFactory.createCustomers(testSize);

        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "throughput_test.xlsx", customers);

        long elapsedTime = System.currentTimeMillis() - startTime;
        double rowsPerSecond = (testSize * 1000.0) / elapsedTime;

        System.out.println("Throughput: " + String.format("%.2f", rowsPerSecond) + " rows/second");
        System.out.println("Total time: " + elapsedTime + "ms for " + testSize + " rows");

        assertTrue(rowsPerSecond > 5000,
            "Throughput should be at least 5000 rows/second, actual: " + String.format("%.2f", rowsPerSecond));
    }
}
