package io.github.takoeats.excelannotator.internal.writer;

import io.github.takoeats.excelannotator.internal.metadata.ColumnMetadata;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.internal.metadata.HeaderMetadata;
import io.github.takoeats.excelannotator.internal.metadata.SheetMetadata;
import io.github.takoeats.excelannotator.internal.writer.builder.MergedHeaderBuilder;
import io.github.takoeats.excelannotator.style.rule.CellContext;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class RowWriter {

    private final CellWriter cellWriter;
    private final MergedHeaderBuilder mergedHeaderBuilder;

    public RowWriter() {
        this.cellWriter = new CellWriter();
        this.mergedHeaderBuilder = new MergedHeaderBuilder(cellWriter);
    }

    <T> void writeDataRow(
            Row row,
            T item,
            int dataRowIndex,
            ExcelMetadata<T> metadata,
            StyleCacheManager styleCacheManager) {

        try (CellContext cellContext = CellContext.acquire()) {
            cellWriter.writeCells(row, item, dataRowIndex, metadata, cellContext, styleCacheManager);
        }
    }

    <T> void createHeaderRow(
            Sheet sheet,
            ExcelMetadata<T> metadata,
            StyleCacheManager styleCacheManager) {

        createHeaderRow(sheet, metadata, metadata, metadata, styleCacheManager);
    }

    private void createHeaderRow(
            Sheet sheet,
            SheetMetadata sheetMetadata,
            ColumnMetadata columnMetadata,
            HeaderMetadata headerMetadata,
            StyleCacheManager styleCacheManager) {

        if (!sheetMetadata.hasHeader()) {
            return;
        }

        if (sheetMetadata.hasAnyMergeHeader()) {
            mergedHeaderBuilder.buildTwoRowHeaders(sheet, columnMetadata, headerMetadata, styleCacheManager);
        } else {
            createSingleRowHeader(sheet, columnMetadata, headerMetadata, styleCacheManager);
        }
    }

    private void createSingleRowHeader(
            Sheet sheet,
            ColumnMetadata columnMetadata,
            HeaderMetadata headerMetadata,
            StyleCacheManager styleCacheManager) {

        Row header = sheet.createRow(0);
        for (int i = 0; i < columnMetadata.getHeaders().size(); i++) {
            cellWriter.configureHeaderCell(header, i, columnMetadata, headerMetadata, styleCacheManager);
        }
    }
}
