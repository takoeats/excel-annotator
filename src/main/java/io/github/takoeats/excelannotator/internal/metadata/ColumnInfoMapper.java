package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.internal.metadata.extractor.FieldValueExtractorFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ColumnInfoMapper {

    static List<String> mapToHeaders(List<ColumnInfo> columnInfos) {
        return columnInfos.stream()
                .map(ColumnInfo::getHeader)
                .collect(Collectors.toList());
    }

    static List<Integer> mapToColumnWidths(List<ColumnInfo> columnInfos) {
        return columnInfos.stream()
                .map(ColumnInfo::getWidth)
                .collect(Collectors.toList());
    }

    static <T> List<Function<T, Object>> mapToExtractors(List<ColumnInfo> columnInfos) {
        return columnInfos.stream()
                .map(FieldValueExtractorFactory::<T>createExtractor)
                .collect(Collectors.toList());
    }
}
