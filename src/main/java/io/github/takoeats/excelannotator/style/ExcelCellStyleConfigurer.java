package io.github.takoeats.excelannotator.style;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.style.internal.util.RgbColorHelper;
import io.github.takoeats.excelannotator.style.internal.wrapper.CellStyleWrapper;
import io.github.takoeats.excelannotator.style.internal.wrapper.WorkbookWrapper;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

/**
 * Excel 셀 스타일 Fluent API 설정 클래스
 * <p>배경색, 폰트, 정렬, 테두리, 포맷, 너비 등을 메서드 체이닝으로 설정</p>
 */
public class ExcelCellStyleConfigurer {

    private Integer cellWidth;
    @Getter
    private boolean autoWidth = false;

    private Integer backgroundColorRed;
    private Integer backgroundColorGreen;
    private Integer backgroundColorBlue;
    private boolean hasBackgroundColorRGB = false;

    private String fontName;
    private Integer fontSize;
    private FontStyle fontStyle;
    private boolean hasFont = false;

    private Integer fontColorRed;
    private Integer fontColorGreen;
    private Integer fontColorBlue;
    private boolean hasFontColorRGB = false;

    private HorizontalAlign horizontalAlignment;
    private VerticalAlign verticalAlignment;
    private boolean hasAlignment = false;

    private BorderType borderStyle;
    private boolean hasBorder = false;

    private String dataFormat;
    private boolean hasDataFormat = false;

    /**
     * RGB 배경색 설정 (0-255)
     */
    public ExcelCellStyleConfigurer backgroundColor(int red, int green, int blue) {
        RgbColorHelper.validateRgb(red, green, blue);

        this.backgroundColorRed = red;
        this.backgroundColorGreen = green;
        this.backgroundColorBlue = blue;
        this.hasBackgroundColorRGB = true;

        return this;
    }

    /**
     * RGB 배경색 설정 (int[] 배열)
     */
    public ExcelCellStyleConfigurer backgroundColor(int[] rgb) {
        if (rgb == null || rgb.length != 3) {
            throw new ExcelExporterException(ErrorCode.INVALID_RGB_ARRAY);
        }
        return backgroundColor(rgb[0], rgb[1], rgb[2]);
    }

    public ExcelCellStyleConfigurer font(String fontName) {
        this.fontName = fontName;
        this.hasFont = true;
        return this;
    }

    public ExcelCellStyleConfigurer font(String fontName, int size) {
        this.fontName = fontName;
        this.fontSize = size;
        this.hasFont = true;
        return this;
    }

    public ExcelCellStyleConfigurer font(String fontName, int size, FontStyle style) {
        this.fontName = fontName;
        this.fontSize = size;
        this.fontStyle = style;
        this.hasFont = true;
        return this;
    }

    /**
     * 폰트 RGB 색상 설정 (0-255)
     */
    public ExcelCellStyleConfigurer fontColor(int red, int green, int blue) {
        RgbColorHelper.validateRgb(red, green, blue);

        this.fontColorRed = red;
        this.fontColorGreen = green;
        this.fontColorBlue = blue;
        this.hasFontColorRGB = true;

        this.hasFont = true;

        return this;
    }

    /**
     * 폰트 RGB 색상 설정 (int[] 배열)
     */
    public ExcelCellStyleConfigurer fontColor(int[] rgb) {
        if (rgb == null || rgb.length != 3) {
            throw new ExcelExporterException(ErrorCode.INVALID_RGB_ARRAY);
        }
        return fontColor(rgb[0], rgb[1], rgb[2]);
    }

    public ExcelCellStyleConfigurer alignment(HorizontalAlign horizontal) {
        this.horizontalAlignment = horizontal;
        this.hasAlignment = true;
        return this;
    }

    public ExcelCellStyleConfigurer alignment(VerticalAlign vertical) {
        this.verticalAlignment = vertical;
        this.hasAlignment = true;
        return this;
    }

