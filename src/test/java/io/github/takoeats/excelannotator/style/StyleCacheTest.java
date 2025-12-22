package io.github.takoeats.excelannotator.style;

import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultColumnStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultHeaderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StyleCacheTest {

    @Test
    void getStyleInstance_returnsSameInstanceForSameClass() {
        CustomExcelCellStyle instance1 = StyleCache.getStyleInstance(DefaultColumnStyle.class);
        CustomExcelCellStyle instance2 = StyleCache.getStyleInstance(DefaultColumnStyle.class);

        assertSame(instance1, instance2);
    }

    @Test
    void getStyleInstance_returnsDifferentInstancesForDifferentClasses() {
        CustomExcelCellStyle columnStyle = StyleCache.getStyleInstance(DefaultColumnStyle.class);
        CustomExcelCellStyle headerStyle = StyleCache.getStyleInstance(DefaultHeaderStyle.class);

        assertNotSame(columnStyle, headerStyle);
    }

    @Test
    void getStyleInstance_throwsExceptionForNonPublicConstructor() {
        assertThrows(ExcelExporterException.class,
                () -> StyleCache.getStyleInstance(StyleWithPrivateConstructor.class));
    }

    @Test
    void getStyleInstance_throwsExceptionForClassWithoutNoArgConstructor() {
        assertThrows(ExcelExporterException.class,
                () -> StyleCache.getStyleInstance(StyleWithoutNoArgConstructor.class));
    }

    @Test
    void createPOIStyle_createsNewCellStyleEachTime() throws Exception {
        try (Workbook workbook = new SXSSFWorkbook()) {
            CellStyle style1 = StyleCache.createPOIStyle(workbook, DefaultColumnStyle.class);
            CellStyle style2 = StyleCache.createPOIStyle(workbook, DefaultColumnStyle.class);

            assertNotSame(style1, style2);
        }
    }

    @Test
    void createPOIStyle_appliesStyleConfigurationCorrectly() throws Exception {
        try (Workbook workbook = new SXSSFWorkbook()) {
            CellStyle cellStyle = StyleCache.createPOIStyle(workbook, DefaultHeaderStyle.class);

            assertNotNull(cellStyle);
            assertTrue(cellStyle.getFontIndex() >= 0);
        }
    }

    @Test
    void getStyleInstance_threadSafety() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        CustomExcelCellStyle[] results = new CustomExcelCellStyle[threadCount];

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            threads[i] = new Thread(() -> results[index] = StyleCache.getStyleInstance(DefaultColumnStyle.class));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        CustomExcelCellStyle firstInstance = results[0];
        for (int i = 1; i < threadCount; i++) {
            assertSame(firstInstance, results[i]);
        }
    }

  @SuppressWarnings({"java:S1068", "java:S1186"})
    public static class StyleWithPrivateConstructor extends CustomExcelCellStyle {
        private StyleWithPrivateConstructor() {
        }

        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

  @SuppressWarnings({"java:S1068", "java:S1186"})
    public static class StyleWithoutNoArgConstructor extends CustomExcelCellStyle {
        private final String name;

        public StyleWithoutNoArgConstructor(String name) {
            this.name = name;
        }

        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }
}
