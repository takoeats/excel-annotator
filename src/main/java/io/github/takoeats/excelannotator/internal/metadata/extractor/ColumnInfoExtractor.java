package io.github.takoeats.excelannotator.internal.metadata.extractor;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import io.github.takoeats.excelannotator.internal.metadata.SheetInfo;
import io.github.takoeats.excelannotator.internal.metadata.style.ColumnStyleResolver;
import io.github.takoeats.excelannotator.internal.metadata.style.ConditionalStyleParser;
import io.github.takoeats.excelannotator.internal.metadata.validator.MergeHeaderValidator;
import io.github.takoeats.excelannotator.masking.Masking;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.rule.StyleRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ColumnInfoExtractor {

    public static List<ColumnInfo> extractAll(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        SheetInfo sheetInfo = SheetInfoExtractor.extract(clazz);

        List<ColumnInfo> columnInfos = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();

        if (sheetInfo.isAutoColumn()) {
            int autoOrder = 1;
            for (Field field : fields) {
                if (shouldSkipField(field)) {
                    continue;
                }
                ColumnInfo columnInfo = processFieldWithAutoColumn(field, sheetInfo, autoOrder);
                if (columnInfo != null) {
                    columnInfos.add(columnInfo);
                    autoOrder++;
                }
            }
        } else {
            for (Field field : fields) {
                ColumnInfo columnInfo = processField(field, sheetInfo);
                if (columnInfo != null) {
                    columnInfos.add(columnInfo);
                }
            }
        }

        if (columnInfos.isEmpty()) {
            throw new ExcelExporterException(ErrorCode.NO_EXCEL_COLUMNS,
                    String.format("클래스 '%s'에 @ExcelColumn 어노테이션이 적용된 필드가 없습니다.", clazz.getName()));
        }

        columnInfos.sort(Comparator.comparingInt(ColumnInfo::getOrder));

        MergeHeaderValidator.validateOrderContinuity(columnInfos);

        return columnInfos;
    }

    private static ColumnInfo processField(Field field, SheetInfo sheetInfo) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        if (excelColumn != null && !excelColumn.exclude()) {

            CustomExcelCellStyle headerStyle = ColumnStyleResolver.resolveHeaderStyle(excelColumn, sheetInfo);
            CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyle(excelColumn, field, sheetInfo);
            CustomExcelCellStyle mergeHeaderStyle = ColumnStyleResolver.resolveMergeHeaderStyle(excelColumn, sheetInfo);
            int width = ColumnStyleResolver.calculateWidth(excelColumn, columnStyle);
            List<StyleRule> conditionalStyleRules = ConditionalStyleParser.parse(excelColumn.conditionalStyles());

            return ColumnInfo.builder()
                    .header(excelColumn.header())
                    .order(excelColumn.order())
                    .width(width)
                    .format(excelColumn.format())
                    .field(field)
                    .headerStyle(headerStyle)
                    .columnStyle(columnStyle)
                    .conditionalStyleRules(conditionalStyleRules)
                    .sheetName(excelColumn.sheetName())
                    .masking(excelColumn.masking())
                    .mergeHeader(excelColumn.mergeHeader())
                    .mergeHeaderStyle(mergeHeaderStyle)
                    .build();
        }
        return null;
    }

    private static boolean shouldSkipField(Field field) {
        return field.isSynthetic() || field.getName().startsWith("$");
    }

    private static ColumnInfo processFieldWithAutoColumn(Field field, SheetInfo sheetInfo, int autoOrder) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);

        if (excelColumn != null && excelColumn.exclude()) {
            return null;
        }

        if (excelColumn != null) {
            return processField(field, sheetInfo);
        }

        CustomExcelCellStyle headerStyle = ColumnStyleResolver.resolveHeaderStyleFromSheetInfo(sheetInfo);
        CustomExcelCellStyle columnStyle = ColumnStyleResolver.resolveColumnStyleFromFieldType(field, sheetInfo);
        int width = ColumnStyleResolver.calculateWidthFromStyle(columnStyle);

        return ColumnInfo.builder()
                .header(field.getName())
                .order(autoOrder)
                .width(width)
                .format("")
                .field(field)
                .headerStyle(headerStyle)
                .columnStyle(columnStyle)
                .conditionalStyleRules(Collections.emptyList())
                .sheetName("")
                .masking(Masking.NONE)
                .mergeHeader("")
                .mergeHeaderStyle(headerStyle)
                .build();
    }
}