    public ExcelCellStyleConfigurer alignment(HorizontalAlign horizontal, VerticalAlign vertical) {
        this.horizontalAlignment = horizontal;
        this.verticalAlignment = vertical;
        this.hasAlignment = true;
        return this;
    }

    /**
     * @deprecated since 2.3.2, will be removed in 3.0.0. Use {@link #alignment(HorizontalAlign)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer alignment(org.apache.poi.ss.usermodel.HorizontalAlignment horizontal) {
        return alignment(HorizontalAlign.fromPoi(horizontal));
    }

    /**
     * @deprecated since 2.3.2, will be removed in 3.0.0. Use {@link #alignment(VerticalAlign)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer alignment(org.apache.poi.ss.usermodel.VerticalAlignment vertical) {
        return alignment(VerticalAlign.fromPoi(vertical));
    }

    /**
     * @deprecated since 2.3.2, will be removed in 3.0.0. Use {@link #alignment(HorizontalAlign, VerticalAlign)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer alignment(org.apache.poi.ss.usermodel.HorizontalAlignment horizontal, org.apache.poi.ss.usermodel.VerticalAlignment vertical) {
        return alignment(HorizontalAlign.fromPoi(horizontal), VerticalAlign.fromPoi(vertical));
    }

    public ExcelCellStyleConfigurer border(BorderType style) {
        this.borderStyle = style;
        this.hasBorder = true;
        return this;
    }

    /**
     * @deprecated since 2.3.2, will be removed in 3.0.0. Use {@link #border(BorderType)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer border(org.apache.poi.ss.usermodel.BorderStyle style) {
        return border(BorderType.fromPoi(style));
    }

    public ExcelCellStyleConfigurer dataFormat(String format) {
        this.dataFormat = format;
        this.hasDataFormat = true;
        return this;
    }

    /**
     * 고정 너비 설정 (픽셀 단위)
     */
    public ExcelCellStyleConfigurer width(int width) {
        this.cellWidth = width;
        this.autoWidth = false;
        return this;
    }

    /**
     * 자동 너비 설정
     */
    public ExcelCellStyleConfigurer autoWidth() {
        this.autoWidth = true;
        this.cellWidth = -1;
        return this;
    }

    /**
     * POI CellStyle 적용
     */
    public void configure(CellStyleWrapper cellStyle, WorkbookWrapper workbook) {
        applyBackgroundColor(cellStyle);
        applyFont(cellStyle, workbook);
        applyAlignment(cellStyle);
        applyBorder(cellStyle);
        applyNumberFormat(cellStyle, workbook);
    }

