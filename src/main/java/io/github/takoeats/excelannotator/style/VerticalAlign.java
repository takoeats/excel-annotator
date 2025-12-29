package io.github.takoeats.excelannotator.style;

import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Excel 세로 정렬
 * <p>Apache POI VerticalAlignment의 Wrapper로, Shaded JAR에서 사용자 API 노출을 위해 사용</p>
 */
public enum VerticalAlign {
    TOP(VerticalAlignment.TOP),
    CENTER(VerticalAlignment.CENTER),
    BOTTOM(VerticalAlignment.BOTTOM),
    JUSTIFY(VerticalAlignment.JUSTIFY),
    DISTRIBUTED(VerticalAlignment.DISTRIBUTED);

    private final VerticalAlignment poiAlignment;

    VerticalAlign(VerticalAlignment poiAlignment) {
        this.poiAlignment = poiAlignment;
    }

    /**
     * POI VerticalAlignment로 변환
     */
    public VerticalAlignment toPoi() {
        return poiAlignment;
    }

    /**
     * POI VerticalAlignment을 VerticalAlign으로 변환
     *
     * @param poiAlignment POI VerticalAlignment
     * @return VerticalAlign
     */
    public static VerticalAlign fromPoi(VerticalAlignment poiAlignment) {
        if (poiAlignment == null) {
            return TOP;
        }
        for (VerticalAlign alignment : values()) {
            if (alignment.poiAlignment == poiAlignment) {
                return alignment;
            }
        }
        return TOP;
    }
}
