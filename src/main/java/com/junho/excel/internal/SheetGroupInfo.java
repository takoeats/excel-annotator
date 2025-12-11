package com.junho.excel.internal;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

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
