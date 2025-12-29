package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.internal.writer.ExcelWriter;
import io.github.takoeats.excelannotator.testdto.MultiSheetColumnDTO;
import io.github.takoeats.excelannotator.testdto.PersonDTO;
import io.github.takoeats.excelannotator.testdto.SalaryDTO;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExcelWriterTest {

    @Test
    void write_withList_createsSheetHeaderAndData() throws IOException {
        ExcelWriter writer = new ExcelWriter();
        List<PersonDTO> list = Arrays.asList(
                new PersonDTO("Alice", 30, new BigDecimal("1000.25")),
                new PersonDTO("Bob", 41, new BigDecimal("200.00"))
        );

        try (SXSSFWorkbook wb = writer.write(list)) {
            assertNotNull(wb);
            Sheet sheet = wb.getSheetAt(0);
            assertEquals("Persons", sheet.getSheetName());

            DataFormatter fmt = new DataFormatter();
            // Header row
            Row header = sheet.getRow(0);
            assertEquals("Name", fmt.formatCellValue(header.getCell(0)));
            assertEquals("Age", fmt.formatCellValue(header.getCell(1)));
            assertEquals("Salary", fmt.formatCellValue(header.getCell(2)));

            // Data rows
            Row r1 = sheet.getRow(1);
            Row r2 = sheet.getRow(2);
            assertEquals("Alice", fmt.formatCellValue(r1.getCell(0)));
            assertEquals("30", fmt.formatCellValue(r1.getCell(1)));
            assertEquals("1,000.25", fmt.formatCellValue(r1.getCell(2)));

            assertEquals("Bob", fmt.formatCellValue(r2.getCell(0)));
            assertEquals("41", fmt.formatCellValue(r2.getCell(1)));
            assertEquals("200.00", fmt.formatCellValue(r2.getCell(2)));
        }
    }

    @Test
    void writeFromStream_createsPaginatedSheetsIfNeeded() throws IOException {
        ExcelWriter writer = new ExcelWriter();
        // small dataset, 3 rows to keep it simple
        List<PersonDTO> l = Arrays.asList(
                new PersonDTO("A", 1, new BigDecimal("1.00")),
                new PersonDTO("B", 2, new BigDecimal("2.00")),
                new PersonDTO("C", 3, new BigDecimal("3.00"))
        );
        Stream<PersonDTO> stream = l.stream();

        try (SXSSFWorkbook wb = writer.write(stream)) {
            assertNotNull(wb);
            Sheet sheet = wb.getSheetAt(0);
            assertEquals("Persons", sheet.getSheetName());

            DataFormatter fmt = new DataFormatter();
            assertEquals("Name", fmt.formatCellValue(sheet.getRow(0).getCell(0)));
            assertEquals("A", fmt.formatCellValue(sheet.getRow(1).getCell(0)));
            assertEquals("B", fmt.formatCellValue(sheet.getRow(2).getCell(0)));
            assertEquals("C", fmt.formatCellValue(sheet.getRow(3).getCell(0)));
        }
    }

    @Test
    void write_detectsColumnSheetSplit() throws IOException {
        ExcelWriter writer = new ExcelWriter();
        List<MultiSheetColumnDTO> list = Arrays.asList(
                new MultiSheetColumnDTO("A1", "B1"),
                new MultiSheetColumnDTO("A2", "B2")
        );

        try (SXSSFWorkbook wb = writer.write(list)) {
            assertEquals(2, wb.getNumberOfSheets());
            List<String> sheetNames = new ArrayList<>();
            sheetNames.add(wb.getSheetAt(0).getSheetName());
            sheetNames.add(wb.getSheetAt(1).getSheetName());
            assertTrue(sheetNames.contains("SheetA"));
            assertTrue(sheetNames.contains("SheetB"));

            DataFormatter fmt = new DataFormatter();
            Sheet sheetA = "SheetA".equals(wb.getSheetAt(0).getSheetName()) ? wb.getSheetAt(0) : wb.getSheetAt(1);
            Sheet sheetB = "SheetB".equals(wb.getSheetAt(0).getSheetName()) ? wb.getSheetAt(0) : wb.getSheetAt(1);

            assertEquals("ColA", fmt.formatCellValue(sheetA.getRow(0).getCell(0)));
            assertEquals("A1", fmt.formatCellValue(sheetA.getRow(1).getCell(0)));
            assertEquals("A2", fmt.formatCellValue(sheetA.getRow(2).getCell(0)));

            assertEquals("ColB", fmt.formatCellValue(sheetB.getRow(0).getCell(0)));
            assertEquals("B1", fmt.formatCellValue(sheetB.getRow(1).getCell(0)));
            assertEquals("B2", fmt.formatCellValue(sheetB.getRow(2).getCell(0)));
        }
    }

    @Test
    void write_formatFromStyleAndAnnotation() throws IOException {
        ExcelWriter writer = new ExcelWriter();
        List<SalaryDTO> list = Arrays.asList(
                new SalaryDTO("Alice", new BigDecimal("5000000"), new BigDecimal("1500000.50")),
                new SalaryDTO("Bob", new BigDecimal("3000000"), new BigDecimal("750000.75"))
        );

        try (SXSSFWorkbook wb = writer.write(list)) {
            assertNotNull(wb);
            Sheet sheet = wb.getSheetAt(0);
            assertEquals("Salaries", sheet.getSheetName());

            DataFormatter fmt = new DataFormatter();

            Row r1 = sheet.getRow(1);
            Row r2 = sheet.getRow(2);

            assertEquals("Alice", fmt.formatCellValue(r1.getCell(0)));
            assertEquals("₩5,000,000", fmt.formatCellValue(r1.getCell(1)));
            assertEquals("1,500,000.50", fmt.formatCellValue(r1.getCell(2)));

            assertEquals("Bob", fmt.formatCellValue(r2.getCell(0)));
            assertEquals("₩3,000,000", fmt.formatCellValue(r2.getCell(1)));
            assertEquals("750,000.75", fmt.formatCellValue(r2.getCell(2)));
        }
    }

    @Test
    @Tag("performance")
    void write_exceedsOneMillionRows_createsMultipleSheets() throws IOException {
        ExcelWriter writer = new ExcelWriter();
        int totalRows = 1_500_000;
        Stream<PersonDTO> largeStream = IntStream.range(0, totalRows)
                .mapToObj(i -> new PersonDTO("Person" + i, 20 + (i % 50), new BigDecimal(i * 100)));

        try (SXSSFWorkbook wb = writer.write(largeStream)) {
            assertNotNull(wb);
            assertEquals(2, wb.getNumberOfSheets(), "1.5M rows should create 2 sheets");

            Sheet sheet1 = wb.getSheetAt(0);
            Sheet sheet2 = wb.getSheetAt(1);

            assertEquals("Persons", sheet1.getSheetName());
            assertEquals("Persons2", sheet2.getSheetName());

            assertEquals(1_000_001, sheet1.getPhysicalNumberOfRows(), "First sheet: header + 1M rows");
            assertEquals(500_001, sheet2.getPhysicalNumberOfRows(), "Second sheet: header + 500K rows");
        }
    }

    @Test
    @Tag("performance")
    void write_exactlyOneMillionRows_singleSheet() throws IOException {
        ExcelWriter writer = new ExcelWriter();
        int totalRows = 1_000_000;
        Stream<PersonDTO> stream = IntStream.range(0, totalRows)
                .mapToObj(i -> new PersonDTO("Person" + i, 20 + (i % 50), new BigDecimal(i * 100)));

        try (SXSSFWorkbook wb = writer.write(stream)) {
            assertNotNull(wb);
            assertEquals(1, wb.getNumberOfSheets(), "Exactly 1M rows should create 1 sheet");

            Sheet sheet = wb.getSheetAt(0);
            assertEquals("Persons", sheet.getSheetName());
            assertEquals(1_000_001, sheet.getPhysicalNumberOfRows(), "Header + 1M rows");
        }
    }

    @Test
    @Tag("performance")
    void write_twoMillionRows_createsTwoSheets() throws IOException {
        ExcelWriter writer = new ExcelWriter();
        int totalRows = 2_000_000;
        Stream<PersonDTO> largeStream = IntStream.range(0, totalRows)
                .mapToObj(i -> new PersonDTO("Person" + i, 20 + (i % 50), new BigDecimal(i * 100)));

        try (SXSSFWorkbook wb = writer.write(largeStream)) {
            assertNotNull(wb);
            assertEquals(2, wb.getNumberOfSheets(), "2M rows should create 2 sheets");

            Sheet sheet1 = wb.getSheetAt(0);
            Sheet sheet2 = wb.getSheetAt(1);

            assertEquals("Persons", sheet1.getSheetName());
            assertEquals("Persons2", sheet2.getSheetName());

            assertEquals(1_000_001, sheet1.getPhysicalNumberOfRows());
            assertEquals(1_000_001, sheet2.getPhysicalNumberOfRows());
        }
    }

    @Test
    @Tag("performance")
    void write_multiSheetWithPagination_eachSheetSplitsSeparately() throws IOException {
        ExcelWriter writer = new ExcelWriter();
        int totalRows = 1_100_000;
        Stream<MultiSheetColumnDTO> stream = IntStream.range(0, totalRows)
                .mapToObj(i -> new MultiSheetColumnDTO("A" + i, "B" + i));

        try (SXSSFWorkbook wb = writer.write(stream)) {
            assertNotNull(wb);
            assertEquals(4, wb.getNumberOfSheets(), "1.1M rows with 2 sheets should create 4 paginated sheets");

            boolean foundSheetA = false;
            boolean foundSheetA2 = false;
            boolean foundSheetB = false;
            boolean foundSheetB2 = false;

            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                if ("SheetA".equals(sheetName)) {
                    foundSheetA = true;
                    assertEquals(1_000_001, sheet.getPhysicalNumberOfRows(), "SheetA should have 1M rows + header");
                } else if ("SheetA2".equals(sheetName)) {
                    foundSheetA2 = true;
                    assertEquals(100_001, sheet.getPhysicalNumberOfRows(), "SheetA2 should have 100K rows + header");
                } else if ("SheetB".equals(sheetName)) {
                    foundSheetB = true;
                    assertEquals(1_000_001, sheet.getPhysicalNumberOfRows(), "SheetB should have 1M rows + header");
                } else if ("SheetB2".equals(sheetName)) {
                    foundSheetB2 = true;
                    assertEquals(100_001, sheet.getPhysicalNumberOfRows(), "SheetB2 should have 100K rows + header");
                }
            }

            assertTrue(foundSheetA, "Should have SheetA");
            assertTrue(foundSheetA2, "Should have SheetA2");
            assertTrue(foundSheetB, "Should have SheetB");
            assertTrue(foundSheetB2, "Should have SheetB2");
        }
    }
}