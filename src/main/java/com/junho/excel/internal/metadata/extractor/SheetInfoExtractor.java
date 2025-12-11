package com.junho.excel.internal.metadata.extractor;

import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.metadata.SheetInfo;

public final class SheetInfoExtractor {

    private SheetInfoExtractor() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static SheetInfo extract(Class<?> clazz) {
        ExcelSheet excelSheet = clazz.getAnnotation(ExcelSheet.class);
        if (excelSheet != null) {
            return SheetInfo.builder()
                    .name(excelSheet.value())
                    .hasHeader(excelSheet.hasHeader())
                    .order(excelSheet.order())
                    .build();
        }
        throw new ExcelExporterException(ErrorCode.METADATA_EXTRACTION_FAILED,
                "@ExcelSheet 어노테이션이 없는 DTO: " + clazz.getName());
    }
}
