package com.junho.excel.internal.metadata;

import com.junho.excel.internal.metadata.extractor.ColumnInfoExtractor;
import com.junho.excel.internal.metadata.extractor.FieldValueExtractorFactory;
import com.junho.excel.internal.metadata.extractor.SheetInfoExtractor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public final class MetadataAssembler {

    public MetadataAssembler() {
    }

    public <T> ExcelMetadata<T> assemble(Class<T> clazz) {
        if (clazz == null) {
            log.warn("Class parameter is null, returning empty metadata");
            return createEmptyMetadata();
        }

        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(clazz);
        List<String> headers = getHeaders(columnInfos);
        List<Function<T, Object>> extractors = getExtractors(columnInfos);
        List<Integer> columnWidths = getColumnWidths(columnInfos);
        SheetInfo sheetInfo = SheetInfoExtractor.extract(clazz);

        return ExcelMetadata.<T>builder()
                .columnInfos(columnInfos)
                .headers(headers)
                .extractors(extractors)
                .columnWidths(columnWidths)
                .sheetInfo(sheetInfo)
                .build();
    }

    public ExcelMetadata<Map<String, Object>> assembleFromMergedColumns(
            String sheetName,
            Map<Integer, ColumnInfo> mergedColumns,
            boolean hasHeader) {

        List<Integer> sortedOrders = new ArrayList<>(mergedColumns.keySet());
        Collections.sort(sortedOrders);

        List<String> headers = new ArrayList<>();
        List<Integer> columnWidths = new ArrayList<>();
        List<Function<Map<String, Object>, Object>> extractors = new ArrayList<>();
        List<ColumnInfo> columnInfos = new ArrayList<>();

        for (int order : sortedOrders) {
            ColumnInfo colInfo = mergedColumns.get(order);
            headers.add(colInfo.getHeader());
            columnWidths.add(colInfo.getWidth());
            columnInfos.add(colInfo);

            int columnIndex = sortedOrders.indexOf(order);
            extractors.add(row -> row.get(String.valueOf(columnIndex)));
        }

        SheetInfo sheetInfo = SheetInfo.builder()
                .name(sheetName)
                .hasHeader(hasHeader)
                .build();

        return ExcelMetadata.<Map<String, Object>>builder()
                .headers(headers)
                .extractors(extractors)
                .columnWidths(columnWidths)
                .columnInfos(columnInfos)
                .sheetInfo(sheetInfo)
                .build();
    }

    private static <T> ExcelMetadata<T> createEmptyMetadata() {
        return ExcelMetadata.<T>builder()
                .headers(Collections.emptyList())
                .extractors(Collections.emptyList())
                .columnWidths(Collections.emptyList())
                .columnInfos(Collections.emptyList())
                .sheetInfo(SheetInfo.builder().build())
                .build();
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
