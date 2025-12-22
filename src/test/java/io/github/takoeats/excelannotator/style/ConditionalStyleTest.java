package io.github.takoeats.excelannotator.style;

import io.github.takoeats.excelannotator.ExcelExporter;
import io.github.takoeats.excelannotator.testdto.ConditionalStyleTestDTO.*;
import java.util.Collections;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalStyleTest {

    @Test
    void conditionalStyle_numericComparison_lessThan() throws Exception {
        List<NumericConditionDTO> data = Arrays.asList(
                new NumericConditionDTO("Item1", new BigDecimal("-100")),
                new NumericConditionDTO("Item2", new BigDecimal("50")),
                new NumericConditionDTO("Item3", new BigDecimal("200"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

        DataFormatter fmt = new DataFormatter();
        Row row1 = sheet.getRow(1);
        assertEquals("-100", fmt.formatCellValue(row1.getCell(1)));

        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);
        byte[] rgb1 = bgColor1.getRGB();
        assertEquals((byte) 255, rgb1[0]);
        assertEquals((byte) 192, rgb1[1]);
        assertEquals((byte) 203, rgb1[2]);
        assertEquals(FillPatternType.SOLID_FOREGROUND, style1.getFillPattern());
        assertEquals(BorderStyle.DASHED, style1.getBorderTop());
        assertEquals(HorizontalAlignment.CENTER, style1.getAlignment());

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNull(bgColor2);

        wb.close();
    }

    @Test
    void conditionalStyle_numericComparison_greaterThan() throws Exception {
        List<NumericConditionDTO> data = Collections.singletonList(
            new NumericConditionDTO("Item1", new BigDecimal("2000000"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(2, sheet.getPhysicalNumberOfRows());

        Row row1 = sheet.getRow(1);
        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);
        byte[] rgb1 = bgColor1.getRGB();
        assertEquals((byte) 255, rgb1[0]);
        assertEquals((byte) 255, rgb1[1]);
        assertEquals((byte) 0, rgb1[2]);
        assertEquals(FillPatternType.SOLID_FOREGROUND, style1.getFillPattern());
        assertEquals(BorderStyle.THIN, style1.getBorderTop());
        assertEquals(HorizontalAlignment.CENTER, style1.getAlignment());

        wb.close();
    }

    @Test
    void conditionalStyle_stringComparison_equals() throws Exception {
        List<StringConditionDTO> data = Arrays.asList(
                new StringConditionDTO("Task1", "완료"),
                new StringConditionDTO("Task2", "진행중"),
                new StringConditionDTO("Task3", "대기")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

        DataFormatter fmt = new DataFormatter();
        Row row1 = sheet.getRow(1);
        assertEquals("완료", fmt.formatCellValue(row1.getCell(1)));

        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);
        byte[] rgb1 = bgColor1.getRGB();
        assertEquals((byte) 175, rgb1[0]);
        assertEquals((byte) 238, rgb1[1]);
        assertEquals((byte) 238, rgb1[2]);
        assertEquals(FillPatternType.SOLID_FOREGROUND, style1.getFillPattern());
        assertEquals(BorderStyle.DOUBLE, style1.getBorderTop());

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNotNull(bgColor2);
        byte[] rgb2 = bgColor2.getRGB();
        assertEquals((byte) 255, rgb2[0]);
        assertEquals((byte) 255, rgb2[1]);
        assertEquals((byte) 0, rgb2[2]);

        wb.close();
    }

    @Test
    void conditionalStyle_stringComparison_contains() throws Exception {
        List<StringConditionDTO> data = Arrays.asList(
                new StringConditionDTO("Task1", "진행중 - 50%"),
                new StringConditionDTO("Task2", "완료")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(3, sheet.getPhysicalNumberOfRows());

        Row row1 = sheet.getRow(1);
        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);
        byte[] rgb1 = bgColor1.getRGB();
        assertEquals((byte) 255, rgb1[0]);
        assertEquals((byte) 255, rgb1[1]);
        assertEquals((byte) 0, rgb1[2]);

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNotNull(bgColor2);
        byte[] rgb2 = bgColor2.getRGB();
        assertEquals((byte) 175, rgb2[0]);
        assertEquals((byte) 238, rgb2[1]);
        assertEquals((byte) 238, rgb2[2]);

        wb.close();
    }

    @Test
    void conditionalStyle_logicalOperators_and() throws Exception {
        List<LogicalOperatorDTO> data = Arrays.asList(
                new LogicalOperatorDTO("Range1", 5),
                new LogicalOperatorDTO("Range2", 50),
                new LogicalOperatorDTO("Range3", 150)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

        DataFormatter fmt = new DataFormatter();
        Row row2 = sheet.getRow(2);
        assertEquals("50", fmt.formatCellValue(row2.getCell(1)));

        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNotNull(bgColor2);
        byte[] rgb2 = bgColor2.getRGB();
        assertEquals((byte) 175, rgb2[0]);
        assertEquals((byte) 238, rgb2[1]);
        assertEquals((byte) 238, rgb2[2]);
        assertEquals(BorderStyle.DOUBLE, style2.getBorderTop());

        wb.close();
    }

    @Test
    void conditionalStyle_logicalOperators_or() throws Exception {
        List<LogicalOperatorDTO> data = Arrays.asList(
                new LogicalOperatorDTO("Test1", -10),
                new LogicalOperatorDTO("Test2", 50),
                new LogicalOperatorDTO("Test3", 200000)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

        Row row1 = sheet.getRow(1);
        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);
        byte[] rgb1 = bgColor1.getRGB();
        assertEquals((byte) 255, rgb1[0]);
        assertEquals((byte) 192, rgb1[1]);
        assertEquals((byte) 203, rgb1[2]);

        Row row3 = sheet.getRow(3);
        Cell cell3 = row3.getCell(1);
        XSSFCellStyle style3 = (XSSFCellStyle) cell3.getCellStyle();
        XSSFColor bgColor3 = style3.getFillForegroundColorColor();
        assertNotNull(bgColor3);
        byte[] rgb3 = bgColor3.getRGB();
        assertEquals((byte) 255, rgb3[0]);
        assertEquals((byte) 192, rgb3[1]);
        assertEquals((byte) 203, rgb3[2]);

        wb.close();
    }

    @Test
    void conditionalStyle_priorityOrdering_highestPriorityWins() throws Exception {
        List<PriorityTestDTO> data = Collections.singletonList(
            new PriorityTestDTO("Priority1", new BigDecimal("-500"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(2, sheet.getPhysicalNumberOfRows());

        Row row1 = sheet.getRow(1);
        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);
        byte[] rgb1 = bgColor1.getRGB();
        assertEquals((byte) 255, rgb1[0]);
        assertEquals((byte) 192, rgb1[1]);
        assertEquals((byte) 203, rgb1[2]);
        assertEquals(BorderStyle.DASHED, style1.getBorderTop());

        wb.close();
    }

    @Test
    void conditionalStyle_betweenOperator() throws Exception {
        List<BetweenConditionDTO> data = Arrays.asList(
                new BetweenConditionDTO("Value1", 50),
                new BetweenConditionDTO("Value2", 5),
                new BetweenConditionDTO("Value3", 150)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

        DataFormatter fmt = new DataFormatter();
        Row row1 = sheet.getRow(1);
        assertEquals("50", fmt.formatCellValue(row1.getCell(1)));

        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);
        byte[] rgb1 = bgColor1.getRGB();
        assertEquals((byte) 255, rgb1[0]);
        assertEquals((byte) 255, rgb1[1]);
        assertEquals((byte) 0, rgb1[2]);
        assertEquals(BorderStyle.THIN, style1.getBorderTop());

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNull(bgColor2);

        wb.close();
    }

    @Test
    void conditionalStyle_multipleTrueConditions_highestPriorityApplies() throws Exception {
        List<PriorityTestDTO> data = Arrays.asList(
                new PriorityTestDTO("Test1", new BigDecimal("-500")),
                new PriorityTestDTO("Test2", new BigDecimal("50")),
                new PriorityTestDTO("Test3", new BigDecimal("500"))
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);
        byte[] rgb1 = bgColor1.getRGB();
        assertEquals((byte) 255, rgb1[0]);
        assertEquals((byte) 192, rgb1[1]);
        assertEquals((byte) 203, rgb1[2]);
        assertEquals(BorderStyle.DASHED, style1.getBorderTop());

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNotNull(bgColor2);
        byte[] rgb2 = bgColor2.getRGB();
        assertEquals((byte) 255, rgb2[0]);
        assertEquals((byte) 255, rgb2[1]);
        assertEquals((byte) 0, rgb2[2]);
        assertEquals(BorderStyle.THIN, style2.getBorderTop());

        Row row3 = sheet.getRow(3);
        Cell cell3 = row3.getCell(1);
        XSSFCellStyle style3 = (XSSFCellStyle) cell3.getCellStyle();
        XSSFColor bgColor3 = style3.getFillForegroundColorColor();
        assertNotNull(bgColor3);
        byte[] rgb3 = bgColor3.getRGB();
        assertEquals((byte) 175, rgb3[0]);
        assertEquals((byte) 238, rgb3[1]);
        assertEquals((byte) 238, rgb3[2]);
        assertEquals(BorderStyle.DOUBLE, style3.getBorderTop());

        wb.close();
    }
}
