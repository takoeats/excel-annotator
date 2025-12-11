package com.junho.excel.internal.metadata;

import com.junho.excel.internal.metadata.extractor.ColumnInfoExtractor;
import com.junho.excel.internal.metadata.extractor.FieldValueExtractorFactory;
import com.junho.excel.internal.metadata.extractor.SheetInfoExtractor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public final class MultiSheetMetadataBuilder {

    public MultiSheetMetadataBuilder() {
    }

    public <T> Map<String, ExcelMetadata<T>> build(Class<T> clazz) {
        if (clazz == null) {
            log.warn("Class parameter is null, returning empty metadata map");
            return Collections.emptyMap();
        }

        List<ColumnInfo> allColumnInfos = ColumnInfoExtractor.extractAll(clazz);
        SheetInfo defaultSheetInfo = SheetInfoExtractor.extract(clazz);
        String defaultSheetName = defaultSheetInfo.getName();

        Map<String, List<ColumnInfo>> sheetColumnMap = new LinkedHashMap<>();

        for (ColumnInfo columnInfo : allColumnInfos) {
            String targetSheet = columnInfo.getSheetName() != null && !columnInfo.getSheetName().trim().isEmpty()
                    ? columnInfo.getSheetName()
                    : defaultSheetName;

            sheetColumnMap.computeIfAbsent(targetSheet, k -> new ArrayList<>()).add(columnInfo);
        }

        Map<String, ExcelMetadata<T>> result = new LinkedHashMap<>();

        for (Map.Entry<String, List<ColumnInfo>> entry : sheetColumnMap.entrySet()) {
            String sheetName = entry.getKey();
            List<ColumnInfo> sheetColumns = entry.getValue();

            sheetColumns.sort(Comparator.comparingInt(ColumnInfo::getOrder));

            List<String> headers = getHeaders(sheetColumns);
            List<Integer> columnWidths = getColumnWidths(sheetColumns);
            List<Function<T, Object>> extractors = getExtractors(sheetColumns);
            SheetInfo sheetInfo = SheetInfo.builder()
                    .name(sheetName)
                    .hasHeader(defaultSheetInfo.isHasHeader())
                    .order(defaultSheetInfo.getOrder())
                    .build();

            ExcelMetadata<T> metadata = ExcelMetadata.<T>builder()
                    .headers(headers)
                    .extractors(extractors)
                    .columnWidths(columnWidths)
                    .sheetInfo(sheetInfo)
                    .columnInfos(sheetColumns)
                    .build();

            result.put(sheetName, metadata);
        }

        return result;
    }

    private static List<String> getHeaders(List<ColumnInfo> columnInfos) {
        return columnInfos.stream()
                .map(ColumnInfo::getHeader)
                .collect(Collectors.toList());
    }

    private static List<Integer> getColumnWidths(List<ColumnInfo> columnInfos) {
        return columnInfos.stream()
                .map(ColumnInfo::getWidth)
                .collect(Collectors.toList());
    }

    private static <T> List<Function<T, Object>> getExtractors(List<ColumnInfo> columnInfos) {
        return columnInfos.stream()
                .map(FieldValueExtractorFactory::<T>createExtractor)
                .collect(Collectors.toList());
    }
}
