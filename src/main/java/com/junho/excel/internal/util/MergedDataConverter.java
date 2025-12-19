package com.junho.excel.internal.util;

import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.ExcelMetadataCache;
import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.SheetDataEntry;
import com.junho.excel.internal.metadata.ColumnInfo;
import com.junho.excel.internal.metadata.ExcelMetadata;
import lombok.Getter;

import java.util.*;

public final class MergedDataConverter {

    private MergedDataConverter() {
    }

    @Getter
    public static final class MergedDataResult {
        private final ExcelMetadata<Map<String, Object>> metadata;
        private final Iterator<Map<String, Object>> dataIterator;

        public MergedDataResult(ExcelMetadata<Map<String, Object>> metadata,
                                Iterator<Map<String, Object>> dataIterator) {
            this.metadata = metadata;
            this.dataIterator = dataIterator;
        }

    }

    public static MergedDataResult convertToMergedData(String sheetName,
                                                       List<SheetDataEntry> dataEntries, boolean isLinkedHashMap) {
        boolean hasOrderConflict = hasOrderConflict(dataEntries);
        validateOrderConflictOrThrow(hasOrderConflict, isLinkedHashMap, sheetName);

        boolean hasHeader = hasHeaderInEntries(dataEntries);

        if (!hasOrderConflict) {
            dataEntries.sort(compareEntriesByMinOrder());
            Map<Integer, ColumnInfo> mergedColumns = buildMergedColumnsFromEntries(dataEntries);
            List<IteratorEntry> iteratorEntries = prepareIteratorEntries(dataEntries);
            Iterator<Map<String, Object>> mergedDataIterator = prepareMergedDataByOrder(iteratorEntries, mergedColumns);

            ExcelMetadata<Map<String, Object>> metadata = ExcelMetadataFactory.createFromMergedColumns(
                    sheetName, mergedColumns, hasHeader);
            return new MergedDataResult(metadata, mergedDataIterator);
        } else {
            Map<Integer, ColumnInfo> sequentialColumns = buildSequentialColumnsFromEntries(
                    dataEntries);
            List<IteratorEntry> iteratorEntries = prepareIteratorEntries(dataEntries);
            Iterator<Map<String, Object>> mergedDataIterator = prepareMergedDataSequential(iteratorEntries);

            ExcelMetadata<Map<String, Object>> metadata = ExcelMetadataFactory.createFromMergedColumns(
                    sheetName, sequentialColumns, hasHeader);
            return new MergedDataResult(metadata, mergedDataIterator);
        }
    }

    private static List<IteratorEntry> prepareIteratorEntries(List<SheetDataEntry> dataEntries) {
        List<IteratorEntry> entries = new ArrayList<>(dataEntries.size());
        for (SheetDataEntry entry : dataEntries) {
            entries.add(new IteratorEntry(entry.getData(), entry.getClazz()));
        }
        return entries;
    }

    private static final class IteratorEntry {
        private final Iterator<?> iterator;
        @Getter
        private final Class<?> clazz;
        @Getter
        private Object current;
        private boolean hasNext;

        private IteratorEntry(Iterator<?> iterator, Class<?> clazz) {
            this.iterator = iterator;
            this.clazz = clazz;
            advance();
        }

        private void advance() {
            if (iterator.hasNext()) {
                current = iterator.next();
                hasNext = true;
            } else {
                current = null;
                hasNext = false;
            }
        }

        public boolean hasNext() {
            return hasNext;
        }

        public void moveNext() {
            advance();
        }

    }

