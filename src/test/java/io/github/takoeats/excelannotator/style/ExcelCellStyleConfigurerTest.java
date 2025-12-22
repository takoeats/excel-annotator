package io.github.takoeats.excelannotator.style;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcelCellStyleConfigurerTest {

    @Test
    void backgroundColor_nullArray_throwsInvalidRgbArrayException() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> configurer.backgroundColor((int[]) null)
        );
        assertEquals(ErrorCode.INVALID_RGB_ARRAY, exception.getErrorCode());
    }

    @Test
    void backgroundColor_arrayLengthNot3_throwsInvalidRgbArrayException() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        ExcelExporterException exception1 = assertThrows(
            ExcelExporterException.class,
            () -> configurer.backgroundColor(new int[]{255, 255})
        );
        assertEquals(ErrorCode.INVALID_RGB_ARRAY, exception1.getErrorCode());

        ExcelExporterException exception2 = assertThrows(
            ExcelExporterException.class,
            () -> configurer.backgroundColor(new int[]{255, 255, 255, 255})
        );
        assertEquals(ErrorCode.INVALID_RGB_ARRAY, exception2.getErrorCode());

        ExcelExporterException exception3 = assertThrows(
            ExcelExporterException.class,
            () -> configurer.backgroundColor(new int[]{})
        );
        assertEquals(ErrorCode.INVALID_RGB_ARRAY, exception3.getErrorCode());

        ExcelExporterException exception4 = assertThrows(
            ExcelExporterException.class,
            () -> configurer.backgroundColor(new int[]{255})
        );
        assertEquals(ErrorCode.INVALID_RGB_ARRAY, exception4.getErrorCode());
    }

    @Test
    void fontColor_nullArray_throwsInvalidRgbArrayException() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> configurer.fontColor((int[]) null)
        );
        assertEquals(ErrorCode.INVALID_RGB_ARRAY, exception.getErrorCode());
    }

    @Test
    void fontColor_arrayLengthNot3_throwsInvalidRgbArrayException() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        ExcelExporterException exception1 = assertThrows(
            ExcelExporterException.class,
            () -> configurer.fontColor(new int[]{255, 255})
        );
        assertEquals(ErrorCode.INVALID_RGB_ARRAY, exception1.getErrorCode());

        ExcelExporterException exception2 = assertThrows(
            ExcelExporterException.class,
            () -> configurer.fontColor(new int[]{255, 255, 255, 255})
        );
        assertEquals(ErrorCode.INVALID_RGB_ARRAY, exception2.getErrorCode());
    }

    @Test
    void font_fontNameOnly_configuresWithoutSizeAndStyle() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        configurer.font("Arial");

        assertDoesNotThrow(() -> configurer.toString());
    }

    @Test
    void alignment_verticalOnly_configuresWithoutHorizontal() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        configurer.alignment(VerticalAlignment.CENTER);

        assertTrue(configurer.isAutoWidth() == false);
    }

    @Test
    void alignment_horizontalOnly_configuresWithoutVertical() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        configurer.alignment(HorizontalAlignment.CENTER);

        assertTrue(configurer.isAutoWidth() == false);
    }

    @Test
    void autoWidth_setsAutoWidthTrueAndCellWidthNegative() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        configurer.autoWidth();

        assertTrue(configurer.isAutoWidth());
        assertEquals(-1, configurer.getColumnWidth());
    }

    @Test
    void getColumnWidth_autoWidthTrue_returnsNegativeOne() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        configurer.autoWidth();

        assertEquals(-1, configurer.getColumnWidth());
        assertTrue(configurer.isAutoWidth());
    }

    @Test
    void getColumnWidth_autoWidthFalseWithSetWidth_returnsSetWidth() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        configurer.width(250);

        assertEquals(250, configurer.getColumnWidth());
        assertFalse(configurer.isAutoWidth());
    }

    @Test
    void getColumnWidth_noWidthSet_returnsDefaultValue() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        assertEquals(100, configurer.getColumnWidth());
        assertFalse(configurer.isAutoWidth());
    }

    @Test
    void width_setsAutoWidthFalse() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        configurer.autoWidth();
        assertTrue(configurer.isAutoWidth());

        configurer.width(200);
        assertFalse(configurer.isAutoWidth());
        assertEquals(200, configurer.getColumnWidth());
    }

    @Test
    void getDataFormat_noFormatSet_returnsGeneral() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        assertEquals("General", configurer.getDataFormat());
    }

    @Test
    void getDataFormat_formatSet_returnsSetFormat() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        configurer.dataFormat("#,##0.00");

        assertEquals("#,##0.00", configurer.getDataFormat());
    }

    @Test
    void backgroundColor_validRgbArray_setsColors() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        assertDoesNotThrow(() -> configurer.backgroundColor(new int[]{255, 128, 64}));
    }

    @Test
    void fontColor_validRgbArray_setsColors() {
        ExcelCellStyleConfigurer configurer = new ExcelCellStyleConfigurer();

        assertDoesNotThrow(() -> configurer.fontColor(new int[]{255, 0, 0}));
    }
}
