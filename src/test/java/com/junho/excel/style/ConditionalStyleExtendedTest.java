package com.junho.excel.style;

import com.junho.excel.ExcelExporter;
import com.junho.excel.annotation.ConditionalStyle;
import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.CriticalAlertStyle;
import com.junho.excel.example.style.HighlightStyle;
import com.junho.excel.example.style.SignatureStyle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalStyleExtendedTest {

    @Test
    void conditionalStyle_lessThanOrEqual_appliesCorrectly() throws Exception {
        List<LessThanOrEqualDTO> data = Arrays.asList(
                new LessThanOrEqualDTO("Item1", 50),
                new LessThanOrEqualDTO("Item2", 100),
                new LessThanOrEqualDTO("Item3", 150)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_greaterThanOrEqual_appliesCorrectly() throws Exception {
        List<GreaterThanOrEqualDTO> data = Arrays.asList(
                new GreaterThanOrEqualDTO("Item1", 50),
                new GreaterThanOrEqualDTO("Item2", 100),
                new GreaterThanOrEqualDTO("Item3", 150)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_notEquals_appliesCorrectly() throws Exception {
        List<NotEqualsDTO> data = Arrays.asList(
                new NotEqualsDTO("Item1", 30),
                new NotEqualsDTO("Item2", 50),
                new NotEqualsDTO("Item3", 70)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_isNegative_appliesCorrectly() throws Exception {
        List<IsNegativeDTO> data = Arrays.asList(
                new IsNegativeDTO("Item1", -10),
                new IsNegativeDTO("Item2", 0),
                new IsNegativeDTO("Item3", 10)
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
        assertEquals(BorderStyle.DASHED, style1.getBorderTop());

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNull(bgColor2);

        wb.close();
    }

    @Test
    void conditionalStyle_isPositive_appliesCorrectly() throws Exception {
        List<IsPositiveDTO> data = Arrays.asList(
                new IsPositiveDTO("Item1", -10),
                new IsPositiveDTO("Item2", 0),
                new IsPositiveDTO("Item3", 10)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_isZero_appliesCorrectly() throws Exception {
        List<IsZeroDTO> data = Arrays.asList(
                new IsZeroDTO("Item1", -10),
                new IsZeroDTO("Item2", 0),
                new IsZeroDTO("Item3", 10)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_equalsIgnoreCase_appliesCorrectly() throws Exception {
        List<EqualsIgnoreCaseDTO> data = Arrays.asList(
                new EqualsIgnoreCaseDTO("Task1", "COMPLETE"),
                new EqualsIgnoreCaseDTO("Task2", "complete"),
                new EqualsIgnoreCaseDTO("Task3", "Complete"),
                new EqualsIgnoreCaseDTO("Task4", "진행중")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(5, sheet.getPhysicalNumberOfRows());

        for (int i = 1; i <= 3; i++) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(1);
            XSSFCellStyle style = (XSSFCellStyle) cell.getCellStyle();
            XSSFColor bgColor = style.getFillForegroundColorColor();
            assertNotNull(bgColor, "Row " + i + " should have SignatureStyle");
            byte[] rgb = bgColor.getRGB();
            assertEquals((byte) 175, rgb[0]);
            assertEquals((byte) 238, rgb[1]);
            assertEquals((byte) 238, rgb[2]);
        }

        Row row4 = sheet.getRow(4);
        Cell cell4 = row4.getCell(1);
        XSSFCellStyle style4 = (XSSFCellStyle) cell4.getCellStyle();
        XSSFColor bgColor4 = style4.getFillForegroundColorColor();
        assertNull(bgColor4);

        wb.close();
    }

    @Test
    void conditionalStyle_startsWith_appliesCorrectly() throws Exception {
        List<StartsWithDTO> data = Arrays.asList(
                new StartsWithDTO("Task1", "주문완료"),
                new StartsWithDTO("Task2", "주문취소"),
                new StartsWithDTO("Task3", "배송완료")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_endsWith_appliesCorrectly() throws Exception {
        List<EndsWithDTO> data = Arrays.asList(
                new EndsWithDTO("Task1", "주문완료"),
                new EndsWithDTO("Task2", "배송완료"),
                new EndsWithDTO("Task3", "진행중")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_isNull_appliesCorrectly() throws Exception {
        List<IsNullDTO> data = Arrays.asList(
                new IsNullDTO("Item1", null),
                new IsNullDTO("Item2", "test"),
                new IsNullDTO("Item3", "")
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

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNull(bgColor2);

        wb.close();
    }

    @Test
    void conditionalStyle_isNotNull_appliesCorrectly() throws Exception {
        List<IsNotNullDTO> data = Arrays.asList(
                new IsNotNullDTO("Item1", null),
                new IsNotNullDTO("Item2", "test"),
                new IsNotNullDTO("Item3", "")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_isEmpty_appliesCorrectly() throws Exception {
        List<IsEmptyDTO> data = Arrays.asList(
                new IsEmptyDTO("Item1", null),
                new IsEmptyDTO("Item2", ""),
                new IsEmptyDTO("Item3", "   "),
                new IsEmptyDTO("Item4", "test")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(5, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_isNotEmpty_appliesCorrectly() throws Exception {
        List<IsNotEmptyDTO> data = Arrays.asList(
                new IsNotEmptyDTO("Item1", null),
                new IsNotEmptyDTO("Item2", ""),
                new IsNotEmptyDTO("Item3", "test")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_xorOperator_appliesCorrectly() throws Exception {
        List<XorOperatorDTO> data = Arrays.asList(
                new XorOperatorDTO("Item1", -10),
                new XorOperatorDTO("Item2", 50),
                new XorOperatorDTO("Item3", 150)
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
        assertEquals((byte) 255, rgb1[1]);
        assertEquals((byte) 0, rgb1[2]);

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNull(bgColor2);

        Row row3 = sheet.getRow(3);
        Cell cell3 = row3.getCell(1);
        XSSFCellStyle style3 = (XSSFCellStyle) cell3.getCellStyle();
        XSSFColor bgColor3 = style3.getFillForegroundColorColor();
        assertNotNull(bgColor3);
        byte[] rgb3 = bgColor3.getRGB();
        assertEquals((byte) 255, rgb3[0]);
        assertEquals((byte) 255, rgb3[1]);
        assertEquals((byte) 0, rgb3[2]);

        wb.close();
    }

    @Test
    void conditionalStyle_notOperator_appliesCorrectly() throws Exception {
        List<NotOperatorDTO> data = Arrays.asList(
                new NotOperatorDTO("Item1", 30),
                new NotOperatorDTO("Item2", 100),
                new NotOperatorDTO("Item3", 150)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Test
    void conditionalStyle_complexNestedParentheses_appliesCorrectly() throws Exception {
        List<ComplexNestedDTO> data = Arrays.asList(
                new ComplexNestedDTO("Item1", 25),
                new ComplexNestedDTO("Item2", 75),
                new ComplexNestedDTO("Item3", 125)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(wb);
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());
        wb.close();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("LessThanOrEqual")
    public static class LessThanOrEqualDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value <= 100", style = HighlightStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("GreaterThanOrEqual")
    public static class GreaterThanOrEqualDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value >= 100", style = HighlightStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("NotEquals")
    public static class NotEqualsDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value != 50", style = HighlightStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("IsNegative")
    public static class IsNegativeDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value is_negative", style = CriticalAlertStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("IsPositive")
    public static class IsPositiveDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value is_positive", style = HighlightStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("IsZero")
    public static class IsZeroDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value is_zero", style = SignatureStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("EqualsIgnoreCase")
    public static class EqualsIgnoreCaseDTO {
        @ExcelColumn(header = "Task", order = 1)
        private String task;

        @ExcelColumn(
                header = "Status",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value equals_ignore_case 'complete'", style = SignatureStyle.class, priority = 10)
                }
        )
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("StartsWith")
    public static class StartsWithDTO {
        @ExcelColumn(header = "Task", order = 1)
        private String task;

        @ExcelColumn(
                header = "Status",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value starts_with '주문'", style = HighlightStyle.class, priority = 10)
                }
        )
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("EndsWith")
    public static class EndsWithDTO {
        @ExcelColumn(header = "Task", order = 1)
        private String task;

        @ExcelColumn(
                header = "Status",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value ends_with '완료'", style = SignatureStyle.class, priority = 10)
                }
        )
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("IsNull")
    public static class IsNullDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value is_null", style = CriticalAlertStyle.class, priority = 10)
                }
        )
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("IsNotNull")
    public static class IsNotNullDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value is_not_null", style = HighlightStyle.class, priority = 10)
                }
        )
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("IsEmpty")
    public static class IsEmptyDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value is_empty", style = CriticalAlertStyle.class, priority = 10)
                }
        )
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("IsNotEmpty")
    public static class IsNotEmptyDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value is_not_empty", style = HighlightStyle.class, priority = 10)
                }
        )
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("XorOperator")
    public static class XorOperatorDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value < 0 ^ value > 100", style = HighlightStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("NotOperator")
    public static class NotOperatorDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "!(value > 100)", style = HighlightStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("ComplexNested")
    public static class ComplexNestedDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(
                                when = "(value > 0 && value < 50) || (value > 100 && value < 150)",
                                style = HighlightStyle.class,
                                priority = 10
                        )
                }
        )
        private Integer value;
    }
}
