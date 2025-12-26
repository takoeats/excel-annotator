package io.github.takoeats.excelannotator.internal.metadata.validator;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MergeHeaderValidator {

    public static void validateOrderContinuity(List<ColumnInfo> columnInfos) {
        if (columnInfos == null || columnInfos.isEmpty()) {
            return;
        }

        Map<String, List<Integer>> mergeHeaderGroups = groupByMergeHeader(columnInfos);

        for (Map.Entry<String, List<Integer>> entry : mergeHeaderGroups.entrySet()) {
            String mergeHeaderName = entry.getKey();
            List<Integer> orders = entry.getValue();

            if (orders.size() < 2) {
                continue;
            }

            Collections.sort(orders);

            validateContinuousOrders(mergeHeaderName, orders, columnInfos);
        }
    }

    private static Map<String, List<Integer>> groupByMergeHeader(List<ColumnInfo> columnInfos) {
        Map<String, List<Integer>> groups = new HashMap<>();

        for (ColumnInfo columnInfo : columnInfos) {
            if (columnInfo.hasMergeHeader()) {
                String mergeHeader = columnInfo.getMergeHeader();
                groups.computeIfAbsent(mergeHeader, k -> new ArrayList<>()).add(columnInfo.getOrder());
            }
        }

        return groups;
    }

    private static void validateContinuousOrders(String mergeHeaderName, List<Integer> orders, List<ColumnInfo> allColumns) {
        int minOrder = orders.get(0);
        int maxOrder = orders.get(orders.size() - 1);

        for (int expectedOrder = minOrder; expectedOrder <= maxOrder; expectedOrder++) {
            if (!orders.contains(expectedOrder)) {
                ColumnInfo gapColumn = findColumnByOrder(allColumns, expectedOrder);

                String errorDetail = String.format(
                        "병합 헤더 '%s'의 order 범위 [%d-%d] 사이에 다른 컬럼(order=%d, header='%s')이 존재합니다.",
                        mergeHeaderName,
                        minOrder,
                        maxOrder,
                        expectedOrder,
                        gapColumn != null ? gapColumn.getHeader() : "unknown"
                );

                throw new ExcelExporterException(ErrorCode.MERGE_HEADER_ORDER_GAP, errorDetail);
            }
        }
    }

    private static ColumnInfo findColumnByOrder(List<ColumnInfo> columnInfos, int order) {
        for (ColumnInfo columnInfo : columnInfos) {
            if (columnInfo.getOrder() == order) {
                return columnInfo;
            }
        }
        return null;
    }
}
