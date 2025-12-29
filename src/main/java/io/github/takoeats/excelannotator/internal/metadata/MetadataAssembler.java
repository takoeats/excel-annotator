package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.internal.metadata.extractor.ColumnInfoExtractor;
import io.github.takoeats.excelannotator.internal.metadata.extractor.SheetInfoExtractor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MetadataAssembler {


    public static <T> ExcelMetadata<T> assemble(Class<T> clazz) {
        if (clazz == null) {
            log.warn("Class parameter is null, returning empty metadata");
            return createEmptyMetadata();
        }

        List<ColumnInfo> columnInfos = ColumnInfoExtractor.extractAll(clazz);
        List<String> headers = ColumnInfoMapper.mapToHeaders(columnInfos);
        List<Function<T, Object>> extractors = ColumnInfoMapper.mapToExtractors(columnInfos);
        List<Integer> columnWidths = ColumnInfoMapper.mapToColumnWidths(columnInfos);
        SheetInfo sheetInfo = SheetInfoExtractor.extract(clazz);

        return ExcelMetadata.<T>builder()
                .columnInfos(columnInfos)
                .headers(headers)
                .extractors(extractors)
                .columnWidths(columnWidths)
                .sheetInfo(sheetInfo)
                .build();
    }

    public static ExcelMetadata<Map<String, Object>> assembleFromMergedColumns(
            String sheetName,
            Map<Integer, ColumnInfo> mergedColumns,
            boolean hasHeader) {

        List<Integer> sortedOrders = new ArrayList<>(mergedColumns.keySet());
        Collections.sort(sortedOrders);

        List<ColumnInfo> columnInfos = new ArrayList<>();
        List<Function<Map<String, Object>, Object>> extractors = new ArrayList<>();

        for (int order : sortedOrders) {
            ColumnInfo colInfo = mergedColumns.get(order);
            columnInfos.add(colInfo);

            int columnIndex = sortedOrders.indexOf(order);
            extractors.add(row -> row.get(String.valueOf(columnIndex)));
        }

        List<String> headers = ColumnInfoMapper.mapToHeaders(columnInfos);
        List<Integer> columnWidths = ColumnInfoMapper.mapToColumnWidths(columnInfos);

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
}
