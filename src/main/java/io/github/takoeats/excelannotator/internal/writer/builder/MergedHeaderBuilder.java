package io.github.takoeats.excelannotator.internal.writer.builder;

import io.github.takoeats.excelannotator.internal.metadata.ColumnMetadata;
import io.github.takoeats.excelannotator.internal.metadata.HeaderMetadata;
import io.github.takoeats.excelannotator.internal.writer.CellWriter;
import io.github.takoeats.excelannotator.internal.writer.StyleCacheManager;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

public final class MergedHeaderBuilder {

    private final CellWriter cellWriter;

    public MergedHeaderBuilder(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    public void buildTwoRowHeaders(
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

        configureMergeHeaderCell(mergeHeaderRow, group.startCol, group.mergeHeaderName,
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

    private void configureMergeHeaderCell(
            Row row,
            int columnIndex,
            String headerText,
            HeaderMetadata headerMetadata,
            StyleCacheManager styleCacheManager) {

        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(headerText);

        CustomExcelCellStyle mergeHeaderStyle = headerMetadata.getMergeHeaderStyleAt(columnIndex);
        Class<? extends CustomExcelCellStyle> styleClass = mergeHeaderStyle != null
                ? mergeHeaderStyle.getClass()
                : null;

        CellStyle poiStyle = styleCacheManager.getOrCreateStyle(styleClass, null);
        cell.setCellStyle(poiStyle);
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
