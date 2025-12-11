package com.junho.excel.internal.writer;

import com.junho.excel.internal.metadata.ExcelMetadata;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;

@Getter
public final class SheetWriteContext<T> {

  private final List<SheetWriteRequest<?>> sheetRequests;
  private final Iterator<T> columnDataIterator;
  private final List<ExcelMetadata<T>> columnMetadataList;

  private SheetWriteContext(
      List<SheetWriteRequest<?>> sheetRequests,
      Iterator<T> columnDataIterator,
      List<ExcelMetadata<T>> columnMetadataList) {

    this.sheetRequests = sheetRequests;
    this.columnDataIterator = columnDataIterator;
    this.columnMetadataList = columnMetadataList;
  }

  public static <T> SheetWriteContext<T> forRowBasedSheets(
      List<SheetWriteRequest<?>> sheetRequests) {
    return new SheetWriteContext<>(sheetRequests, null, null);
  }

  public static <T> SheetWriteContext<T> forColumnBasedSheets(
      Iterator<T> dataIterator,
      List<ExcelMetadata<T>> metadataList) {
    return new SheetWriteContext<>(Collections.emptyList(), dataIterator, metadataList);
  }

  public boolean isColumnBasedSplit() {
    return columnDataIterator != null;
  }

  public int getSheetCount() {
    return isColumnBasedSplit() ? columnMetadataList.size() : sheetRequests.size();
  }

}
