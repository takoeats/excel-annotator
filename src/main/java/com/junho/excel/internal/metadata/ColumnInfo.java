package com.junho.excel.internal.metadata;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.rule.StyleRule;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@Getter
public final class ColumnInfo {
    private final String header;
    private final int order;
    private final int width;
    private final String format;
    private final Field field;
    private final CustomExcelCellStyle headerStyle;
    private final CustomExcelCellStyle columnStyle;
    private final List<StyleRule> conditionalStyleRules;
    private final String sheetName;

    public ColumnInfo(String header, int order, int width, String format, Field field,
                      CustomExcelCellStyle headerStyle, CustomExcelCellStyle columnStyle,
                      List<StyleRule> conditionalStyleRules, String sheetName) {
        this.header = header;
        this.order = order;
        this.width = width;
        this.format = format;
        this.field = field;
        this.headerStyle = headerStyle;
        this.columnStyle = columnStyle;
        this.conditionalStyleRules = conditionalStyleRules != null
                ? conditionalStyleRules
                : Collections.emptyList();
        this.sheetName = sheetName;
    }
}
