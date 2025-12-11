package com.junho.excel.internal.metadata;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SheetInfo {
    private final String name;
    private final boolean hasHeader;
    private final int order;

    public boolean hasOrder() {
        return order != Integer.MIN_VALUE;
    }
}