    /**
     * RGB 배경색 적용 (XSSF/SXSSF만 지원)
     */
    private void applyBackgroundColor(CellStyleWrapper cellStyle) {
        if (hasBackgroundColorRGB && isXSSF(cellStyle.toPoi())) {
            XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle.toPoi();
            XSSFColor xssfColor = RgbColorHelper.createRgbColor(
                    backgroundColorRed, backgroundColorGreen, backgroundColorBlue
            );
            xssfCellStyle.setFillForegroundColor(xssfColor);
            xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
    }

    /**
     * 폰트 및 RGB 폰트 색상 적용
     */
    private void applyFont(CellStyleWrapper cellStyle, WorkbookWrapper workbook) {
        if (hasFont || hasFontColorRGB) {
            Font font = workbook.createFont();

            if (fontName != null) font.setFontName(fontName);
            if (fontSize != null) font.setFontHeightInPoints(fontSize.shortValue());

            if (fontStyle != null) {
                font.setBold(fontStyle.isBold());
                font.setItalic(fontStyle.isItalic());
                font.setUnderline(fontStyle.isUnderline() ? Font.U_SINGLE : Font.U_NONE);
            }

            if (hasFontColorRGB && font instanceof XSSFFont) {
                XSSFFont xssfFont = (XSSFFont) font;
                XSSFColor xssfColor = RgbColorHelper.createRgbColor(
                        fontColorRed, fontColorGreen, fontColorBlue
                );
                xssfFont.setColor(xssfColor);
            }

            cellStyle.setFont(font);
        }
    }

    /**
     * 정렬 설정 적용
     */
    private void applyAlignment(CellStyleWrapper cellStyle) {
        if (hasAlignment) {
            if (horizontalAlignment != null) cellStyle.setAlignment(horizontalAlignment.toPoi());
            if (verticalAlignment != null) cellStyle.setVerticalAlignment(verticalAlignment.toPoi());
        }
    }

    /**
     * 테두리 설정 적용
     */
    private void applyBorder(CellStyleWrapper cellStyle) {
        if (hasBorder) {
            cellStyle.setBorderTop(borderStyle.toPoi());
            cellStyle.setBorderRight(borderStyle.toPoi());
            cellStyle.setBorderBottom(borderStyle.toPoi());
            cellStyle.setBorderLeft(borderStyle.toPoi());
        }
    }

    /**
     * 데이터 포맷 설정 적용
     */
    private void applyNumberFormat(CellStyleWrapper cellStyle, WorkbookWrapper workbook) {
        if (hasDataFormat) {
            DataFormat format = workbook.createDataFormat();
            cellStyle.setDataFormat(format.getFormat(this.dataFormat));
        }
    }

    /**
     * XSSF 타입 체크
     */
    private boolean isXSSF(CellStyle cellStyle) {
        return cellStyle instanceof XSSFCellStyle;
    }

    /**
     * 설정된 너비 값 반환 (픽셀 단위, 자동=-1, 기본=100)
     */
    public int getColumnWidth() {
        if (autoWidth) {
            return -1;
        }
        return cellWidth != null ? cellWidth : 100;
    }

    /**
     * 데이터 포맷 반환 (기본="General")
     */
    public String getDataFormat() {
        return hasDataFormat ? dataFormat : "General";
    }

    /**
     * 흰색 배경 (255, 255, 255)
     *
     * @deprecated Use {@link #backgroundColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer whiteBackground() {
        return backgroundColor(255, 255, 255);
    }

    /**
     * 회색 배경 (192, 192, 192)
     *
     * @deprecated Use {@link #backgroundColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer greyBackground() {
        return backgroundColor(192, 192, 192);
    }

    /**
     * 라벤더 배경 (230, 230, 250)
     *
     * @deprecated Use {@link #backgroundColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer lavenderBackground() {
        return backgroundColor(230, 230, 250);
    }

    /**
     * 연한 노란색 배경 (255, 255, 224)
     *
     * @deprecated Use {@link #backgroundColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer lightYellowBackground() {
        return backgroundColor(255, 255, 224);
    }

    /**
     * 연한 초록색 배경 (144, 238, 144)
     *
     * @deprecated Use {@link #backgroundColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer lightGreenBackground() {
        return backgroundColor(144, 238, 144);
    }

    /**
     * 분홍색 배경 (255, 192, 203)
     *
     * @deprecated Use {@link #backgroundColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer pinkBackground() {
        return backgroundColor(255, 192, 203);
    }

    /**
     * 검은색 폰트 (0, 0, 0)
     *
     * @deprecated Use {@link #fontColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer blackFont() {
        return fontColor(0, 0, 0);
    }

    /**
     * 흰색 폰트 (255, 255, 255)
     *
     * @deprecated Use {@link #fontColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer whiteFont() {
        return fontColor(255, 255, 255);
    }

    /**
     * 빨간색 폰트 (255, 0, 0)
     *
     * @deprecated Use {@link #fontColor(int, int, int)} instead.
     */
    @Deprecated
    public ExcelCellStyleConfigurer redFont() {
        return fontColor(255, 0, 0);
    }
}