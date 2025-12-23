package io.github.takoeats.excelannotator.internal.metadata.style;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.internal.metadata.SheetInfo;
import io.github.takoeats.excelannotator.internal.metadata.extractor.FieldTypeClassifier;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.StyleCache;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultColumnStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultHeaderStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultNumberStyle;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ColumnStyleResolver {


    public static CustomExcelCellStyle resolveHeaderStyle(ExcelColumn excelColumn, SheetInfo sheetInfo) {
        Class<? extends CustomExcelCellStyle> styleClass = excelColumn.headerStyle();

        if (!styleClass.equals(DefaultHeaderStyle.class)) {
            return StyleCache.getStyleInstance(styleClass);
        }

        if (sheetInfo.getDefaultHeaderStyle() != null &&
                !sheetInfo.getDefaultHeaderStyle().equals(DefaultHeaderStyle.class)) {
            return StyleCache.getStyleInstance(sheetInfo.getDefaultHeaderStyle());
        }

        return StyleCache.getStyleInstance(DefaultHeaderStyle.class);
    }

    public static CustomExcelCellStyle resolveColumnStyle(ExcelColumn excelColumn, Field field, SheetInfo sheetInfo) {
        Class<? extends CustomExcelCellStyle> styleClass = excelColumn.columnStyle();

        if (!styleClass.equals(DefaultColumnStyle.class)) {
            return StyleCache.getStyleInstance(styleClass);
        }

        if (sheetInfo.getDefaultColumnStyle() != null &&
                !sheetInfo.getDefaultColumnStyle().equals(DefaultColumnStyle.class)) {
            return StyleCache.getStyleInstance(sheetInfo.getDefaultColumnStyle());
        }

        if (FieldTypeClassifier.isNumericType(field.getType())) {
            return StyleCache.getStyleInstance(DefaultNumberStyle.class);
        }

        return StyleCache.getStyleInstance(DefaultColumnStyle.class);
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

    public static CustomExcelCellStyle resolveHeaderStyleFromSheetInfo(SheetInfo sheetInfo) {
        if (sheetInfo.getDefaultHeaderStyle() != null &&
                !sheetInfo.getDefaultHeaderStyle().equals(DefaultHeaderStyle.class)) {
            return StyleCache.getStyleInstance(sheetInfo.getDefaultHeaderStyle());
        }

        return StyleCache.getStyleInstance(DefaultHeaderStyle.class);
    }

    public static CustomExcelCellStyle resolveColumnStyleFromFieldType(Field field, SheetInfo sheetInfo) {
        if (sheetInfo.getDefaultColumnStyle() != null &&
                !sheetInfo.getDefaultColumnStyle().equals(DefaultColumnStyle.class)) {
            return StyleCache.getStyleInstance(sheetInfo.getDefaultColumnStyle());
        }

        if (FieldTypeClassifier.isNumericType(field.getType())) {
            return StyleCache.getStyleInstance(DefaultNumberStyle.class);
        }

        return StyleCache.getStyleInstance(DefaultColumnStyle.class);
    }

    public static int calculateWidthFromStyle(CustomExcelCellStyle columnStyle) {
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
