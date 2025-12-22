package io.github.takoeats.excelannotator.internal.metadata.extractor;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import io.github.takoeats.excelannotator.internal.metadata.SheetInfo;
import io.github.takoeats.excelannotator.internal.metadata.style.ColumnStyleResolver;
import io.github.takoeats.excelannotator.internal.metadata.style.ConditionalStyleParser;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.rule.StyleRule;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ColumnInfoExtractor {

    private ColumnInfoExtractor() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static List<ColumnInfo> extractAll(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        SheetInfo sheetInfo = SheetInfoExtractor.extract(clazz);

        List<ColumnInfo> columnInfos = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 0) {
            return Collections.emptyList();
        }

        for (Field field : fields) {
            ColumnInfo columnInfo = processField(field, sheetInfo);
            if (columnInfo != null) {
                columnInfos.add(columnInfo);
            }
        }

        columnInfos.sort(Comparator.comparingInt(ColumnInfo::getOrder));

        return columnInfos;
    }

    private static ColumnInfo processField(Field field, SheetInfo sheetInfo) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        if (excelColumn != null && !excelColumn.exclude()) {

            String format = excelColumn.format();

            CustomExcelCellStyle headerStyle = ColumnStyleResolver.resolveHeaderStyle(excelColumn, sheetInfo);
            CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, field, sheetInfo);
            int width = ColumnStyleResolver.calculateWidth(excelColumn, columnStyle);

            List<StyleRule> conditionalStyleRules = ConditionalStyleParser.parse(excelColumn.conditionalStyles());

            String sheetName = excelColumn.sheetName();

            return new ColumnInfo(
                    excelColumn.header(),
                    excelColumn.order(),
                    width,
                    format,
                    field,
                    headerStyle,
                    columnStyle,
                    conditionalStyleRules,
                    sheetName
            );
        }
        return null;
    }
}
