package io.github.takoeats.excelannotator.internal.metadata.style;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.internal.metadata.extractor.FieldTypeClassifier;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.StyleCache;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultColumnStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultNumberStyle;

import java.lang.reflect.Field;

public final class ColumnStyleResolver {

    private ColumnStyleResolver() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static CustomExcelCellStyle resolveHeaderStyle(ExcelColumn excelColumn) {
        return StyleCache.getStyleInstance(excelColumn.headerStyle());
    }

    public static CustomExcelCellStyle resolveColumnStyle(ExcelColumn excelColumn, Field field) {
        if (excelColumn.columnStyle().equals(DefaultColumnStyle.class)) {
            if (FieldTypeClassifier.isNumericType(field.getType())) {
                return StyleCache.getStyleInstance(DefaultNumberStyle.class);
            } else {
                return StyleCache.getStyleInstance(DefaultColumnStyle.class);
            }
        } else {
            return StyleCache.getStyleInstance(excelColumn.columnStyle());
        }
    }

    public static int calculateWidth(ExcelColumn excelColumn, CustomExcelCellStyle columnStyle) {
        if (excelColumn.width() != 0) {
            return excelColumn.width();
        }

        if (columnStyle.isAutoWidth()) {
            return -1;
        }

        int styleWidth = columnStyle.getColumnWidth();
        if (styleWidth > 0) {
            return styleWidth;
        }

        return 100;
    }
}
