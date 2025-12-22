package io.github.takoeats.excelannotator.internal.writer;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.ExcelMetadataFactory;
import io.github.takoeats.excelannotator.internal.SheetDataEntry;
import io.github.takoeats.excelannotator.internal.SheetGroupInfo;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.internal.writer.adapter.DataStreamAdapter;
import io.github.takoeats.excelannotator.internal.writer.builder.SheetRequestBuilder;
import io.github.takoeats.excelannotator.internal.writer.organizer.SheetDataOrganizer;
import io.github.takoeats.excelannotator.internal.writer.validation.ExcelDataValidator;
import io.github.takoeats.excelannotator.internal.writer.workbook.SXSSFWorkbookBuilder;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.*;
import java.util.stream.Stream;

/**
 * Excel 파일 작성을 위한 Facade 클래스
 * <p>다양한 Writer 클래스들을 조합하여 Excel 파일을 생성합니다.</p>
 * <p>모든 입력 타입(List, Stream, Map)을 Iterator로 변환하여 Single/Multi Sheet Writer에 위임합니다.</p>
 *
 * <h3>지원 기능</h3>
 * <ul>
 *     <li>단일 시트 Excel 생성 (List/Stream 기반)</li>
 *     <li>멀티 시트 Excel 생성 (여러 DTO를 각각 시트로)</li>
 *     <li>컬럼별 시트 분리 (@ExcelColumn.sheetName() 기반)</li>
 *     <li>100만건 초과 시 자동 시트 분할</li>
 * </ul>
 */
public final class ExcelWriter {

    private final ExcelDataValidator dataValidator;
    private final DataStreamAdapter streamAdapter;
    private final SheetDataOrganizer sheetOrganizer;
    private final SheetRequestBuilder requestBuilder;
    private final SXSSFWorkbookBuilder workbookBuilder;

    public ExcelWriter() {
        this.dataValidator = new ExcelDataValidator();
        this.streamAdapter = new DataStreamAdapter();
        this.sheetOrganizer = new SheetDataOrganizer(streamAdapter, dataValidator);
        this.requestBuilder = new SheetRequestBuilder();

        RowWriter rowWriter = new RowWriter();
        SheetWriter sheetWriter = new SheetWriter(rowWriter);
        this.workbookBuilder = new SXSSFWorkbookBuilder(sheetWriter);
    }

    public SXSSFWorkbook write(List<?> list) {
        dataValidator.validateDataNotEmpty(list);
        return write(list.stream());
    }

    public SXSSFWorkbook write(Map<String, List<?>> sheetDataMap) {
        dataValidator.validateDataNotEmpty(sheetDataMap);
        boolean isLinkedHashMap = sheetDataMap instanceof LinkedHashMap;
        Map<String, Stream<?>> streamMap = isLinkedHashMap ? new LinkedHashMap<>() : new java.util.HashMap<>();
        for (Map.Entry<String, List<?>> entry : sheetDataMap.entrySet()) {
            List<?> list = entry.getValue();
            if (list == null || list.isEmpty()) {
                throw new ExcelExporterException(ErrorCode.EMPTY_DATA, "데이터가 없습니다: " + entry.getKey());
            }
            streamMap.put(entry.getKey(), list.stream());
        }
        return writeWithStreams(streamMap);
    }

    public SXSSFWorkbook writeWithStreams(Map<String, Stream<?>> sheetStreamMap) {
        dataValidator.validateDataNotEmpty(sheetStreamMap);
        return writeFromStreamMap(sheetStreamMap);
    }

    @SuppressWarnings("unchecked")
    public SXSSFWorkbook write(Stream<?> stream) {
        Iterator<?> iterator = dataValidator.validateAndGetIterator(stream);

        Object firstElement = iterator.next();
        Class<?> clazz = firstElement.getClass();
        Iterator<Object> fullIterator = streamAdapter.prependToIterator(firstElement, (Iterator<Object>) iterator);

        Map<String, ExcelMetadata<Object>> multiSheetMeta =
                (Map<String, ExcelMetadata<Object>>) (Map<?, ?>) ExcelMetadataFactory.extractMultiSheetMetadata(
                        clazz);

        if (multiSheetMeta.size() > 1) {
            List<ExcelMetadata<Object>> metadataList = new ArrayList<>(multiSheetMeta.values());
            SheetWriteContext<Object> context = SheetWriteContext.forColumnBasedSheets(
                    fullIterator, metadataList);
            return workbookBuilder.createWorkbookAndWrite(context);
        }

        ExcelMetadata<Object> metadata = (ExcelMetadata<Object>) ExcelMetadataFactory.extractExcelMetadata(
                clazz);
        SheetWriteRequest<Object> request = requestBuilder.createRequest(fullIterator, metadata);
        SheetWriteContext<Object> context = SheetWriteContext.forRowBasedSheets(
                Collections.singletonList(request));
        return workbookBuilder.createWorkbookAndWrite(context);
    }

    /**
     * Case 2 & Case 3: 멀티 시트 Excel 파일 작성 (List 기반)
     * <p>여러 DTO를 시트로 생성합니다. Sheet 이름은 DTO의 @ExcelSheet.value()에서 추출합니다.</p>
     * <p>동일한 Sheet 이름을 가진 DTO들은 하나의 시트에 병합됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * // Map의 Key는 식별용, 실제 Sheet 이름은 @ExcelSheet.value()에서 추출
     * Map<String, List<?>> sheetData = new LinkedHashMap<>();
     * sheetData.put("identifier1", customerList);  // Sheet 이름: @ExcelSheet("고객")
     * sheetData.put("identifier2", orderList);     // Sheet 이름: @ExcelSheet("주문")
     * sheetData.put("identifier3", productList);   // Sheet 이름: @ExcelSheet("고객") -> "고객" 시트에 병합
     * }</pre>
     *
     * @return SXSSFWorkbook 객체 (호출자는 반드시 close() 및 dispose() 호출 필요)
     * @throws ExcelExporterException 멀티 시트 생성 중 오류 발생 시
     */
    private SXSSFWorkbook writeFromStreamMap(Map<String, Stream<?>> sheetStreamMap) {
        boolean isLinkedHashMap = sheetStreamMap instanceof LinkedHashMap;

        Map<String, SheetGroupInfo> sheetGroupedData = sheetOrganizer.groupSheetDataFromStreams(sheetStreamMap);
        List<Map.Entry<String, SheetGroupInfo>> sortedSheets = sheetOrganizer.sortSheetsByOrder(sheetGroupedData);

        List<SheetWriteRequest<?>> requests = new ArrayList<>();

        for (Map.Entry<String, SheetGroupInfo> entry : sortedSheets) {
            String sheetName = entry.getKey();
            List<SheetDataEntry> dataEntries = entry
                    .getValue()
                    .getEntries();

            if (dataEntries.size() == 1) {
                requests.addAll(requestBuilder.createRequestsForSingleEntry(sheetName, dataEntries.get(0)));
            } else {
                requests.add(requestBuilder.createRequestForMergedData(sheetName, dataEntries, isLinkedHashMap));
            }
        }

        SheetWriteContext<?> context = SheetWriteContext.forRowBasedSheets(requests);
        return workbookBuilder.createWorkbookAndWrite(context);
    }

}
