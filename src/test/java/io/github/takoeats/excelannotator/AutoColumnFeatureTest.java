package io.github.takoeats.excelannotator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.takoeats.excelannotator.testdto.AutoColumnDTO;
import io.github.takoeats.excelannotator.testdto.AutoColumnMixedDTO;
import io.github.takoeats.excelannotator.testdto.AutoColumnWithExcludeDTO;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

class AutoColumnFeatureTest {

    @Test
    void autoColumn_withAllFields_shouldExportAllFieldsInDeclarationOrder() throws Exception {
        List<AutoColumnDTO> list = Arrays.asList(
            new AutoColumnDTO("Alice", 30, "alice@example.com", 50000.0),
            new AutoColumnDTO("Bob", 40, "bob@example.com", 60000.0)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.excelFromList(baos, "autoColumn.xlsx", list);
        assertNotNull(returnedName);
        assertEquals("autoColumn.xlsx", returnedName);

        byte[] bytes = baos.toByteArray();
        assertTrue(bytes.length > 0);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes));
        assertEquals(1, wb.getNumberOfSheets());
        Sheet sheet = wb.getSheetAt(0);
        assertEquals("AutoColumn", sheet.getSheetName());

        DataFormatter fmt = new DataFormatter();
        Row header = sheet.getRow(0);
        assertEquals("name", fmt.formatCellValue(header.getCell(0)));
        assertEquals("age", fmt.formatCellValue(header.getCell(1)));
        assertEquals("email", fmt.formatCellValue(header.getCell(2)));
        assertEquals("salary", fmt.formatCellValue(header.getCell(3)));

        Row row1 = sheet.getRow(1);
        assertEquals("Alice", fmt.formatCellValue(row1.getCell(0)));
        assertEquals("30", fmt.formatCellValue(row1.getCell(1)));
        assertEquals("alice@example.com", fmt.formatCellValue(row1.getCell(2)));

        Row row2 = sheet.getRow(2);
        assertEquals("Bob", fmt.formatCellValue(row2.getCell(0)));
        assertEquals("40", fmt.formatCellValue(row2.getCell(1)));
        assertEquals("bob@example.com", fmt.formatCellValue(row2.getCell(2)));

        wb.close();
    }

    @Test
    void autoColumn_withExcludedField_shouldSkipExcludedField() throws Exception {
        List<AutoColumnWithExcludeDTO> list = Arrays.asList(
            new AutoColumnWithExcludeDTO("user1", "secret123", "user1@example.com", 25),
            new AutoColumnWithExcludeDTO("user2", "secret456", "user2@example.com", 30)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.excelFromList(baos, "autoColumnExclude.xlsx", list);
        assertNotNull(returnedName);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals("AutoColumnExclude", sheet.getSheetName());

        DataFormatter fmt = new DataFormatter();
        Row header = sheet.getRow(0);
        assertEquals("username", fmt.formatCellValue(header.getCell(0)));
        assertEquals("email", fmt.formatCellValue(header.getCell(1)));
        assertEquals("age", fmt.formatCellValue(header.getCell(2)));

        Row row1 = sheet.getRow(1);
        assertEquals("user1", fmt.formatCellValue(row1.getCell(0)));
        assertEquals("user1@example.com", fmt.formatCellValue(row1.getCell(1)));
        assertEquals("25", fmt.formatCellValue(row1.getCell(2)));

        wb.close();
    }

    @Test
    void autoColumn_withMixedAnnotations_shouldRespectExplicitAnnotations() throws Exception {
        List<AutoColumnMixedDTO> list = Arrays.asList(
            new AutoColumnMixedDTO("Alice Smith", 30, "alice@example.com", "123-456-7890", "ID001"),
            new AutoColumnMixedDTO("Bob Jones", 40, "bob@example.com", "987-654-3210", "ID002")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String returnedName = ExcelExporter.excelFromList(baos, "autoColumnMixed.xlsx", list);
        assertNotNull(returnedName);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals("AutoColumnMixed", sheet.getSheetName());

        DataFormatter fmt = new DataFormatter();
        Row header = sheet.getRow(0);
        assertEquals("Full Name", fmt.formatCellValue(header.getCell(0)));
        assertEquals("age", fmt.formatCellValue(header.getCell(1)));
        assertEquals("Email Address", fmt.formatCellValue(header.getCell(2)));
        assertEquals("phone", fmt.formatCellValue(header.getCell(3)));

        Row row1 = sheet.getRow(1);
        assertEquals("Alice Smith", fmt.formatCellValue(row1.getCell(0)));
        assertEquals("30", fmt.formatCellValue(row1.getCell(1)));
        assertEquals("alice@example.com", fmt.formatCellValue(row1.getCell(2)));
        assertEquals("123-456-7890", fmt.formatCellValue(row1.getCell(3)));

        wb.close();
    }
}
