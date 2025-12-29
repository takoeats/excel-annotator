package io.github.takoeats.excelannotator.internal.writer;

import io.github.takoeats.excelannotator.internal.metadata.ColumnMetadata;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.internal.metadata.HeaderMetadata;
import io.github.takoeats.excelannotator.internal.metadata.SheetMetadata;
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
            createTwoRowHeaders(sheet, columnMetadata, headerMetadata, styleCacheManager);
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

    private void createTwoRowHeaders(
            Sheet sheet,
            ColumnMetadata columnMetadata,
            HeaderMetadata headerMetadata,
            StyleCacheManager styleCacheManager) {

        Row mergeHeaderRow = sheet.createRow(0);
        Row normalHeaderRow = sheet.createRow(1);

        List<MergeHeaderGroup> mergeGroups = buildMergeHeaderGroups(columnMetadata, headerMetadata);

        for (MergeHeaderGroup group : mergeGroups) {
            if (group.isMerged) {
                writeMergedHeaderGroup(sheet, mergeHeaderRow, normalHeaderRow, group,
                                      columnMetadata, headerMetadata, styleCacheManager);
            } else {
                writeNonMergedHeaderGroup(sheet, mergeHeaderRow, normalHeaderRow, group.startCol,
                                         columnMetadata, headerMetadata, styleCacheManager);
            }
        }
    }

    private void writeMergedHeaderGroup(
            Sheet sheet,
            Row mergeHeaderRow,
            Row normalHeaderRow,
            MergeHeaderGroup group,
            ColumnMetadata columnMetadata,
            HeaderMetadata headerMetadata,
            StyleCacheManager styleCacheManager) {

        cellWriter.configureMergeHeaderCell(mergeHeaderRow, group.startCol, group.mergeHeaderName,
                                           headerMetadata, styleCacheManager);

        if (group.startCol != group.endCol) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, group.startCol, group.endCol));
        }

        for (int col = group.startCol; col <= group.endCol; col++) {
            cellWriter.configureHeaderCell(normalHeaderRow, col, columnMetadata, headerMetadata, styleCacheManager);
        }
    }

    private void writeNonMergedHeaderGroup(
            Sheet sheet,
            Row mergeHeaderRow,
            Row normalHeaderRow,
            int columnIndex,
            ColumnMetadata columnMetadata,
            HeaderMetadata headerMetadata,
            StyleCacheManager styleCacheManager) {

        cellWriter.configureHeaderCell(mergeHeaderRow, columnIndex, columnMetadata, headerMetadata, styleCacheManager);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, columnIndex, columnIndex));
        cellWriter.configureHeaderCell(normalHeaderRow, columnIndex, columnMetadata, headerMetadata, styleCacheManager);
    }

    private List<MergeHeaderGroup> buildMergeHeaderGroups(
            ColumnMetadata columnMetadata,
            HeaderMetadata headerMetadata) {

        List<MergeHeaderGroup> groups = new ArrayList<>();
        int columnCount = columnMetadata.getColumnCount();

        int i = 0;
        while (i < columnCount) {
            String mergeHeader = headerMetadata.getMergeHeaderAt(i);

            if (mergeHeader != null && !mergeHeader.isEmpty()) {
                int startCol = i;
                int endCol = i;

                while (endCol + 1 < columnCount && mergeHeader.equals(headerMetadata.getMergeHeaderAt(endCol + 1))) {
                    endCol++;
                }

                groups.add(new MergeHeaderGroup(true, mergeHeader, startCol, endCol));
                i = endCol + 1;
            } else {
                groups.add(new MergeHeaderGroup(false, "", i, i));
                i++;
            }
        }

        return groups;
    }

    private static final class MergeHeaderGroup {
        final boolean isMerged;
        final String mergeHeaderName;
        final int startCol;
        final int endCol;

        MergeHeaderGroup(boolean isMerged, String mergeHeaderName, int startCol, int endCol) {
            this.isMerged = isMerged;
            this.mergeHeaderName = mergeHeaderName;
            this.startCol = startCol;
            this.endCol = endCol;
        }
    }
}
