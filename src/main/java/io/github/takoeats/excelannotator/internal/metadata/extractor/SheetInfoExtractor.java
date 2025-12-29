package io.github.takoeats.excelannotator.internal.metadata.extractor;

import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.metadata.SheetInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SheetInfoExtractor {


    public static SheetInfo extract(Class<?> clazz) {
        ExcelSheet excelSheet = clazz.getAnnotation(ExcelSheet.class);
        if (excelSheet != null) {
            return SheetInfo.builder()
                    .name(excelSheet.value())
                    .hasHeader(excelSheet.hasHeader())
                    .order(excelSheet.order())
                    .defaultHeaderStyle(excelSheet.defaultHeaderStyle())
                    .defaultColumnStyle(excelSheet.defaultColumnStyle())
                    .autoColumn(excelSheet.autoColumn())
                    .build();
        }
        throw new ExcelExporterException(ErrorCode.METADATA_EXTRACTION_FAILED,
                "@ExcelSheet 어노테이션이 없는 DTO: " + clazz.getName());
    }
}
