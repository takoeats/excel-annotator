package io.github.takoeats.excelannotator.style;

import org.apache.poi.ss.usermodel.BorderStyle;

/**
 * Excel 테두리 스타일
 * <p>Apache POI BorderStyle의 Wrapper로, Shaded JAR에서 사용자 API 노출을 위해 사용</p>
 */
public enum BorderType {
    NONE(BorderStyle.NONE),
    THIN(BorderStyle.THIN),
    MEDIUM(BorderStyle.MEDIUM),
    DASHED(BorderStyle.DASHED),
    DOTTED(BorderStyle.DOTTED),
    THICK(BorderStyle.THICK),
    DOUBLE(BorderStyle.DOUBLE),
    HAIR(BorderStyle.HAIR),
    MEDIUM_DASHED(BorderStyle.MEDIUM_DASHED),
    DASH_DOT(BorderStyle.DASH_DOT),
    MEDIUM_DASH_DOT(BorderStyle.MEDIUM_DASH_DOT),
    DASH_DOT_DOT(BorderStyle.DASH_DOT_DOT),
    MEDIUM_DASH_DOT_DOT(BorderStyle.MEDIUM_DASH_DOT_DOT),
    SLANTED_DASH_DOT(BorderStyle.SLANTED_DASH_DOT);

    private final BorderStyle poiStyle;

    BorderType(BorderStyle poiStyle) {
        this.poiStyle = poiStyle;
    }

    /**
     * POI BorderStyle로 변환
     */
    public BorderStyle toPoi() {
        return poiStyle;
    }

    /**
     * POI BorderStyle을 BorderType으로 변환
     *
     * @param poiStyle POI BorderStyle
     * @return BorderType
     */
    public static BorderType fromPoi(BorderStyle poiStyle) {
        if (poiStyle == null) {
            return NONE;
        }
        for (BorderType style : values()) {
            if (style.poiStyle == poiStyle) {
                return style;
            }
        }
        return NONE;
    }
}
