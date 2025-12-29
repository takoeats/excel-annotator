package io.github.takoeats.excelannotator.style.internal.wrapper;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

/**
 * Apache POI CellStyle Wrapper
 * <p>Shaded JAR에서 POI 타입을 public API에 노출하지 않기 위한 Wrapper</p>
 * <p>내부 구현용이며, 사용자는 이 클래스를 직접 사용하지 않음</p>
 */
public final class CellStyleWrapper {

    private final CellStyle poiCellStyle;

    private CellStyleWrapper(CellStyle poiCellStyle) {
        this.poiCellStyle = poiCellStyle;
    }

    /**
     * POI CellStyle을 Wrapper로 감싸기
     */
    public static CellStyleWrapper wrap(CellStyle cellStyle) {
        return new CellStyleWrapper(cellStyle);
    }

    /**
     * 내부 POI CellStyle 반환 (internal use only)
     */
    public CellStyle toPoi() {
        return poiCellStyle;
    }

    public void setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment alignment) {
        poiCellStyle.setAlignment(alignment);
    }

    public void setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment alignment) {
        poiCellStyle.setVerticalAlignment(alignment);
    }

    public void setBorderTop(org.apache.poi.ss.usermodel.BorderStyle border) {
        poiCellStyle.setBorderTop(border);
    }

    public void setBorderRight(org.apache.poi.ss.usermodel.BorderStyle border) {
        poiCellStyle.setBorderRight(border);
    }

    public void setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle border) {
        poiCellStyle.setBorderBottom(border);
    }

    public void setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle border) {
        poiCellStyle.setBorderLeft(border);
    }

    public void setDataFormat(short format) {
        poiCellStyle.setDataFormat(format);
    }

    public void setFont(Font font) {
        poiCellStyle.setFont(font);
    }

    public void setFillForegroundColor(short color) {
        poiCellStyle.setFillForegroundColor(color);
    }

    public void setFillPattern(org.apache.poi.ss.usermodel.FillPatternType pattern) {
        poiCellStyle.setFillPattern(pattern);
    }
}
