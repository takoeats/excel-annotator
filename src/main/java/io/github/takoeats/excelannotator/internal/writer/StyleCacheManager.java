package io.github.takoeats.excelannotator.internal.writer;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.StyleCache;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

public final class StyleCacheManager {

    private final Map<Class<? extends CustomExcelCellStyle>, CellStyle> baseStyleCache = new HashMap<>();
    private final Map<String, CellStyle> formatStyleCache = new HashMap<>();
    private final Workbook workbook;

    StyleCacheManager(Workbook workbook) {
        this.workbook = workbook;
    }

    public CellStyle getOrCreateStyle(
            Class<? extends CustomExcelCellStyle> styleClass,
            String format) {

        if (format != null) {
            String cacheKey = buildCacheKey(styleClass, format);
            return formatStyleCache.computeIfAbsent(cacheKey,
                    k -> createStyleWithFormat(styleClass, format));
        }

        if (styleClass != null) {
            return baseStyleCache.computeIfAbsent(styleClass,
                    k -> StyleCache.createPOIStyle(workbook, styleClass));
        }

        return workbook.createCellStyle();
    }

    private String buildCacheKey(
            Class<? extends CustomExcelCellStyle> styleClass,
            String format) {
        String styleClassName = styleClass != null ? styleClass.getName() : "null";
        return styleClassName + ":" + format;
    }

    private CellStyle createStyleWithFormat(
            Class<? extends CustomExcelCellStyle> styleClass,
            String format) {
        CellStyle poiStyle = workbook.createCellStyle();

        if (styleClass != null) {
            CellStyle baseStyle = baseStyleCache.computeIfAbsent(styleClass,
                    k -> StyleCache.createPOIStyle(workbook, styleClass));
            poiStyle.cloneStyleFrom(baseStyle);
        }

        poiStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
        return poiStyle;
    }
}
