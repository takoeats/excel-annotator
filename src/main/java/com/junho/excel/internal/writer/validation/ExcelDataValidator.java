package com.junho.excel.internal.writer.validation;

import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.SheetGroupInfo;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class ExcelDataValidator {

  public void validateDataNotEmpty(List<?> list) {
    if (list == null || list.isEmpty()) {
      throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
    }
  }

  public void validateDataNotEmpty(Map<String, ?> dataMap) {
    if (dataMap == null || dataMap.isEmpty()) {
      throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
    }
  }

  public <T> Iterator<T> validateAndGetIterator(Stream<T> dataStream) {
    Iterator<T> iterator;
    try {
      iterator = dataStream.iterator();
    } catch (IllegalStateException e) {
      if (e.getMessage() != null && e
          .getMessage()
          .contains("stream has already been operated upon or closed")) {
        throw new ExcelExporterException(ErrorCode.STREAM_ALREADY_CONSUMED, e);
      }
      throw new ExcelExporterException(ErrorCode.WORKBOOK_CREATION_FAILED, e);
    }

    if (!iterator.hasNext()) {
      throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
    }
    return iterator;
  }

  public void validateDuplicateSheetOrders(
      List<Map.Entry<String, SheetGroupInfo>> withOrder) {
    Set<Integer> seenOrders = new HashSet<>();
    for (Map.Entry<String, SheetGroupInfo> entry : withOrder) {
      int order = entry
          .getValue()
          .getOrder();
      if (!seenOrders.add(order)) {
        throw new ExcelExporterException(ErrorCode.DUPLICATE_SHEET_ORDER,
            "중복된 시트 order: " + order + " (시트: " + entry.getKey() + ")");
      }
    }
  }
}
