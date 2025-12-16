package com.junho.excel;

import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.style.RgbColorHelper;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RgbColorHelperTest {

    @Test
    void validateRgb_validValues_noException() {
        assertDoesNotThrow(() -> RgbColorHelper.validateRgb(0, 0, 0));
        assertDoesNotThrow(() -> RgbColorHelper.validateRgb(255, 255, 255));
        assertDoesNotThrow(() -> RgbColorHelper.validateRgb(128, 64, 192));
        assertDoesNotThrow(() -> RgbColorHelper.validateRgb(255, 0, 0));
        assertDoesNotThrow(() -> RgbColorHelper.validateRgb(0, 255, 0));
        assertDoesNotThrow(() -> RgbColorHelper.validateRgb(0, 0, 255));
    }

    @Test
    void validateRgb_invalidRedValue_throwsException() {
        ExcelExporterException exception1 = assertThrows(
            ExcelExporterException.class,
            () -> RgbColorHelper.validateRgb(-1, 0, 0)
        );
        assertTrue(exception1.getMessage().contains("Red"));

        ExcelExporterException exception2 = assertThrows(
            ExcelExporterException.class,
            () -> RgbColorHelper.validateRgb(256, 0, 0)
        );
        assertTrue(exception2.getMessage().contains("Red"));

        ExcelExporterException exception3 = assertThrows(
            ExcelExporterException.class,
            () -> RgbColorHelper.validateRgb(1000, 0, 0)
        );
        assertTrue(exception3.getMessage().contains("Red"));
    }

    @Test
    void validateRgb_invalidGreenValue_throwsException() {
        ExcelExporterException exception1 = assertThrows(
            ExcelExporterException.class,
            () -> RgbColorHelper.validateRgb(0, -1, 0)
        );
        assertTrue(exception1.getMessage().contains("Green"));

        ExcelExporterException exception2 = assertThrows(
            ExcelExporterException.class,
            () -> RgbColorHelper.validateRgb(0, 256, 0)
        );
        assertTrue(exception2.getMessage().contains("Green"));
    }

    @Test
    void validateRgb_invalidBlueValue_throwsException() {
        ExcelExporterException exception1 = assertThrows(
            ExcelExporterException.class,
            () -> RgbColorHelper.validateRgb(0, 0, -1)
        );
        assertTrue(exception1.getMessage().contains("Blue"));

        ExcelExporterException exception2 = assertThrows(
            ExcelExporterException.class,
            () -> RgbColorHelper.validateRgb(0, 0, 256)
        );
        assertTrue(exception2.getMessage().contains("Blue"));
    }

    @Test
    void createRgbColor_validValues_createsCorrectColor() {
        XSSFColor black = RgbColorHelper.createRgbColor(0, 0, 0);
        assertNotNull(black);
        assertArrayEquals(new byte[]{0, 0, 0}, black.getRGB());

        XSSFColor white = RgbColorHelper.createRgbColor(255, 255, 255);
        assertNotNull(white);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 255}, white.getRGB());

        XSSFColor red = RgbColorHelper.createRgbColor(255, 0, 0);
        assertNotNull(red);
        assertArrayEquals(new byte[]{(byte) 255, 0, 0}, red.getRGB());

        XSSFColor green = RgbColorHelper.createRgbColor(0, 255, 0);
        assertNotNull(green);
        assertArrayEquals(new byte[]{0, (byte) 255, 0}, green.getRGB());

        XSSFColor blue = RgbColorHelper.createRgbColor(0, 0, 255);
        assertNotNull(blue);
        assertArrayEquals(new byte[]{0, 0, (byte) 255}, blue.getRGB());

        XSSFColor custom = RgbColorHelper.createRgbColor(128, 64, 192);
        assertNotNull(custom);
        assertArrayEquals(new byte[]{(byte) 128, 64, (byte) 192}, custom.getRGB());
    }

    @Test
    void createRgbColor_invalidValues_throwsException() {
        assertThrows(ExcelExporterException.class,
            () -> RgbColorHelper.createRgbColor(-1, 0, 0));

        assertThrows(ExcelExporterException.class,
            () -> RgbColorHelper.createRgbColor(0, -1, 0));

        assertThrows(ExcelExporterException.class,
            () -> RgbColorHelper.createRgbColor(0, 0, -1));

        assertThrows(ExcelExporterException.class,
            () -> RgbColorHelper.createRgbColor(256, 0, 0));

        assertThrows(ExcelExporterException.class,
            () -> RgbColorHelper.createRgbColor(0, 256, 0));

        assertThrows(ExcelExporterException.class,
            () -> RgbColorHelper.createRgbColor(0, 0, 256));

        assertThrows(ExcelExporterException.class,
            () -> RgbColorHelper.createRgbColor(300, 300, 300));
    }

    @Test
    void createRgbColor_boundaryValues_handlesCorrectly() {
        XSSFColor minColor = RgbColorHelper.createRgbColor(0, 0, 0);
        assertArrayEquals(new byte[]{0, 0, 0}, minColor.getRGB());

        XSSFColor maxColor = RgbColorHelper.createRgbColor(255, 255, 255);
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 255}, maxColor.getRGB());

        XSSFColor midColor = RgbColorHelper.createRgbColor(127, 128, 129);
        assertArrayEquals(new byte[]{127, (byte) 128, (byte) 129}, midColor.getRGB());
    }

    @Test
    void utilityClass_cannotBeInstantiated() {
        try {
            java.lang.reflect.Constructor<RgbColorHelper> constructor =
                RgbColorHelper.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Should have thrown AssertionError");
        } catch (java.lang.reflect.InvocationTargetException e) {
            assertTrue(e.getCause() instanceof AssertionError);
            assertTrue(e.getCause().getMessage().contains("Utility class cannot be instantiated"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass().getName());
        }
    }
}
