package com.junho.excel.internal;

import com.junho.excel.internal.metadata.*;
import com.junho.excel.internal.metadata.extractor.SheetInfoExtractor;

import java.util.Map;

public final class ExcelMetadataFactory {
    private static final MultiSheetMetadataBuilder MULTI_SHEET_BUILDER = new MultiSheetMetadataBuilder();

    private ExcelMetadataFactory() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

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
