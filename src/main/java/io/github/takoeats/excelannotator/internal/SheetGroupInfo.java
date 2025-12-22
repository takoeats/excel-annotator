package io.github.takoeats.excelannotator.internal;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class SheetGroupInfo {

    private final int order;
    private final List<SheetDataEntry> entries;

    public SheetGroupInfo(int order) {
        this.order = order;
        this.entries = new ArrayList<>();
    }

    public void addEntry(SheetDataEntry entry) {
        entries.add(entry);
    }
}
