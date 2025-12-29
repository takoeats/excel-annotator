package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.internal.metadata.extractor.ColumnInfoExtractor;
import io.github.takoeats.excelannotator.internal.metadata.extractor.SheetInfoExtractor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;

@Slf4j
public final class MultiSheetMetadataBuilder {

    public MultiSheetMetadataBuilder() {
        // for factory
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

            List<String> headers = ColumnInfoMapper.mapToHeaders(sheetColumns);
            List<Integer> columnWidths = ColumnInfoMapper.mapToColumnWidths(sheetColumns);
            List<Function<T, Object>> extractors = ColumnInfoMapper.mapToExtractors(sheetColumns);
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
}
