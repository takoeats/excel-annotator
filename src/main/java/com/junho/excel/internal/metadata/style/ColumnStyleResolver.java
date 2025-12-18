package com.junho.excel.internal.metadata.style;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.internal.metadata.extractor.FieldTypeClassifier;
import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.StyleCache;
import com.junho.excel.style.defaultstyle.DefaultColumnStyle;
import com.junho.excel.style.defaultstyle.DefaultNumberStyle;

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
        if (excelColumn.width() != 100) {
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
