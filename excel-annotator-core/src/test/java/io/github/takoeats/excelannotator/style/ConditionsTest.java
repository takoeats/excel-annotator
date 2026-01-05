package io.github.takoeats.excelannotator.style;

import io.github.takoeats.excelannotator.ExcelExporter;
import io.github.takoeats.excelannotator.annotation.ConditionalStyle;
import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.teststyle.CriticalAlertStyle;
import io.github.takoeats.excelannotator.teststyle.HighlightStyle;
import io.github.takoeats.excelannotator.teststyle.SignatureStyle;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionsTest {

    @Test
    void conditions_constantValues_areCorrect() {
        assertEquals("value is_negative", Conditions.IS_NEGATIVE);
        assertEquals("value is_positive", Conditions.IS_POSITIVE);
        assertEquals("value is_zero", Conditions.IS_ZERO);
        assertEquals("value is_null", Conditions.IS_NULL);
        assertEquals("value is_not_null", Conditions.IS_NOT_NULL);
        assertEquals("value is_empty", Conditions.IS_EMPTY);
        assertEquals("value is_not_empty", Conditions.IS_NOT_EMPTY);
    }

    @Test
    void conditions_isNegative_appliesStyleCorrectly() throws Exception {
        List<NegativeDTO> data = Arrays.asList(
                new NegativeDTO("Item1", -10),
                new NegativeDTO("Item2", 0),
                new NegativeDTO("Item3", 10)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
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

        wb.close();
    }

    @Test
    void conditions_isPositive_appliesStyleCorrectly() throws Exception {
        List<PositiveDTO> data = Arrays.asList(
                new PositiveDTO("Item1", -10),
                new PositiveDTO("Item2", 0),
                new PositiveDTO("Item3", 10)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

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
    void conditions_isZero_appliesStyleCorrectly() throws Exception {
        List<ZeroDTO> data = Arrays.asList(
                new ZeroDTO("Item1", -10),
                new ZeroDTO("Item2", 0),
                new ZeroDTO("Item3", 10)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

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
    void conditions_isNull_appliesStyleCorrectly() throws Exception {
        List<NullDTO> data = Arrays.asList(
                new NullDTO("Item1", null),
                new NullDTO("Item2", "test"),
                new NullDTO("Item3", "")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
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

        wb.close();
    }

    @Test
    void conditions_isNotNull_appliesStyleCorrectly() throws Exception {
        List<NotNullDTO> data = Arrays.asList(
                new NotNullDTO("Item1", null),
                new NotNullDTO("Item2", "test"),
                new NotNullDTO("Item3", "")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNotNull(bgColor2);

        Row row3 = sheet.getRow(3);
        Cell cell3 = row3.getCell(1);
        XSSFCellStyle style3 = (XSSFCellStyle) cell3.getCellStyle();
        XSSFColor bgColor3 = style3.getFillForegroundColorColor();
        assertNotNull(bgColor3);

        wb.close();
    }

    @Test
    void conditions_isEmpty_appliesStyleCorrectly() throws Exception {
        List<EmptyDTO> data = Arrays.asList(
                new EmptyDTO("Item1", null),
                new EmptyDTO("Item2", ""),
                new EmptyDTO("Item3", "   "),
                new EmptyDTO("Item4", "test")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(5, sheet.getPhysicalNumberOfRows());

        Row row1 = sheet.getRow(1);
        Cell cell1 = row1.getCell(1);
        XSSFCellStyle style1 = (XSSFCellStyle) cell1.getCellStyle();
        XSSFColor bgColor1 = style1.getFillForegroundColorColor();
        assertNotNull(bgColor1);

        Row row2 = sheet.getRow(2);
        Cell cell2 = row2.getCell(1);
        XSSFCellStyle style2 = (XSSFCellStyle) cell2.getCellStyle();
        XSSFColor bgColor2 = style2.getFillForegroundColorColor();
        assertNotNull(bgColor2);

        wb.close();
    }

    @Test
    void conditions_isNotEmpty_appliesStyleCorrectly() throws Exception {
        List<NotEmptyDTO> data = Arrays.asList(
                new NotEmptyDTO("Item1", null),
                new NotEmptyDTO("Item2", ""),
                new NotEmptyDTO("Item3", "test")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = wb.getSheetAt(0);
        assertEquals(4, sheet.getPhysicalNumberOfRows());

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("NegativeTest")
    public static class NegativeDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = Conditions.IS_NEGATIVE, style = CriticalAlertStyle.class)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("PositiveTest")
    public static class PositiveDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = Conditions.IS_POSITIVE, style = HighlightStyle.class)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("ZeroTest")
    public static class ZeroDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = Conditions.IS_ZERO, style = SignatureStyle.class)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("NullTest")
    public static class NullDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = Conditions.IS_NULL, style = CriticalAlertStyle.class)
                }
        )
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("NotNullTest")
    public static class NotNullDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = Conditions.IS_NOT_NULL, style = HighlightStyle.class)
                }
        )
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("EmptyTest")
    public static class EmptyDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = Conditions.IS_EMPTY, style = CriticalAlertStyle.class)
                }
        )
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("NotEmptyTest")
    public static class NotEmptyDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = Conditions.IS_NOT_EMPTY, style = HighlightStyle.class)
                }
        )
        private String value;
    }
}
