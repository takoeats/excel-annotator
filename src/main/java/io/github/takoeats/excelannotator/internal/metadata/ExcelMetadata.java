package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.masking.Masking;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.rule.StyleRule;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Builder
public final class ExcelMetadata<T> implements SheetMetadata, ColumnMetadata, HeaderMetadata, StyleMetadata, DataExtractionMetadata<T> {
    private final List<String> headers;
    private final List<Function<T, Object>> extractors;
    private final List<Integer> columnWidths;
    private final SheetInfo sheetInfo;
    private final List<ColumnInfo> columnInfos;

    public String getSheetName() {
        return sheetInfo.getName();
    }

    public boolean hasHeader() {
        return sheetInfo.isHasHeader();
    }

    public CustomExcelCellStyle getHeaderStyleAt(int index) {
        if (columnInfos != null && index >= 0 && index < columnInfos.size()) {
            return columnInfos.get(index).getHeaderStyle();
        }
        return null;
    }

    public CustomExcelCellStyle getColumnStyleAt(int index) {
        if (columnInfos != null && index >= 0 && index < columnInfos.size()) {
            return columnInfos.get(index).getColumnStyle();
        }
        return null;
    }

    public List<StyleRule> getConditionalStyleRulesAt(int index) {
        if (columnInfos != null && index >= 0 && index < columnInfos.size()) {
            return columnInfos.get(index).getConditionalStyleRules();
        }
        return Collections.emptyList();
    }

    public String getFieldNameAt(int index) {
        if (columnInfos != null && index >= 0 && index < columnInfos.size()) {
            return columnInfos.get(index).getField().getName();
        }
        return null;
    }

    public int getMinOrder() {
        if (columnInfos == null || columnInfos.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        return columnInfos.stream()
                .mapToInt(ColumnInfo::getOrder)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    public Set<Integer> getAllOrders() {
        if (columnInfos == null || columnInfos.isEmpty()) {
            return Collections.emptySet();
        }
        return columnInfos.stream()
                .map(ColumnInfo::getOrder)
                .collect(Collectors.toSet());
    }

    public int getColumnCount() {
        return columnInfos != null ? columnInfos.size() : 0;
    }

    public String getFormatAt(int index) {
        if (columnInfos != null && index >= 0 && index < columnInfos.size()) {
            return columnInfos.get(index).getFormat();
        }
        return null;
    }

    public Masking getMaskingAt(int index) {
        if (columnInfos != null && index >= 0 && index < columnInfos.size()) {
            return columnInfos.get(index).getMasking();
        }
        return Masking.NONE;
    }

    public boolean hasAnyMergeHeader() {
        if (columnInfos == null || columnInfos.isEmpty()) {
            return false;
        }
        return columnInfos.stream().anyMatch(ColumnInfo::hasMergeHeader);
    }

    public String getMergeHeaderAt(int index) {
        if (columnInfos != null && index >= 0 && index < columnInfos.size()) {
            return columnInfos.get(index).getMergeHeader();
        }
        return "";
    }

    public CustomExcelCellStyle getMergeHeaderStyleAt(int index) {
        if (columnInfos != null && index >= 0 && index < columnInfos.size()) {
            return columnInfos.get(index).getMergeHeaderStyle();
        }
        return null;
    }

    public int getHeaderRowCount() {
        return hasAnyMergeHeader() ? 2 : 1;
    }
}
