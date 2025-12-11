package com.junho.excel.internal.writer.organizer;

import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.SheetDataEntry;
import com.junho.excel.internal.SheetGroupInfo;
import com.junho.excel.internal.metadata.SheetInfo;
import com.junho.excel.internal.writer.adapter.DataStreamAdapter;
import com.junho.excel.internal.writer.validation.ExcelDataValidator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class SheetDataOrganizer {

  private final DataStreamAdapter streamAdapter;
  private final ExcelDataValidator dataValidator;

  public SheetDataOrganizer(
      DataStreamAdapter streamAdapter,
      ExcelDataValidator dataValidator) {
    this.streamAdapter = streamAdapter;
    this.dataValidator = dataValidator;
  }

  @SuppressWarnings("unchecked")
  public Map<String, SheetGroupInfo> groupSheetDataFromStreams(
      Map<String, Stream<?>> sheetStreamMap) {
    Map<String, SheetGroupInfo> sheetGroupedData = new LinkedHashMap<>();

    for (Map.Entry<String, Stream<?>> entry : sheetStreamMap.entrySet()) {
      Iterator<?> iterator = entry.getValue().iterator();
      if (!iterator.hasNext()) {
        throw new ExcelExporterException(ErrorCode.EMPTY_DATA, "데이터가 없습니다: " + entry.getKey());
      }

      Object firstItem = iterator.next();
      Class<?> clazz = firstItem.getClass();
      SheetInfo sheetInfo = ExcelMetadataFactory.extractSheetInfo(clazz);

      Iterator<Object> fullIterator = streamAdapter.prependToIterator(firstItem, (Iterator<Object>) iterator);

      sheetGroupedData
          .computeIfAbsent(sheetInfo.getName(), k -> new SheetGroupInfo(sheetInfo.getOrder()))
          .addEntry(new SheetDataEntry(fullIterator, clazz));
    }

    return sheetGroupedData;
  }

  public List<Map.Entry<String, SheetGroupInfo>> sortSheetsByOrder(
      Map<String, SheetGroupInfo> sheetGroupedData) {
    List<Map.Entry<String, SheetGroupInfo>> withoutOrder = new ArrayList<>();
    List<Map.Entry<String, SheetGroupInfo>> withOrder = new ArrayList<>();

    for (Map.Entry<String, SheetGroupInfo> entry : sheetGroupedData.entrySet()) {
      if (entry
          .getValue()
          .getOrder() == Integer.MIN_VALUE) {
        withoutOrder.add(entry);
      } else {
        withOrder.add(entry);
      }
    }

    dataValidator.validateDuplicateSheetOrders(withOrder);
    withOrder.sort(Comparator.comparingInt(e -> e
        .getValue()
        .getOrder()));

    List<Map.Entry<String, SheetGroupInfo>> result = new ArrayList<>();
    result.addAll(withoutOrder);
    result.addAll(withOrder);
    return result;
  }
}