    private static Iterator<Map<String, Object>> prepareMergedDataByOrder(
            List<IteratorEntry> dataEntries,
            Map<Integer, ColumnInfo> mergedColumns) {
        List<Integer> orderKeys = new ArrayList<>(mergedColumns.keySet());

        return new Iterator<Map<String, Object>>() {
            @Override
            public boolean hasNext() {
                for (IteratorEntry entry : dataEntries) {
                    if (entry.hasNext()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Map<String, Object> next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                Map<String, Object> mergedRow = new LinkedHashMap<>();

                for (IteratorEntry entry : dataEntries) {
                    if (entry.hasNext()) {
                        Object dataItem = entry.getCurrent();
                        ExcelMetadata<Object> metadata = extractMetadata(entry.getClazz());
                        populateRowByOrder(mergedRow, metadata, dataItem, orderKeys);
                        entry.moveNext();
                    }
                }

                return mergedRow;
            }
        };
    }

    private static void populateRowByOrder(
            Map<String, Object> mergedRow,
            ExcelMetadata<Object> metadata,
            Object dataItem,
            List<Integer> orderKeys) {
        Set<Integer> orders = metadata.getAllOrders();

        for (Integer order : orders) {
            int metadataColumnIndex = findColumnIndexByOrder(metadata, order);
            if (metadataColumnIndex < 0) {
                continue;
            }

            int sheetColumnIndex = orderKeys.indexOf(order);
            String fieldName = String.valueOf(sheetColumnIndex);
            Object value = metadata.getExtractors().get(metadataColumnIndex).apply(dataItem);
            mergedRow.put(fieldName, value);
        }
    }

    private static Iterator<Map<String, Object>> prepareMergedDataSequential(
            List<IteratorEntry> dataEntries) {
        return new Iterator<Map<String, Object>>() {
            @Override
            public boolean hasNext() {
                for (IteratorEntry entry : dataEntries) {
                    if (entry.hasNext()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Map<String, Object> next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                Map<String, Object> mergedRow = new LinkedHashMap<>();
                int globalColumnIndex = 0;

                for (IteratorEntry entry : dataEntries) {
                    ExcelMetadata<Object> metadata = extractMetadata(entry.getClazz());

                    if (entry.hasNext()) {
                        Object dataItem = entry.getCurrent();
                        globalColumnIndex = populateRowSequential(mergedRow, metadata, dataItem, globalColumnIndex);
                        entry.moveNext();
                    } else {
                        globalColumnIndex += metadata.getExtractors().size();
                    }
                }

                return mergedRow;
            }
        };
    }

    private static int populateRowSequential(
            Map<String, Object> mergedRow,
            ExcelMetadata<Object> metadata,
            Object dataItem,
            int startColumnIndex) {
        int columnIndex = startColumnIndex;

        for (int extractorIndex = 0; extractorIndex < metadata.getExtractors().size(); extractorIndex++) {
            String fieldName = String.valueOf(columnIndex);
            Object value = metadata.getExtractors().get(extractorIndex).apply(dataItem);
            mergedRow.put(fieldName, value);
            columnIndex++;
        }

        return columnIndex;
    }

    @SuppressWarnings("unchecked")
    private static ExcelMetadata<Object> extractMetadata(Class<?> clazz) {
        return ExcelMetadataCache.getMetadata((Class<Object>) clazz);
    }

    private static boolean hasHeaderInEntries(List<SheetDataEntry> dataEntries) {
        return !dataEntries.isEmpty() &&
                ExcelMetadataCache.getMetadata(dataEntries.get(0).getClazz()).hasHeader();
    }

    private static Map<Integer, ColumnInfo> buildMergedColumnsFromEntries(
            List<SheetDataEntry> dataEntries) {
        Map<Integer, ColumnInfo> mergedColumns = new TreeMap<>();

        for (SheetDataEntry entry : dataEntries) {
            ExcelMetadata<?> metadata =
                    ExcelMetadataFactory.extractExcelMetadata(entry.getClazz());
            addColumnsToMergedMap(mergedColumns, metadata);
        }

        return mergedColumns;
    }

    private static Map<Integer, ColumnInfo> buildSequentialColumnsFromEntries(
            List<SheetDataEntry> dataEntries) {
        Map<Integer, ColumnInfo> sequentialColumns = new LinkedHashMap<>();
        int globalIndex = 0;

        for (SheetDataEntry entry : dataEntries) {
            ExcelMetadata<?> metadata = ExcelMetadataFactory.extractExcelMetadata(
                    entry.getClazz());

            for (ColumnInfo columnInfo : metadata.getColumnInfos()) {
                sequentialColumns.put(globalIndex, columnInfo);
                globalIndex++;
            }
        }
        return sequentialColumns;
    }

    private static void addColumnsToMergedMap(
            Map<Integer, ColumnInfo> mergedColumns,
            ExcelMetadata<?> metadata) {
        for (ColumnInfo columnInfo : metadata.getColumnInfos()) {
            mergedColumns.put(columnInfo.getOrder(), columnInfo);
        }
    }

    private static int findColumnIndexByOrder(
            ExcelMetadata<?> metadata,
            int targetOrder) {
        Set<Integer> orders = metadata.getAllOrders();
        List<Integer> sortedOrders = new ArrayList<>(orders);
        Collections.sort(sortedOrders);
        return sortedOrders.indexOf(targetOrder);
    }

    private static boolean hasOrderConflict(List<SheetDataEntry> dataEntries) {
        Set<Integer> uniqueOrders = new HashSet<>();
        int totalColumnCount = 0;

        for (SheetDataEntry entry : dataEntries) {
            ExcelMetadata<?> metadata =
                    ExcelMetadataFactory.extractExcelMetadata(entry.getClazz());
            uniqueOrders.addAll(metadata.getAllOrders());
            totalColumnCount += metadata.getColumnCount();
        }

        return uniqueOrders.size() < totalColumnCount;
    }

    private static void validateOrderConflictOrThrow(
            boolean hasOrderConflict,
            boolean isLinkedHashMap,
            String sheetName) {
        if (hasOrderConflict && !isLinkedHashMap) {
            String errorMessage = String.format(
                    "시트 '%s'에 병합되는 DTO들의 order 값이 충돌합니다. LinkedHashMap을 사용하세요.",
                    sheetName);
            throw new ExcelExporterException(ErrorCode.ORDER_CONFLICT, errorMessage);
        }
    }

    private static Comparator<SheetDataEntry> compareEntriesByMinOrder() {
        return (firstEntry, secondEntry) -> {
            ExcelMetadata<?> firstMetadata =
                    ExcelMetadataFactory.extractExcelMetadata(firstEntry.getClazz());
            ExcelMetadata<?> secondMetadata =
                    ExcelMetadataFactory.extractExcelMetadata(secondEntry.getClazz());
            return Integer.compare(firstMetadata.getMinOrder(), secondMetadata.getMinOrder());
        };
    }
}
