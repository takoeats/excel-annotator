package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.masking.Masking;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.internal.rule.StyleRule;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@Getter
public final class ColumnInfo {
    private final String header;
    private final int order;
    private final int width;
    private final String format;
    private final Field field;
    private final CustomExcelCellStyle headerStyle;
    private final CustomExcelCellStyle columnStyle;
    private final List<StyleRule> conditionalStyleRules;
    private final String sheetName;
    private final Masking masking;
    private final String mergeHeader;
    private final CustomExcelCellStyle mergeHeaderStyle;

    private ColumnInfo(Builder builder) {
        this.header = builder.header;
        this.order = builder.order;
        this.width = builder.width;
        this.format = builder.format;
        this.field = builder.field;
        this.headerStyle = builder.headerStyle;
        this.columnStyle = builder.columnStyle;
        this.conditionalStyleRules = builder.conditionalStyleRules != null
                ? builder.conditionalStyleRules
                : Collections.emptyList();
        this.sheetName = builder.sheetName;
        this.masking = builder.masking != null ? builder.masking : Masking.NONE;
        this.mergeHeader = builder.mergeHeader != null ? builder.mergeHeader : "";
        this.mergeHeaderStyle = builder.mergeHeaderStyle;
    }

    public boolean hasMergeHeader() {
        return mergeHeader != null && !mergeHeader.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String header = "";
        private int order = Integer.MAX_VALUE;
        private int width = 0;
        private String format = "";
        private Field field = null;
        private CustomExcelCellStyle headerStyle = null;
        private CustomExcelCellStyle columnStyle = null;
        private List<StyleRule> conditionalStyleRules = Collections.emptyList();
        private String sheetName = "";
        private Masking masking = Masking.NONE;
        private String mergeHeader = "";
        private CustomExcelCellStyle mergeHeaderStyle = null;

        private Builder() {
        }

        public Builder header(String header) {
            this.header = header;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder field(Field field) {
            this.field = field;
            return this;
        }

        public Builder headerStyle(CustomExcelCellStyle headerStyle) {
            this.headerStyle = headerStyle;
            return this;
        }

        public Builder columnStyle(CustomExcelCellStyle columnStyle) {
            this.columnStyle = columnStyle;
            return this;
        }

        public Builder conditionalStyleRules(List<StyleRule> conditionalStyleRules) {
            this.conditionalStyleRules = conditionalStyleRules;
            return this;
        }

        public Builder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public Builder masking(Masking masking) {
            this.masking = masking;
            return this;
        }

        public Builder mergeHeader(String mergeHeader) {
            this.mergeHeader = mergeHeader;
            return this;
        }

        public Builder mergeHeaderStyle(CustomExcelCellStyle mergeHeaderStyle) {
            this.mergeHeaderStyle = mergeHeaderStyle;
            return this;
        }

        public ColumnInfo build() {
            return new ColumnInfo(this);
        }
    }
}
