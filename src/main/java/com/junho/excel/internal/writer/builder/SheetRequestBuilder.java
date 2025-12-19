package com.junho.excel.internal.writer.builder;

import com.junho.excel.internal.ExcelMetadataFactory;
import com.junho.excel.internal.SheetDataEntry;
import com.junho.excel.internal.metadata.ExcelMetadata;
import com.junho.excel.internal.metadata.SheetInfo;
import com.junho.excel.internal.util.MergedDataConverter;
import com.junho.excel.internal.util.MergedDataConverter.MergedDataResult;
import com.junho.excel.internal.util.SheetNameValidator;
import com.junho.excel.internal.writer.SheetWriteRequest;

import java.util.*;

public final class SheetRequestBuilder {

    public <T> SheetWriteRequest<T> createRequest(
            Iterator<T> iterator,
            ExcelMetadata<T> metadata) {

        return SheetWriteRequest
                .<T>builder()
                .dataIterator(iterator)
                .metadata(metadata)
                .build();
    }

    @SuppressWarnings("unchecked")
    public List<SheetWriteRequest<?>> createRequestsForSingleEntry(
            String sheetName, SheetDataEntry singleEntry) {

        Class<Object> clazz = (Class<Object>) singleEntry.getClazz();
        Iterator<Object> dataIterator = (Iterator<Object>) singleEntry.getData();

        Map<String, ExcelMetadata<Object>> multiSheetMeta =
                ExcelMetadataFactory.extractMultiSheetMetadata(clazz);

        if (multiSheetMeta.size() > 1) {
            List<Object> materializedData = new ArrayList<>();
            dataIterator.forEachRemaining(materializedData::add);

            List<SheetWriteRequest<?>> requests = new ArrayList<>();
            for (ExcelMetadata<Object> metadata : multiSheetMeta.values()) {
                requests.add(createRequest(materializedData.iterator(), metadata));
            }
            return requests;
        }

        ExcelMetadata<Object> metadata = ExcelMetadataFactory.extractExcelMetadata(clazz);
        ExcelMetadata<Object> updatedMetadata = updateSheetName(metadata, sheetName);

        return Collections.singletonList(createRequest(dataIterator, updatedMetadata));
    }

    public SheetWriteRequest<Map<String, Object>> createRequestForMergedData(
            String sheetName,
            List<SheetDataEntry> dataEntries,
            boolean isLinkedHashMap) {

        MergedDataResult result = MergedDataConverter.convertToMergedData(
                sheetName, dataEntries, isLinkedHashMap);

        ExcelMetadata<Map<String, Object>> metadata = result.getMetadata();
        Iterator<Map<String, Object>> dataIterator = result.getDataIterator();

        return createRequest(dataIterator, metadata);
    }

    private <T> ExcelMetadata<T> updateSheetName(
            ExcelMetadata<T> metadata,
            String newSheetName) {

        String sanitizedName = SheetNameValidator.validateAndSanitize(newSheetName);

        return ExcelMetadata
                .<T>builder()
                .headers(metadata.getHeaders())
                .extractors(metadata.getExtractors())
                .columnWidths(metadata.getColumnWidths())
                .columnInfos(metadata.getColumnInfos())
                .sheetInfo(SheetInfo
                        .builder()
                        .name(sanitizedName)
                        .hasHeader(metadata.hasHeader())
                        .build())
                .build();
    }
}
