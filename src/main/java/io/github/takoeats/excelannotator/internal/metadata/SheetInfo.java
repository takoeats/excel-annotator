package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SheetInfo {
    private final String name;
    private final boolean hasHeader;
    private final int order;
    private final Class<? extends CustomExcelCellStyle> defaultHeaderStyle;
    private final Class<? extends CustomExcelCellStyle> defaultColumnStyle;

    public boolean hasOrder() {
        return order != Integer.MIN_VALUE;
    }
}
