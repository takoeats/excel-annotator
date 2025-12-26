package io.github.takoeats.excelannotator.internal.writer;

import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.rule.CellContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

public final class RowWriter {

    private final CellWriter cellWriter;

    public RowWriter() {
        this.cellWriter = new CellWriter();
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

        if (!metadata.hasHeader()) {
            return;
        }

        if (metadata.hasAnyMergeHeader()) {
            createTwoRowHeaders(sheet, metadata, styleCacheManager);
        } else {
            createSingleRowHeader(sheet, metadata, styleCacheManager);
        }
    }

    private <T> void createSingleRowHeader(
            Sheet sheet,
            ExcelMetadata<T> metadata,
            StyleCacheManager styleCacheManager) {

        Row header = sheet.createRow(0);
        for (int i = 0; i < metadata.getHeaders().size(); i++) {
            cellWriter.configureHeaderCell(header, i, metadata, styleCacheManager);
        }
    }

    private <T> void createTwoRowHeaders(
            Sheet sheet,
            ExcelMetadata<T> metadata,
            StyleCacheManager styleCacheManager) {

        Row mergeHeaderRow = sheet.createRow(0);
        Row normalHeaderRow = sheet.createRow(1);

        List<MergeHeaderGroup> mergeGroups = buildMergeHeaderGroups(metadata);

        for (MergeHeaderGroup group : mergeGroups) {
            if (group.isMerged) {
                Cell mergeCell = mergeHeaderRow.createCell(group.startCol);
                mergeCell.setCellValue(group.mergeHeaderName);

                CustomExcelCellStyle mergeHeaderStyle = metadata.getMergeHeaderStyleAt(group.startCol);
                Class<? extends CustomExcelCellStyle> styleClass = mergeHeaderStyle != null
                        ? mergeHeaderStyle.getClass()
                        : null;
                CellStyle poiStyle = styleCacheManager.getOrCreateStyle(styleClass, null);
                mergeCell.setCellStyle(poiStyle);

                if (group.startCol != group.endCol) {
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, group.startCol, group.endCol));
                }

                for (int col = group.startCol; col <= group.endCol; col++) {
                    cellWriter.configureHeaderCell(normalHeaderRow, col, metadata, styleCacheManager);
                }
            } else {
                int col = group.startCol;
                Cell mergeCell = mergeHeaderRow.createCell(col);
                mergeCell.setCellValue(metadata.getHeaders().get(col));

                CustomExcelCellStyle headerStyle = metadata.getHeaderStyleAt(col);
                Class<? extends CustomExcelCellStyle> styleClass = headerStyle != null
                        ? headerStyle.getClass()
                        : null;
                CellStyle poiStyle = styleCacheManager.getOrCreateStyle(styleClass, null);
                mergeCell.setCellStyle(poiStyle);

                sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col));

                Cell normalCell = normalHeaderRow.createCell(col);
                normalCell.setCellValue(metadata.getHeaders().get(col));
                normalCell.setCellStyle(poiStyle);
            }
        }
    }

    private <T> List<MergeHeaderGroup> buildMergeHeaderGroups(ExcelMetadata<T> metadata) {
        List<MergeHeaderGroup> groups = new ArrayList<>();
        int columnCount = metadata.getColumnCount();

        int i = 0;
        while (i < columnCount) {
            String mergeHeader = metadata.getMergeHeaderAt(i);

            if (mergeHeader != null && !mergeHeader.isEmpty()) {
                int startCol = i;
                int endCol = i;

                while (endCol + 1 < columnCount && mergeHeader.equals(metadata.getMergeHeaderAt(endCol + 1))) {
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
