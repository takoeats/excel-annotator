package io.github.takoeats.excelannotator.style;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * Excel 가로 정렬
 * <p>Apache POI HorizontalAlignment의 Wrapper로, Shaded JAR에서 사용자 API 노출을 위해 사용</p>
 */
public enum HorizontalAlign {
    GENERAL(HorizontalAlignment.GENERAL),
    LEFT(HorizontalAlignment.LEFT),
    CENTER(HorizontalAlignment.CENTER),
    RIGHT(HorizontalAlignment.RIGHT),
    FILL(HorizontalAlignment.FILL),
    JUSTIFY(HorizontalAlignment.JUSTIFY),
    CENTER_SELECTION(HorizontalAlignment.CENTER_SELECTION),
    DISTRIBUTED(HorizontalAlignment.DISTRIBUTED);

    private final HorizontalAlignment poiAlignment;

    HorizontalAlign(HorizontalAlignment poiAlignment) {
        this.poiAlignment = poiAlignment;
    }

    /**
     * POI HorizontalAlignment로 변환
     */
    public HorizontalAlignment toPoi() {
        return poiAlignment;
    }

    /**
     * POI HorizontalAlignment을 HorizontalAlign으로 변환
     *
     * @param poiAlignment POI HorizontalAlignment
     * @return HorizontalAlign
     */
    public static HorizontalAlign fromPoi(HorizontalAlignment poiAlignment) {
        if (poiAlignment == null) {
            return GENERAL;
        }
        for (HorizontalAlign alignment : values()) {
            if (alignment.poiAlignment == poiAlignment) {
                return alignment;
            }
        }
        return GENERAL;
    }
}
