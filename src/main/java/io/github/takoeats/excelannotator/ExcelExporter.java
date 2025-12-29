package io.github.takoeats.excelannotator;


import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.builder.BuilderFactory;
import io.github.takoeats.excelannotator.internal.builder.CsvBuilder;
import io.github.takoeats.excelannotator.internal.builder.ExcelBuilder;
import io.github.takoeats.excelannotator.internal.util.FilenameSecurityValidator;
import io.github.takoeats.excelannotator.internal.writer.CsvWriter;
import io.github.takoeats.excelannotator.internal.writer.ExcelWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Excel 다운로드 Static Utility 클래스
 * <p>Apache POI 기반 어노테이션 방식 Excel 생성 및 다운로드</p>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelExporter {

    private static final String XLSX = ".xlsx";
    private static final String DEFAULT_FILE_NAME = "download";
    private static final int MAX_ROWS_FOR_LIST_API = 1000000;

    /**
     * Fluent API 진입점: Excel 파일을 HttpServletResponse로 다운로드
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * ExcelExporter.excel(response)
     *     .fileName("customers.xlsx")
     *     .write(customerList);
     * }</pre>
     *
     * @param response HTTP 응답 객체
     * @return Excel 빌더 인스턴스
     */
    public static ExcelBuilder excel(HttpServletResponse response) {
        return BuilderFactory.createExcelBuilder(response);
    }

    /**
     * Fluent API 진입점: Excel 파일을 OutputStream에 작성
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * try (FileOutputStream fos = new FileOutputStream("output.xlsx")) {
     *     String fileName = ExcelExporter.excel(fos)
     *         .fileName("customers.xlsx")
     *         .write(customerList);
     * }
     * }</pre>
     *
     * @param outputStream 출력 스트림
     * @return Excel 빌더 인스턴스
     */
    public static ExcelBuilder excel(OutputStream outputStream) {
        return BuilderFactory.createExcelBuilder(outputStream);
    }

    /**
     * Fluent API 진입점: CSV 파일을 HttpServletResponse로 다운로드
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * ExcelExporter.csv(response)
     *     .fileName("customers.csv")
     *     .write(customerList);
     * }</pre>
     *
     * @param response HTTP 응답 객체
     * @return CSV 빌더 인스턴스
     */
    public static CsvBuilder csv(HttpServletResponse response) {
        return BuilderFactory.createCsvBuilder(response);
    }

    /**
     * Fluent API 진입점: CSV 파일을 OutputStream에 작성
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * try (FileOutputStream fos = new FileOutputStream("output.csv")) {
     *     String fileName = ExcelExporter.csv(fos)
     *         .fileName("customers.csv")
     *         .write(customerList);
     * }
     * }</pre>
     *
     * @param outputStream 출력 스트림
     * @return CSV 빌더 인스턴스
     */
    public static CsvBuilder csv(OutputStream outputStream) {
        return BuilderFactory.createCsvBuilder(outputStream);
    }


    /**
     * 어노테이션 기반 Excel 파일 다운로드 (단순 버전, 기본 파일명)
     * <p>데이터 리스트를 Excel 파일로 변환하여 HTTP 응답으로 전송합니다.</p>
     * <p>파일명이 지정되지 않으면 "download_타임스탬프.xlsx" 형식으로 생성됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * // Controller에서 호출
     * try {
     *     List<CustomerDTO> customers = service.getCustomers();
     *     ExcelExporter.excelFromList(response, customers);
     * } catch (ExcelExporterException ex) {
     *     // 에러 처리
     * }
     * }</pre>
     *
     * @param <T>      Excel DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param response HTTP 응답 객체 (Excel 파일이 이 응답으로 전송됨)
     * @param data     Excel로 변환할 데이터 리스트
     * @throws ExcelExporterException 데이터가 null이거나 비어있을 경우, 또는 Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(HttpServletResponse)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * ExcelExporter.excel(response).write(customers);
     * }</pre>
     */
    @Deprecated
    public static <T> void excelFromList(HttpServletResponse response, List<T> data) {
        excel(response).write(data);
    }

    /**
     * 어노테이션 기반 Excel 파일 다운로드 (단순 버전)
     * <p>데이터 리스트를 Excel 파일로 변환하여 HTTP 응답으로 전송합니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @ExcelSheet("고객 목록")
     * public class CustomerDTO {
     *     @ExcelColumn(header = "이름", order = 1)
     *     private String name;
     *
     *     @ExcelColumn(header = "이메일", order = 2)
     *     private String email;
     * }
     *
     * // Controller에서 호출
     * try {
     *     List<CustomerDTO> customers = service.getCustomers();
     *     ExcelExporter.excelFromList(response, "customers.xlsx", customers);
     * } catch (ExcelExporterException ex) {
     *     // 에러 처리
     * }
     * }</pre>
     *
     * @param <T>      Excel DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param response HTTP 응답 객체 (Excel 파일이 이 응답으로 전송됨)
     * @param fileName 다운로드될 파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가)
     * @param data     Excel로 변환할 데이터 리스트
     * @throws ExcelExporterException 데이터가 null이거나 비어있을 경우, 또는 Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(HttpServletResponse)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * ExcelExporter.excel(response).fileName("customers.xlsx").write(customers);
     * }</pre>
     */
    @Deprecated
    public static <T> void excelFromList(HttpServletResponse response, String fileName,
                                         List<T> data) {
        excel(response).fileName(fileName).write(data);
    }

    /**
     * 어노테이션 기반 Excel 파일을 OutputStream에 작성 (파일명 지정)
     * <p>데이터 리스트를 Excel 파일로 변환하여 제공된 OutputStream에 출력합니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * String fileName = ExcelExporter.excelFromList(fos, "customers.xlsx", customers);
     * // fileName: "customers.xlsx" (보안 검증 및 인코딩 적용, 명시적 파일명은 타임스탬프 없음)
     * }</pre>
     *
     * @param <T>          Excel DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param outputStream Excel 데이터가 출력될 스트림 (호출자가 스트림 닫기 책임)
     * @param fileName     파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가, 보안 검증 및 인코딩 적용)
     * @param data         Excel로 변환할 데이터 리스트
     * @return 보안 검증 및 인코딩이 적용된 최종 파일명
     * @throws ExcelExporterException 데이터가 null이거나 비어있을 경우, 또는 Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.excel(fos).fileName("customers.xlsx").write(customers);
     * }</pre>
     */
    @Deprecated
    public static <T> String excelFromList(OutputStream outputStream, String fileName, List<T> data) {
        return excel(outputStream).fileName(fileName).write(data);
    }

    /**
     * 어노테이션 기반 Excel 파일을 OutputStream에 작성 (기본 파일명)
     * <p>데이터 리스트를 Excel 파일로 변환하여 제공된 OutputStream에 출력합니다.</p>
     * <p>파일명이 지정되지 않으면 "download_타임스탬프.xlsx" 형식으로 생성됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * String fileName = ExcelExporter.excelFromList(fos, customers);
     * // fileName: "download_20250119_135348.xlsx"
     * }</pre>
     *
     * @param <T>          Excel DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param outputStream Excel 데이터가 출력될 스트림 (호출자가 스트림 닫기 책임)
     * @param data         Excel로 변환할 데이터 리스트
     * @return 생성된 파일명 (download_타임스탬프.xlsx)
     * @throws ExcelExporterException 데이터가 null이거나 비어있을 경우, 또는 Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.excel(fos).write(customers);
     * }</pre>
     */
    @Deprecated
    public static <T> String excelFromList(OutputStream outputStream, List<T> data) {
        return excel(outputStream).write(data);
    }

    /**
     * 어노테이션 기반 Excel 파일을 OutputStream에 작성 (Stream 기반, 메모리 효율 극대화)
     * <p>Stream API를 사용하여 데이터를 한 번에 하나씩 처리합니다.</p>
     * <p>대용량 데이터 처리 시 List 전체 로딩 없이 메모리 효율적으로 처리 가능합니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * // DB 커서로부터 스트리밍 처리 (100만 건도 메모리 부담 없음)
     * Stream<CustomerDTO> dataStream = customerRepository.streamAllCustomers();
     * String fileName = ExcelExporter.excelFromStream(
     *     fos,
     *     "customers.xlsx",
     *     dataStream,
     *     CustomerDTO.class
     * );
     * }</pre>
     *
     * @param <T>          Excel DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param outputStream Excel 데이터가 출력될 스트림 (호출자가 스트림 닫기 책임)
     * @param fileName     파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가, 보안 검증 및 인코딩 적용)
     * @param dataStream   Excel로 변환할 데이터 스트림
     * @return 보안 검증 및 인코딩이 적용된 최종 파일명
     * @throws ExcelExporterException Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.excel(fos).fileName("customers.xlsx").write(dataStream);
     * }</pre>
     */
    @Deprecated
    public static <T> String excelFromStream(OutputStream outputStream, String fileName,
                                             Stream<T> dataStream) {
        return excel(outputStream).fileName(fileName).write(dataStream);
    }

    /**
     * 어노테이션 기반 Excel 파일을 OutputStream에 작성 (Stream 기반, 기본 파일명)
     * <p>Stream API를 사용하여 데이터를 한 번에 하나씩 처리합니다.</p>
     * <p>파일명이 지정되지 않으면 "download_타임스탬프.xlsx" 형식으로 생성됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * Stream<CustomerDTO> dataStream = customerRepository.streamAllCustomers();
     * String fileName = ExcelExporter.excelFromStream(fos, dataStream, CustomerDTO.class);
     * // fileName: "download_20250119_135348.xlsx"
     * }</pre>
     *
     * @param <T>          Excel DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param outputStream Excel 데이터가 출력될 스트림 (호출자가 스트림 닫기 책임)
     * @param dataStream   Excel로 변환할 데이터 스트림
     * @return 생성된 파일명 (download_타임스탬프.xlsx)
     * @throws ExcelExporterException Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.excel(fos).write(dataStream);
     * }</pre>
     */
    @Deprecated
    public static <T> String excelFromStream(OutputStream outputStream, Stream<T> dataStream) {
        return excel(outputStream).write(dataStream);
    }

    /**
     * 멀티 시트 Excel 파일 다운로드 (Stream 기반, HttpServletResponse)
     * <p>여러 DTO 스트림을 각각의 시트로 생성하여 다운로드합니다.</p>
     * <p>시트 이름은 각 DTO의 @ExcelSheet.value()에서 추출되며, Map의 key는 단순 식별자로만 사용됩니다.</p>
     * <p>동일한 시트 이름을 가진 DTO들은 @ExcelColumn.order 순서대로 정렬되어 하나의 시트로 병합됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @PostMapping("/multiSheetStreamExcel")
     * public void downloadExcel(HttpServletResponse response) {
     *     Map<String, Stream<?>> sheetStreams = new LinkedHashMap<>();
     *     sheetStreams.put("identifier1", customerRepository.streamAllCustomers());  // @ExcelSheet("고객") -> "고객" 시트
     *     sheetStreams.put("identifier2", orderRepository.streamAllOrders());        // @ExcelSheet("주문") -> "주문" 시트
     *
     *     ExcelExporter.excelFromStream(response, "report.xlsx", sheetStreams);
     * }
     * }</pre>
     *
     * @param response       HTTP 응답 객체
     * @param fileName       다운로드될 파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가)
     * @param sheetStreamMap 식별자-스트림 매핑 (순서 보장을 위해 LinkedHashMap 권장)
     * @throws ExcelExporterException 멀티 시트 Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(HttpServletResponse)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * ExcelExporter.excel(response).fileName("report.xlsx").write(sheetStreams);
     * }</pre>
     */
    @Deprecated
    public static void excelFromStream(HttpServletResponse response, String fileName,
                                       Map<String, Stream<?>> sheetStreamMap) {
        setupResponseAndWriteExcel(response, fileName,
                outputStream -> writeMultiSheetWorkbookFromStreams(outputStream, sheetStreamMap));
    }

    /**
     * 멀티 시트 Excel 파일 작성 (Stream 기반, OutputStream)
     * <p>여러 DTO 스트림을 각각의 시트로 생성하여 OutputStream에 출력합니다.</p>
     * <p>시트 이름은 각 DTO의 @ExcelSheet.value()에서 추출되며, Map의 key는 단순 식별자로만 사용됩니다.</p>
     * <p>동일한 시트 이름을 가진 DTO들은 @ExcelColumn.order 순서대로 정렬되어 하나의 시트로 병합됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * Map<String, Stream<?>> sheetStreams = new LinkedHashMap<>();
     * sheetStreams.put("identifier1", customerRepository.streamAllCustomers());  // @ExcelSheet("고객") -> "고객" 시트
     * sheetStreams.put("identifier2", orderRepository.streamAllOrders());        // @ExcelSheet("주문") -> "주문" 시트
     *
     * try (FileOutputStream fos = new FileOutputStream("report.xlsx")) {
     *     String fileName = ExcelExporter.excelFromStream(fos, "report.xlsx", sheetStreams);
     * }
     * }</pre>
     *
     * @param outputStream   Excel 데이터가 출력될 스트림
     * @param fileName       파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가, 보안 검증 및 인코딩 적용)
     * @param sheetStreamMap 식별자-스트림 매핑 (순서 보장을 위해 LinkedHashMap 권장)
     * @return 보안 검증 및 인코딩이 적용된 최종 파일명
     * @throws ExcelExporterException 멀티 시트 Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.excel(fos).fileName("report.xlsx").write(sheetStreams);
     * }</pre>
     */
    @Deprecated
    public static String excelFromStream(OutputStream outputStream, String fileName,
                                         Map<String, Stream<?>> sheetStreamMap) {
        String sanitizedFileName = encodeFileNameCommons(fileName);
        String transFileName = getTransFileName(sanitizedFileName);
        writeMultiSheetWorkbookFromStreams(outputStream, sheetStreamMap);
        return transFileName;
    }

    /**
     * 어노테이션 기반 Excel 파일 다운로드 (Stream 기반, HttpServletResponse)
     * <p>Stream API를 사용하여 데이터를 한 번에 하나씩 처리합니다.</p>
     * <p>대용량 데이터 처리 시 List 전체 로딩 없이 메모리 효율적으로 처리 가능합니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @PostMapping("/excelDown")
     * public void downloadExcel(HttpServletResponse response) {
     *     Stream<CustomerDTO> dataStream = customerRepository.streamAllCustomers();
     *     ExcelExporter.excelFromStream(response, "customers.xlsx", dataStream, CustomerDTO.class);
     * }
     * }</pre>
     *
     * @param <T>        Excel DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param response   HTTP 응답 객체 (Excel 파일이 이 응답으로 전송됨)
     * @param fileName   다운로드될 파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가)
     * @param dataStream Excel로 변환할 데이터 스트림
     * @throws ExcelExporterException Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(HttpServletResponse)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * ExcelExporter.excel(response).fileName("customers.xlsx").write(dataStream);
     * }</pre>
     */
    @Deprecated
    public static <T> void excelFromStream(HttpServletResponse response, String fileName,
                                           Stream<T> dataStream) {
        excel(response).fileName(fileName).write(dataStream);
    }

    private static String getTransFileName(String fileName) {
        if (isPersonalFileName(fileName)) {
            return ensureXlsxExtension(fileName);
        }

        String ts = LocalDateTime
                .now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nameWithoutExt = removeExtension(fileName);
        return nameWithoutExt + "_" + ts + XLSX;
    }

    private static boolean isPersonalFileName(String fileName) {
        String nameWithoutExt = removeExtension(fileName);
        return !DEFAULT_FILE_NAME.equals(nameWithoutExt);
    }

    private static String ensureXlsxExtension(String fileName) {
        if (!fileName.endsWith(XLSX) || !fileName.contains(".")) {
            return fileName + XLSX;
        }
        return fileName;
    }

    /**
     * 어노테이션 기반 Excel 다운로드 (데이터 조회 + 변환 통합 버전)
     * <p>데이터 조회부터 Excel 변환까지 한 번에 처리하는 고급 API입니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * // Controller에서 호출
     * try {
     *     ExcelExporter.downloadExcel(
     *         response,
     *         "customers.xlsx",
     *         request,
     *         customerService::getCustomerList,
     *         customerConverter::toExcelDTO
     *     );
     * } catch (ExcelExporterException ex) {
     *     // 에러 처리
     * }
     * }</pre>
     *
     * <p><strong>장점:</strong> Service 조회 → DTO 변환 → Excel 생성을 한 줄로 처리</p>
     *
     * @param <Q>          조회 조건 타입 (Request 객체 등)
     * @param <R>          서비스 응답 데이터 타입 (Entity, Response 객체 등)
     * @param <E>          Excel DTO 타입 (@ExcelSheet, @ExcelColumn 어노테이션 필요)
     * @param response     HTTP 응답 객체
     * @param fileName     다운로드될 파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가)
     * @param queryParams  데이터 조회에 사용될 조건 객체
     * @param dataProvider 데이터 조회 함수 (queryParams → List&lt;R&gt;)
     * @param converter    데이터 변환 함수 (R → E)
     * @throws ExcelExporterException Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(HttpServletResponse)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * ExcelExporter.excel(response).fileName("customers.xlsx").write(queryParams, dataProvider, converter);
     * }</pre>
     */
    @Deprecated
    public static <Q, R, E> void excelFromList(HttpServletResponse response, String fileName,
                                               Q queryParams,
                                               ExcelDataProvider<Q, R> dataProvider,
                                               Function<R, E> converter) {
        excel(response).fileName(fileName).write(queryParams, dataProvider, converter);
    }

    /**
     * 어노테이션 기반 Excel 생성 (데이터 조회 + 변환 통합, OutputStream 버전, 파일명 지정)
     * <p>데이터 조회부터 Excel 변환까지 한 번에 처리하여 OutputStream에 출력합니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * try (FileOutputStream fos = new FileOutputStream("customers.xlsx")) {
     *     String fileName = ExcelExporter.downloadExcel(
     *         fos,
     *         "customers.xlsx",
     *         request,
     *         customerService::getCustomerList,
     *         customerConverter::toExcelDTO
     *     );
     *     // fileName: "customers.xlsx" (명시적 파일명은 타임스탬프 없음)
     * } catch (ExcelExporterException ex) {
     *     // 에러 처리
     * }
     * }</pre>
     *
     * @param <Q>          조회 조건 타입 (Request 객체 등)
     * @param <R>          서비스 응답 데이터 타입 (Entity, Response 객체 등)
     * @param <E>          Excel DTO 타입 (@ExcelSheet, @ExcelColumn 어노테이션 필요)
     * @param outputStream Excel 데이터가 출력될 스트림
     * @param fileName     파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가, 보안 검증 및 인코딩 적용)
     * @param queryParams  데이터 조회에 사용될 조건 객체
     * @param dataProvider 데이터 조회 함수 (queryParams → List&lt;R&gt;)
     * @param converter    데이터 변환 함수 (R → E)
     * @return 보안 검증 및 인코딩이 적용된 최종 파일명
     * @throws ExcelExporterException Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.excel(fos).fileName("customers.xlsx").write(queryParams, dataProvider, converter);
     * }</pre>
     */
    @Deprecated
    public static <Q, R, E> String excelFromList(OutputStream outputStream, String fileName,
                                                 Q queryParams,
                                                 ExcelDataProvider<Q, R> dataProvider,
                                                 Function<R, E> converter) {
        return excel(outputStream).fileName(fileName).write(queryParams, dataProvider, converter);
    }

    /**
     * 어노테이션 기반 Excel 생성 (데이터 조회 + 변환 통합, OutputStream 버전, 기본 파일명)
     * <p>데이터 조회부터 Excel 변환까지 한 번에 처리하여 OutputStream에 출력합니다.</p>
     * <p>파일명이 지정되지 않으면 "download_타임스탬프.xlsx" 형식으로 생성됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * try (FileOutputStream fos = new FileOutputStream("output.xlsx")) {
     *     String fileName = ExcelExporter.excelFromList(
     *         fos,
     *         request,
     *         customerService::getCustomerList,
     *         customerConverter::toExcelDTO
     *     );
     *     // fileName: "download_20250119_135348.xlsx"
     * } catch (ExcelExporterException ex) {
     *     // 에러 처리
     * }
     * }</pre>
     *
     * @param <Q>          조회 조건 타입 (Request 객체 등)
     * @param <R>          서비스 응답 데이터 타입 (Entity, Response 객체 등)
     * @param <E>          Excel DTO 타입 (@ExcelSheet, @ExcelColumn 어노테이션 필요)
     * @param outputStream Excel 데이터가 출력될 스트림
     * @param queryParams  데이터 조회에 사용될 조건 객체
     * @param dataProvider 데이터 조회 함수 (queryParams → List&lt;R&gt;)
     * @param converter    데이터 변환 함수 (R → E)
     * @return 생성된 파일명 (download_타임스탬프.xlsx)
     * @throws ExcelExporterException Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.excel(fos).write(queryParams, dataProvider, converter);
     * }</pre>
     */
    @Deprecated
    public static <Q, R, E> String excelFromList(OutputStream outputStream, Q queryParams,
                                                 ExcelDataProvider<Q, R> dataProvider,
                                                 Function<R, E> converter) {
        return excel(outputStream).write(queryParams, dataProvider, converter);
    }

    private static <Q, R, E> List<E> transformData(Q queryParams,
                                                   ExcelDataProvider<Q, R> dataProvider,
                                                   Function<R, E> converter) {
        List<R> responseData = dataProvider.getExcelData(queryParams);
        return responseData
                .stream()
                .map(converter)
                .collect(Collectors.toList());
    }

    private static <T> void validateData(List<T> data) {
        if (data == null || data.isEmpty()) {
            throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
        }
        if (data.size() > MAX_ROWS_FOR_LIST_API) {
            throw new ExcelExporterException(ErrorCode.EXCEED_MAX_ROWS,
                    String.format("데이터 크기: %,d건 (최대: %,d건). Stream API를 사용하세요: ExcelExporter.excelFromStream()",
                            data.size(), MAX_ROWS_FOR_LIST_API));
        }
    }

    private static <T> void writeWorkbookToStream(OutputStream outputStream, Stream<T> dataStream) {
        ExcelWriter writer = new ExcelWriter();
        writeWorkbookAndHandleErrors(outputStream,
                () -> writer.write(dataStream));
    }

    /**
     * 파일명 보안 검증 및 정제
     * <p>상세 로직은 {@link FilenameSecurityValidator#sanitizeFilename(String)} 참조</p>
     *
     * @param fileName 검증할 파일명
     * @return 안전하게 정제된 파일명 또는 기본 파일명
     */
    private static String encodeFileNameCommons(String fileName) {
        return FilenameSecurityValidator.sanitizeFilename(fileName);
    }

    /**
     * RFC 5987 방식으로 파일명 URL 인코딩
     */
    private static String urlEncodeRFC5987(String fileName) {
        try {
            String enc = URLEncoder.encode(fileName, "UTF-8");
            enc = enc
                    .replace("+", "%20")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%7E", "~");
            return enc;
        } catch (java.io.UnsupportedEncodingException e) {
            throw new ExcelExporterException(ErrorCode.IO_ERROR, "UTF-8 인코딩 지원 안 됨", e);
        }
    }

    /**
     * 멀티 시트 Excel 파일 다운로드 (List 기반, HttpServletResponse)
     * <p>여러 DTO를 각각의 시트로 생성하여 다운로드합니다.</p>
     * <p>시트 이름은 각 DTO의 @ExcelSheet.value()에서 추출되며, Map의 key는 단순 식별자로만 사용됩니다.</p>
     * <p>동일한 시트 이름을 가진 DTO들은 @ExcelColumn.order 순서대로 정렬되어 하나의 시트로 병합됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @PostMapping("/multiSheetExcel")
     * public void downloadExcel(HttpServletResponse response) {
     *     Map<String, List<?>> sheetData = new LinkedHashMap<>();
     *     sheetData.put("identifier1", customerService.getCustomers());  // @ExcelSheet("고객") -> "고객" 시트
     *     sheetData.put("identifier2", orderService.getOrders());        // @ExcelSheet("주문") -> "주문" 시트
     *     sheetData.put("identifier3", productService.getProducts());    // @ExcelSheet("고객") -> "고객" 시트에 병합
     *
     *     ExcelExporter.downloadExcel(response, "report.xlsx", sheetData);
     * }
     * }</pre>
     *
     * @param response     HTTP 응답 객체
     * @param fileName     다운로드될 파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가)
     * @param sheetDataMap 식별자-데이터 매핑 (순서 보장을 위해 LinkedHashMap 권장)
     * @throws ExcelExporterException 멀티 시트 Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(HttpServletResponse)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * ExcelExporter.excel(response).fileName("report.xlsx").write(sheetData);
     * }</pre>
     */
    @Deprecated
    public static void excelFromList(HttpServletResponse response, String fileName,
                                     Map<String, List<?>> sheetDataMap) {
        setupResponseAndWriteExcel(response, fileName,
                outputStream -> writeMultiSheetWorkbookToStream(outputStream, sheetDataMap));
    }

    /**
     * 멀티 시트 Excel 파일 작성 (List 기반, OutputStream)
     * <p>여러 DTO를 각각의 시트로 생성하여 OutputStream에 출력합니다.</p>
     * <p>시트 이름은 각 DTO의 @ExcelSheet.value()에서 추출되며, Map의 key는 단순 식별자로만 사용됩니다.</p>
     * <p>동일한 시트 이름을 가진 DTO들은 @ExcelColumn.order 순서대로 정렬되어 하나의 시트로 병합됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * Map<String, List<?>> sheetData = new LinkedHashMap<>();
     * sheetData.put("identifier1", customerList);  // @ExcelSheet("고객") -> "고객" 시트
     * sheetData.put("identifier2", orderList);     // @ExcelSheet("주문") -> "주문" 시트
     *
     * try (FileOutputStream fos = new FileOutputStream("report.xlsx")) {
     *     String fileName = ExcelExporter.excelFromList(fos, "report.xlsx", sheetData);
     * }
     * }</pre>
     *
     * @param outputStream Excel 데이터가 출력될 스트림
     * @param fileName     파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가, 보안 검증 및 인코딩 적용)
     * @param sheetDataMap 식별자-데이터 매핑 (순서 보장을 위해 LinkedHashMap 권장)
     * @return 보안 검증 및 인코딩이 적용된 최종 파일명
     * @throws ExcelExporterException 멀티 시트 Excel 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #excel(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.excel(fos).fileName("report.xlsx").write(sheetData);
     * }</pre>
     */
    @Deprecated
    public static String excelFromList(OutputStream outputStream, String fileName,
                                       Map<String, List<?>> sheetDataMap) {
        String sanitizedFileName = encodeFileNameCommons(fileName);
        String transFileName = getTransFileName(sanitizedFileName);
        writeMultiSheetWorkbookToStream(outputStream, sheetDataMap);
        return transFileName;
    }


    private static void writeMultiSheetWorkbookToStream(OutputStream outputStream,
                                                        Map<String, List<?>> sheetDataMap) {
        ExcelWriter writer = new ExcelWriter();
        writeWorkbookAndHandleErrors(outputStream, () -> writer.write(sheetDataMap));
    }

    private static void writeMultiSheetWorkbookFromStreams(OutputStream outputStream,
                                                           Map<String, Stream<?>> sheetStreamMap) {
        ExcelWriter writer = new ExcelWriter();
        writeWorkbookAndHandleErrors(outputStream, () -> writer.writeWithStreams(sheetStreamMap));
    }

    private static void setupResponseAndWriteExcel(HttpServletResponse response, String fileName,
                                                   OutputStreamWriter writer) {
        try {
            final String ascii = encodeFileNameCommons(fileName);
            String transFileName = getTransFileName(ascii);
            final String utf8 = urlEncodeRFC5987(transFileName);

            setResponseHeaders(response,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "download.xlsx",
                    utf8);

            writer.write(response.getOutputStream());
        } catch (java.io.IOException ioEx) {
            throw new ExcelExporterException(ErrorCode.IO_ERROR, ioEx);
        }
    }

    private static void writeWorkbookAndHandleErrors(OutputStream outputStream,
                                                     WorkbookSupplier workbookSupplier) {
        try (SXSSFWorkbook wb = workbookSupplier.get()) {
            wb.write(outputStream);
        } catch (ExcelExporterException ex) {
            throw ex;
        } catch (java.io.IOException ioEx) {
            throw new ExcelExporterException(ErrorCode.IO_ERROR, ioEx);
        } catch (Exception ex) {
            throw new ExcelExporterException(
                    ErrorCode.WORKBOOK_CREATION_FAILED,
                    "Excel 생성 중 예상치 못한 오류 발생",
                    ex
            );
        }
    }

    @FunctionalInterface
    private interface OutputStreamWriter {

        void write(OutputStream outputStream) throws IOException;
    }

    @FunctionalInterface
    private interface WorkbookSupplier {

        SXSSFWorkbook get() throws ExcelExporterException;
    }

    /**
     * 어노테이션 기반 CSV 파일 다운로드 (List 기반, HttpServletResponse)
     * <p>데이터 리스트를 CSV 파일로 변환하여 HTTP 응답으로 전송합니다.</p>
     * <p>@ExcelColumn.order 순서대로 컬럼이 정렬됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @PostMapping("/csvDown")
     * public void downloadCsv(HttpServletResponse response) {
     *     List<CustomerDTO> customers = service.getCustomers();
     *     ExcelExporter.csvFromList(response, "customers.csv", customers);
     * }
     * }</pre>
     *
     * @param <T>      CSV DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param response HTTP 응답 객체 (CSV 파일이 이 응답으로 전송됨)
     * @param fileName 다운로드될 파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가)
     * @param data     CSV로 변환할 데이터 리스트
     * @throws ExcelExporterException 데이터가 null이거나 비어있을 경우, 또는 CSV 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #csv(HttpServletResponse)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * ExcelExporter.csv(response).fileName("customers.csv").write(customers);
     * }</pre>
     */
    @Deprecated
    public static <T> void csvFromList(HttpServletResponse response, String fileName, List<T> data) {
        csv(response).fileName(fileName).write(data);
    }

    /**
     * 어노테이션 기반 CSV 파일을 OutputStream에 작성 (List 기반)
     * <p>데이터 리스트를 CSV 파일로 변환하여 제공된 OutputStream에 출력합니다.</p>
     * <p>@ExcelColumn.order 순서대로 컬럼이 정렬됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * try (FileOutputStream fos = new FileOutputStream("customers.csv")) {
     *     String fileName = ExcelExporter.csvFromList(fos, "customers.csv", customers);
     * }
     * }</pre>
     *
     * @param <T>          CSV DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param outputStream CSV 데이터가 출력될 스트림 (호출자가 스트림 닫기 책임)
     * @param fileName     파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가, 보안 검증 및 인코딩 적용)
     * @param data         CSV로 변환할 데이터 리스트
     * @return 보안 검증 및 인코딩이 적용된 최종 파일명
     * @throws ExcelExporterException 데이터가 null이거나 비어있을 경우, 또는 CSV 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #csv(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.csv(fos).fileName("customers.csv").write(customers);
     * }</pre>
     */
    @Deprecated
    public static <T> String csvFromList(OutputStream outputStream, String fileName, List<T> data) {
        return csv(outputStream).fileName(fileName).write(data);
    }

    /**
     * 어노테이션 기반 CSV 파일 다운로드 (Stream 기반, HttpServletResponse)
     * <p>Stream API를 사용하여 데이터를 한 번에 하나씩 처리합니다.</p>
     * <p>대용량 데이터 처리 시 List 전체 로딩 없이 메모리 효율적으로 처리 가능합니다.</p>
     * <p>@ExcelColumn.order 순서대로 컬럼이 정렬됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @PostMapping("/csvStreamDown")
     * public void downloadCsv(HttpServletResponse response) {
     *     Stream<CustomerDTO> dataStream = customerRepository.streamAllCustomers();
     *     ExcelExporter.csvFromStream(response, "customers.csv", dataStream);
     * }
     * }</pre>
     *
     * @param <T>        CSV DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param response   HTTP 응답 객체 (CSV 파일이 이 응답으로 전송됨)
     * @param fileName   다운로드될 파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가)
     * @param dataStream CSV로 변환할 데이터 스트림
     * @throws ExcelExporterException CSV 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #csv(HttpServletResponse)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * ExcelExporter.csv(response).fileName("customers.csv").write(dataStream);
     * }</pre>
     */
    @Deprecated
    public static <T> void csvFromStream(HttpServletResponse response, String fileName,
                                         Stream<T> dataStream) {
        csv(response).fileName(fileName).write(dataStream);
    }

    /**
     * 어노테이션 기반 CSV 파일을 OutputStream에 작성 (Stream 기반)
     * <p>Stream API를 사용하여 데이터를 한 번에 하나씩 처리합니다.</p>
     * <p>대용량 데이터 처리 시 List 전체 로딩 없이 메모리 효율적으로 처리 가능합니다.</p>
     * <p>@ExcelColumn.order 순서대로 컬럼이 정렬됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * try (FileOutputStream fos = new FileOutputStream("customers.csv")) {
     *     Stream<CustomerDTO> dataStream = customerRepository.streamAllCustomers();
     *     String fileName = ExcelExporter.csvFromStream(fos, "customers.csv", dataStream);
     * }
     * }</pre>
     *
     * @param <T>          CSV DTO 타입 (반드시 @ExcelSheet와 @ExcelColumn 어노테이션 필요)
     * @param outputStream CSV 데이터가 출력될 스트림 (호출자가 스트림 닫기 책임)
     * @param fileName     파일명 (명시적 파일명은 그대로 사용, 기본값 "download"만 타임스탬프 추가, 보안 검증 및 인코딩 적용)
     * @param dataStream   CSV로 변환할 데이터 스트림
     * @return 보안 검증 및 인코딩이 적용된 최종 파일명
     * @throws ExcelExporterException CSV 생성 중 오류 발생 시
     * @deprecated 3.0.0에서 삭제 예정. {@link #csv(OutputStream)} Fluent API를 사용하세요.
     * <pre>{@code
     * // 마이그레이션 예시
     * String fileName = ExcelExporter.csv(fos).fileName("customers.csv").write(dataStream);
     * }</pre>
     */
    @Deprecated
    public static <T> String csvFromStream(OutputStream outputStream, String fileName,
                                           Stream<T> dataStream) {
        return csv(outputStream).fileName(fileName).write(dataStream);
    }

    private static <T> void writeCsvFromList(OutputStream outputStream, List<T> data) {
        CsvWriter writer = new CsvWriter();
        writer.write(outputStream, data);
    }

    private static <T> void writeCsvFromStream(OutputStream outputStream, Stream<T> dataStream) {
        CsvWriter writer = new CsvWriter();
        writer.write(outputStream, dataStream);
    }

    private static void setupCsvResponseAndWrite(HttpServletResponse response, String fileName,
                                                 OutputStreamWriter writer) {
        try {
            final String ascii = encodeFileNameCommons(fileName);
            String transFileName = getTransFileNameWithExtension(ascii);
            final String utf8 = urlEncodeRFC5987(transFileName);

            setResponseHeaders(response,
                    "text/csv; charset=UTF-8",
                    "download.csv",
                    utf8);

            writer.write(response.getOutputStream());
        } catch (IOException ioEx) {
            throw new ExcelExporterException(ErrorCode.IO_ERROR, ioEx);
        }
    }

    private static void setResponseHeaders(HttpServletResponse response, String contentType,
                                           String fallbackFileName, String encodedFileName) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fallbackFileName + "\"; filename*=UTF-8''" + encodedFileName);
        setDefaultCacheControlIfAbsent(response);
    }

    private static void setDefaultCacheControlIfAbsent(HttpServletResponse response) {
        if (response.getHeader("Cache-Control") == null) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        }
    }

    private static String getTransFileNameWithExtension(String fileName) {
        if (isPersonalFileName(fileName)) {
            return ensureExtension(fileName);
        }

        String ts = LocalDateTime
                .now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nameWithoutExt = removeExtension(fileName);
        return nameWithoutExt + "_" + ts + ".csv";
    }

    private static String ensureExtension(String fileName) {
        return removeExtension(fileName) + ".csv";
    }

    private static String removeExtension(String fileName) {
        return fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;
    }


    /**
     * Excel 데이터 제공 함수형 인터페이스
     */
    @FunctionalInterface
    public interface ExcelDataProvider<T, R> {

        List<R> getExcelData(T queryParams);
    }
}
