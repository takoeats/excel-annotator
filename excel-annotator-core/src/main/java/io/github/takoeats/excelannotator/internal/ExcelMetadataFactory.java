package io.github.takoeats.excelannotator.internal;

import io.github.takoeats.excelannotator.internal.metadata.*;
import io.github.takoeats.excelannotator.internal.metadata.extractor.SheetInfoExtractor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelMetadataFactory {
    private static final MultiSheetMetadataBuilder MULTI_SHEET_BUILDER = new MultiSheetMetadataBuilder();


    public static <T> ExcelMetadata<T> extractExcelMetadata(Class<T> clazz) {
        return MetadataAssembler.assemble(clazz);
    }

    public static <T> Map<String, ExcelMetadata<T>> extractMultiSheetMetadata(Class<T> clazz) {
        return MULTI_SHEET_BUILDER.build(clazz);
    }

    public static ExcelMetadata<Map<String, Object>> createFromMergedColumns(
            String sheetName,
            Map<Integer, ColumnInfo> mergedColumns,
            boolean hasHeader) {
        return MetadataAssembler.assembleFromMergedColumns(sheetName, mergedColumns, hasHeader);
    }

    public static SheetInfo extractSheetInfo(Class<?> clazz) {
        return SheetInfoExtractor.extract(clazz);
    }
}
