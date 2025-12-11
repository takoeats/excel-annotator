package com.junho.excel.internal;

import java.util.Iterator;
import lombok.Getter;

@Getter
public final class SheetDataEntry {

  private final Iterator<?> data;
  private final Class<?> clazz;

  public SheetDataEntry(Iterator<?> data, Class<?> clazz) {
    this.data = data;
    this.clazz = clazz;
  }

}
