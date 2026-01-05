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
     * Internal use only - converts to POI type for internal implementation.
     * This method is package-private to prevent exposure in shaded JAR environment.
     */
    VerticalAlignment toPoi() {
        return poiAlignment;
    }

    /**
     * Internal use only - converts from POI type (used by deprecated methods).
     * This method is package-private to prevent exposure in shaded JAR environment.
     *
     * @param poiAlignment POI VerticalAlignment
     * @return VerticalAlign
     */
    static VerticalAlign fromPoi(VerticalAlignment poiAlignment) {
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
