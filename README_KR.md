# Excel Annotator

> 어노테이션 기반 Excel 생성 라이브러리 | Annotation-driven Excel Generation Library

**한국어** | **[English](README.md)**

[![Java](https://img.shields.io/badge/Java-1.8+-007396?style=flat&logo=java)](https://www.oracle.com/java/)
[![Apache POI](https://img.shields.io/badge/Apache%20POI-5.4.0-D22128?style=flat)](https://poi.apache.org/)
[![Version](https://img.shields.io/badge/version-1.0.4-blue.svg)](https://github.com/yourusername/excel-exporter)
[![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)](LICENSE)

**POI 코드 작성 없이 어노테이션만으로 Excel 파일을 생성하세요!**

---

## ⚡ 빠른 시작 (Quick Start)

### 1. Maven Dependency 추가

```xml
<dependency>
    <groupId>io.github.takoeats</groupId>
    <artifactId>excel-annotator</artifactId>
    <version>1.0.4</version>
</dependency>
```

### 2. DTO에 어노테이션 추가

```java
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.annotation.ExcelColumn;

@ExcelSheet("고객 목록")
public class CustomerDTO {
    @ExcelColumn(header = "고객ID", order = 1)
    private Long customerId;

    @ExcelColumn(header = "고객명", order = 2)
    private String customerName;

    @ExcelColumn(header = "이메일", order = 3)
    private String email;
}
```

### 3. Excel 다운로드

```java
import com.junho.excel.ExcelExporter;

@PostMapping("/download/customers")
public void downloadExcel(HttpServletResponse response) {
    List<CustomerDTO> customers = customerService.getCustomers();
    ExcelExporter.excelFromList(response, "고객목록.xlsx", customers);
}
```

**끝!** 🎉 브라우저에서 `고객목록.xlsx` 파일이 다운로드됩니다.

---

## 📖 API 진입점 (Entry Points)

ExcelExporter는 다양한 사용 사례를 위한 **17개의 정적 메서드**를 제공합니다.

### 전체 API 개요

#### List 기반 API (소규모 데이터 - 최대 1M 행)

| 메서드 시그니처 | 출력 | 파일명 | 설명 |
|--------------|------|--------|------|
| `excelFromList(response, fileName, list)` | HttpServletResponse | 필수 | 웹 다운로드 (단일 시트) |
| `excelFromList(response, fileName, map)` | HttpServletResponse | 필수 | 웹 다운로드 (멀티시트) |
| `excelFromList(outputStream, fileName, list)` | OutputStream | 필수 | 파일 저장 (단일 시트) |
| `excelFromList(outputStream, list)` | OutputStream | 자동 | 파일 저장 (자동 파일명) |
| `excelFromList(outputStream, fileName, map)` | OutputStream | 필수 | 파일 저장 (멀티시트) |

#### Data Provider 패턴 API (쿼리/변환 분리)

| 메서드 시그니처 | 출력 | 파일명 | 설명 |
|--------------|------|--------|------|
| `excelFromList(response, fileName, query, provider, converter)` | HttpServletResponse | 필수 | 웹 다운로드 (쿼리 분리) |
| `excelFromList(outputStream, fileName, query, provider, converter)` | OutputStream | 필수 | 파일 저장 (쿼리 분리) |
| `excelFromList(outputStream, query, provider, converter)` | OutputStream | 자동 | 파일 저장 (쿼리 분리, 자동 파일명) |

#### Stream 기반 API (대용량 데이터 - 100M+ 행 지원)

| 메서드 시그니처 | 출력 | 파일명 | 설명 |
|--------------|------|--------|------|
| `excelFromStream(response, fileName, stream)` | HttpServletResponse | 필수 | 웹 다운로드 (단일 시트 스트리밍) |
| `excelFromStream(response, fileName, streamMap)` | HttpServletResponse | 필수 | 웹 다운로드 (멀티시트 스트리밍) |
| `excelFromStream(outputStream, fileName, stream)` | OutputStream | 필수 | 파일 저장 (단일 시트 스트리밍) |
| `excelFromStream(outputStream, stream)` | OutputStream | 자동 | 파일 저장 (자동 파일명) |
| `excelFromStream(outputStream, fileName, streamMap)` | OutputStream | 필수 | 파일 저장 (멀티시트 스트리밍) |

#### CSV 기반 API (RFC 4180 표준 준수)

| 메서드 시그니처 | 출력 | 파일명 | 설명 |
|--------------|------|--------|------|
| `csvFromList(response, fileName, list)` | HttpServletResponse | 필수 | CSV 웹 다운로드 (List) |
| `csvFromList(outputStream, fileName, list)` | OutputStream | 필수 | CSV 파일 저장 (List) |
| `csvFromStream(response, fileName, stream)` | HttpServletResponse | 필수 | CSV 웹 다운로드 (Stream) |
| `csvFromStream(outputStream, fileName, stream)` | OutputStream | 필수 | CSV 파일 저장 (Stream) |

**📄 CSV 포맷 특징:**
- ✅ RFC 4180 표준 완벽 준수
- ✅ 모든 필드를 큰따옴표로 감싸기 (특수문자 안전 처리)
- ✅ CRLF (\r\n) 줄바꿈 사용
- ✅ UTF-8 BOM 포함 (Excel 호환성)
- ✅ 필드 내 줄바꿈, 쉼표, 따옴표 보존

**💡 선택 가이드:**
- **1만 건 이하**: List API 사용 (간단하고 빠름)
- **1만~100만 건**: Stream API 권장 (메모리 효율)
- **100만 건 초과**: Stream API 필수 (List는 1M 행 제한)
- **쿼리 재사용 필요**: Data Provider 패턴
- **간단한 데이터 교환**: CSV API 사용 (스타일 불필요, 범용성 높음)

---

## 📚 핵심 기능

### 1️⃣ 기본 Excel 생성

#### 1-1. HttpServletResponse로 웹 다운로드 (가장 일반적)
```java
@RestController
public class ExcelController {

    @GetMapping("/download/customers")
    public void downloadCustomers(HttpServletResponse response) {
        List<CustomerDTO> customers = customerService.getAllCustomers();

        // 브라우저에서 즉시 다운로드
        ExcelExporter.excelFromList(response, "고객목록.xlsx", customers);
        // 실제 다운로드: 고객목록.xlsx (명시적 파일명 - 타임스탬프 없음)
    }
}
```

#### 1-2. OutputStream으로 파일 저장
```java
// 파일명 지정
try (FileOutputStream fos = new FileOutputStream("output.xlsx")) {
    List<CustomerDTO> customers = customerService.getCustomers();
    String fileName = ExcelExporter.excelFromList(fos, "고객목록.xlsx", customers);
    System.out.println("생성 완료: " + fileName);
    // 출력: 생성 완료: 고객목록.xlsx (명시적 파일명 - 타임스탬프 없음)
}
```

#### 1-3. 파일명 자동 생성
```java
// 파일명 생략 시 "download_yyyyMMdd_HHmmss.xlsx" 자동 생성
try (FileOutputStream fos = new FileOutputStream("output.xlsx")) {
    List<CustomerDTO> customers = customerService.getCustomers();
    String fileName = ExcelExporter.excelFromList(fos, customers);
    System.out.println("생성 완료: " + fileName);
    // 출력: 생성 완료: download_20250108_143025.xlsx
}
```

#### 1-4. ByteArrayOutputStream으로 메모리 생성 (테스트/API 응답)
```java
// 메모리에서 생성 후 바이트 배열로 반환
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ExcelExporter.excelFromList(baos, "customers.xlsx", customers);

byte[] excelBytes = baos.toByteArray();

// 다른 API로 전송하거나 DB에 저장 가능
return ResponseEntity.ok()
    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.xlsx")
    .contentType(MediaType.APPLICATION_OCTET_STREAM)
    .body(excelBytes);
```

---

### 2️⃣ 스타일 적용

#### 사전 정의 스타일 사용

```java
import com.junho.excel.example.style.*;

@ExcelSheet("판매 내역")
public class SalesDTO {

    @ExcelColumn(
        header = "판매금액",
        order = 1,
        columnStyle = CurrencyStyle.class  // 통화 포맷: ₩#,##0
    )
    private BigDecimal amount;

    @ExcelColumn(
        header = "판매일",
        order = 2,
        columnStyle = DateOnlyStyle.class  // 날짜 포맷: yyyy-MM-dd
    )
    private LocalDate saleDate;

    @ExcelColumn(
        header = "달성률",
        order = 3,
        columnStyle = PercentageStyle.class  // 퍼센트 포맷: 0.00%
    )
    private Double achievementRate;
}
```


**주요 사전 정의 스타일:**

| 스타일 | 설명 | 포맷 |
|--------|------|------|
| `CurrencyStyle` | 통화 | ₩#,##0 |
| `DecimalNumberStyle` | 소수점 숫자 | #,##0.00 |
| `PercentageStyle` | 퍼센트 | 0.00% |
| `DateOnlyStyle` | 날짜 | yyyy-MM-dd |
| `DateTimeStyle` | 날짜+시간 | yyyy-MM-dd HH:mm:ss |
| `KoreanDateStyle` | 한글 날짜 | yyyy년 MM월 dd일 |
| `TableHeaderStyle` | 테이블 헤더 | 파란 배경 + 흰색 글자 |
| `CriticalAlertStyle` | 위험 경고 | 빨간 배경 + 흰색 글자 |
| `HighlightStyle` | 강조 | 노란 배경 |

#### 커스텀 스타일 생성

```java
import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.configurer.ExcelCellStyleConfigurer;

public class MyCustomStyle extends CustomExcelCellStyle {
    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
            .backgroundColor(144, 238, 144)  // RGB: Light Green
            .fontColor(0, 100, 0)            // RGB: Dark Green
            .bold(true)
            .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
            .numberFormat("#,##0");
    }
}
```

**사용:**
```java
@ExcelColumn(
    header = "매출",
    order = 1,
    columnStyle = MyCustomStyle.class
)
private BigDecimal revenue;
```

---

### 3️⃣ 조건부 스타일

#### 기본 조건부 스타일

```java
import com.junho.excel.annotation.ConditionalStyle;

@ExcelSheet("재무 리포트")
public class FinanceDTO {

    @ExcelColumn(
        header = "손익",
        order = 1,
        conditionalStyles = {
            @ConditionalStyle(
                when = "value < 0",                   // 음수일 때
                style = CriticalAlertStyle.class,     // 빨간 배경
                priority = 10
            )
        }
    )
    private BigDecimal profitLoss;
}
```

#### 복합 조건

```java
@ExcelColumn(
    header = "금액",
    order = 2,
    conditionalStyles = {
        // 우선순위 높음: 음수 → 빨간색
        @ConditionalStyle(
            when = "value < 0",
            style = CriticalAlertStyle.class,
            priority = 30
        ),
        // 중간: 백만 초과 → 노란색 강조
        @ConditionalStyle(
            when = "value > 1000000",
            style = HighlightStyle.class,
            priority = 20
        ),
        // 낮음: 정상 범위 → 녹색
        @ConditionalStyle(
            when = "value > 0 && value <= 1000000",
            style = SignatureStyle.class,
            priority = 10
        )
    }
)
private BigDecimal amount;
```

#### 문자열 조건

```java
@ExcelColumn(
    header = "상태",
    order = 3,
    conditionalStyles = {
        @ConditionalStyle(
            when = "value equals '완료' || value equals '승인'",
            style = SignatureStyle.class,
            priority = 10
        ),
        @ConditionalStyle(
            when = "value contains '진행'",
            style = HighlightStyle.class,
            priority = 9
        )
    }
)
private String status;
```

**지원 표현식:**

| 연산자 | 예시 | 설명 |
|--------|------|------|
| `<` `<=` `>` `>=` | `value > 100` | 숫자 비교 |
| `==` `equals` | `value equals 100` | 같음 |
| `!=` | `value != 0` | 다름 |
| `between` | `value between 10 and 100` | 범위 (10 이상 100 이하) |
| `contains` | `value contains 'text'` | 문자열 포함 |
| `is_null` | `value is_null` | Null 체크 |
| `is_empty` | `value is_empty` | 빈 문자열 |
| `is_negative` | `value is_negative` | 음수 |
| `&&` `\|\|` `!` | `value > 0 && value < 100` | 논리 연산자 |

---

### 4️⃣ 멀티시트 생성

#### 4-1. HttpServletResponse로 멀티시트 다운로드

```java
@PostMapping("/download/report")
public void downloadMultiSheetReport(HttpServletResponse response) {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();

    // 키는 식별자, 실제 시트명은 @ExcelSheet.value()에서 가져옴
    sheetData.put("customers", customerService.getCustomers());   // @ExcelSheet("고객 목록")
    sheetData.put("orders", orderService.getOrders());           // @ExcelSheet("주문 내역")
    sheetData.put("products", productService.getProducts());     // @ExcelSheet("상품 목록")

    // Map 버전 API 사용
    ExcelExporter.excelFromList(response, "통합_리포트.xlsx", sheetData);
}
```

**결과:** 3개의 시트를 가진 Excel 파일
- Sheet1: "고객 목록"
- Sheet2: "주문 내역"
- Sheet3: "상품 목록"

#### 4-2. OutputStream으로 멀티시트 파일 저장

```java
try (FileOutputStream fos = new FileOutputStream("report.xlsx")) {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("customers", customerList);
    sheetData.put("orders", orderList);

    // OutputStream + Map 버전 API
    String fileName = ExcelExporter.excelFromList(fos, "리포트.xlsx", sheetData);
    System.out.println("멀티시트 생성 완료: " + fileName);
}
```

#### 4-3. 같은 시트에 컬럼 병합

```java
// CustomerBasicDTO
@ExcelSheet("고객")
public class CustomerBasicDTO {
    @ExcelColumn(header = "ID", order = 1)
    private Long id;

    @ExcelColumn(header = "이름", order = 2)
    private String name;
}

// CustomerExtraDTO
@ExcelSheet("고객")  // 같은 시트명!
public class CustomerExtraDTO {
    @ExcelColumn(header = "이메일", order = 3)
    private String email;

    @ExcelColumn(header = "전화번호", order = 4)
    private String phone;
}

// 사용
Map<String, List<?>> data = new LinkedHashMap<>();
data.put("basic", customerBasicList);
data.put("extra", customerExtraList);

ExcelExporter.excelFromList(response, "고객.xlsx", data);
```

**결과:** 단일 시트 "고객"에 4개 컬럼 (ID, 이름, 이메일, 전화번호)

---

### 5️⃣ 대용량 데이터 (스트리밍 API)

#### 5-1. HttpServletResponse로 스트림 다운로드 (단일 시트)

```java
@PostMapping("/download/large-customers")
public void downloadLargeCustomers(HttpServletResponse response) {
    // JPA Repository에서 Stream 반환 (커서 기반)
    Stream<CustomerDTO> customerStream = customerRepository.streamAllCustomers();

    // Stream 버전 API 사용
    ExcelExporter.excelFromStream(response, "대용량_고객.xlsx", customerStream);
}
```

**장점:**
- ✅ 100만+ 행 처리 가능
- ✅ 메모리에 100행만 유지 (SXSSF)
- ✅ 전체 데이터를 메모리에 로드하지 않음

#### 5-2. OutputStream으로 스트림 파일 저장

```java
// 파일명 지정
try (FileOutputStream fos = new FileOutputStream("customers.xlsx");
     Stream<CustomerDTO> stream = customerRepository.streamAll()) {

    String fileName = ExcelExporter.excelFromStream(fos, "고객.xlsx", stream);
    System.out.println("대용량 파일 생성: " + fileName);
}

// 파일명 자동 생성
try (FileOutputStream fos = new FileOutputStream("customers.xlsx");
     Stream<CustomerDTO> stream = customerRepository.streamAll()) {

    String fileName = ExcelExporter.excelFromStream(fos, stream);
    System.out.println("대용량 파일 생성: " + fileName);
    // 출력: 대용량 파일 생성: download_20250108_143025.xlsx
}
```

#### 5-3. 멀티시트 스트리밍

```java
@PostMapping("/download/large-report")
public void downloadLargeReport(HttpServletResponse response) {
    Map<String, Stream<?>> sheetStreams = new LinkedHashMap<>();

    // 각 시트를 Stream으로 제공
    sheetStreams.put("customers", customerRepository.streamAll());
    sheetStreams.put("orders", orderRepository.streamAll());

    // Map<String, Stream<?>> 버전 API
    ExcelExporter.excelFromStream(response, "대용량_리포트.xlsx", sheetStreams);
}
```

#### 5-4. JPA Repository Stream 예제

```java
// Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    @Query("SELECT c FROM CustomerEntity c WHERE c.active = true")
    @QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "100"))
    Stream<CustomerEntity> streamActiveCustomers();
}

// Service
@Service
@Transactional(readOnly = true)
public class CustomerService {

    public void exportActiveCustomers(HttpServletResponse response) {
        try (Stream<CustomerEntity> stream = customerRepository.streamActiveCustomers()) {
            Stream<CustomerDTO> dtoStream = stream.map(this::toDTO);
            ExcelExporter.excelFromStream(response, "고객.xlsx", dtoStream);
        }
    }
}
```

#### 언제 Stream을 사용할까?

| 데이터 크기 | 권장 API | 이유 |
|------------|---------|------|
| 1만 건 이하 | `excelFromList()` | 간단, 빠름 |
| 1만~100만 건 | `excelFromStream()` | 메모리 효율 |
| 100만 건 초과 | `excelFromStream()` 필수 | List API는 1M 행 제한 |

---

### 6️⃣ CSV 파일 생성

어노테이션 기반으로 CSV 파일도 생성할 수 있습니다. RFC 4180 표준을 완벽히 준수합니다.

#### 6-1. HttpServletResponse로 CSV 다운로드

```java
@PostMapping("/download/customers-csv")
public void downloadCustomersAsCsv(HttpServletResponse response) {
    List<CustomerDTO> customers = customerService.getAllCustomers();

    // CSV 다운로드 (Excel과 동일한 DTO 사용)
    ExcelExporter.csvFromList(response, "고객목록.csv", customers);
    // 실제 다운로드: 고객목록.csv (명시적 파일명 - 타임스탬프 없음)
}
```

#### 6-2. OutputStream으로 CSV 파일 저장

```java
try (FileOutputStream fos = new FileOutputStream("customers.csv")) {
    List<CustomerDTO> customers = customerService.getCustomers();
    String fileName = ExcelExporter.csvFromList(fos, "고객.csv", customers);
    System.out.println("CSV 생성 완료: " + fileName);
}
```

#### 6-3. 대용량 CSV 스트리밍

```java
@PostMapping("/download/large-customers-csv")
public void downloadLargeCustomersAsCsv(HttpServletResponse response) {
    Stream<CustomerDTO> stream = customerRepository.streamAllCustomers();

    // 대용량 CSV 스트리밍
    ExcelExporter.csvFromStream(response, "대용량_고객.csv", stream);
}
```

**CSV 포맷 예시:**
```csv
"Name","Age","Salary"
"Alice","30","123.45"
"Bob","40","67.89"
"Charlie","25","50000.00"
```

**RFC 4180 준수 사항:**
- 모든 필드를 큰따옴표(`"`)로 감싸기
- 필드 내 큰따옴표는 `""`로 이스케이프
- 레코드 구분자는 CRLF(`\r\n`)
- 필드 내 줄바꿈, 쉼표 보존
- UTF-8 BOM 포함 (Excel 호환)

**Excel과 CSV 선택 기준:**

| 기준 | Excel | CSV |
|------|-------|-----|
| 스타일 필요 | ✅ | ❌ |
| 조건부 포맷 | ✅ | ❌ |
| 멀티시트 | ✅ | ❌ |
| 단순 데이터 교환 | ⚪ | ✅ |
| 파일 크기 | 큼 | 작음 |
| 범용성 | 보통 | 높음 |
| 처리 속도 | 보통 | 빠름 |

---

## 🔧 고급 사용법

### 7️⃣ Data Provider 패턴

쿼리 로직과 변환 로직을 분리하여 재사용성을 높이는 전용 API입니다.

#### API 시그니처
```java
// HttpServletResponse 버전
ExcelExporter.excelFromList(
    HttpServletResponse response,
    String fileName,
    Q queryParams,                        // 쿼리 파라미터 객체
    ExcelDataProvider<Q, R> dataProvider, // 데이터 조회 함수
    Function<R, E> converter              // Entity → DTO 변환 함수
)
```

#### 사용 예제

```java
// 1. Query Parameters DTO
@Data
public class CustomerSearchRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String customerType;
}

// 2. Service Layer
@Service
public class CustomerService {

    // Data Provider: 복잡한 쿼리 로직
    public List<CustomerEntity> searchCustomers(CustomerSearchRequest request) {
        return customerRepository.findByDateRangeAndType(
            request.getStartDate(),
            request.getEndDate(),
            request.getCustomerType()
        );
    }

    // Converter: Entity → DTO 변환
    public CustomerDTO toDTO(CustomerEntity entity) {
        return CustomerDTO.builder()
            .customerId(entity.getId())
            .customerName(entity.getName())
            .email(entity.getEmail())
            .build();
    }
}

// 3. Controller
@PostMapping("/download/customers/search")
public void downloadSearchResults(
    @RequestBody CustomerSearchRequest request,
    HttpServletResponse response
) {
    // 세 가지 관심사 분리: 쿼리, 조회, 변환
    ExcelExporter.excelFromList(
        response,
        "검색결과.xlsx",
        request,                          // Q: Query params
        customerService::searchCustomers,  // ExcelDataProvider<Q, R>
        customerService::toDTO             // Function<R, E>
    );
}
```

**장점:**
- ✅ 쿼리 로직 재사용 (다른 API에서도 `searchCustomers()` 사용 가능)
- ✅ 변환 로직 재사용 (다른 API에서도 `toDTO()` 사용 가능)
- ✅ 테스트 용이성 (각 함수를 독립적으로 테스트)
- ✅ 코드 가독성 (관심사 분리)

### 8️⃣ 컬럼 너비 설정

```java
@ExcelSheet("고객")
public class CustomerDTO {

    @ExcelColumn(
        header = "고객명",
        order = 1,
        width = 150  // 픽셀 단위로 명시적 지정
    )
    private String customerName;

    @ExcelColumn(
        header = "이메일",
        order = 2
        // width 생략 시 자동 계산
    )
    private String email;
}
```

### 9️⃣ 헤더 제어

#### 헤더 없는 시트
```java
@ExcelSheet(value = "데이터", hasHeader = false)  // 헤더 행 생략
public class DataDTO {
    @ExcelColumn(header = "ID", order = 1)  // header는 필수지만 출력되지 않음
    private Long id;

    @ExcelColumn(header = "Name", order = 2)
    private String name;
}
```

#### 커스텀 헤더 스타일
```java
@ExcelColumn(
    header = "총액",
    order = 1,
    headerStyle = MyCustomHeaderStyle.class,  // 헤더 셀 스타일
    columnStyle = CurrencyStyle.class         // 데이터 셀 스타일
)
private BigDecimal totalAmount;
```

### 🔟 시트 순서 지정

```java
@ExcelSheet(value = "요약", order = 1)  // 첫 번째 시트
public class SummaryDTO { ... }

@ExcelSheet(value = "상세", order = 2)  // 두 번째 시트
public class DetailDTO { ... }

@ExcelSheet(value = "참고")  // order 없음 → 가장 앞쪽 배치
public class ReferenceDTO { ... }
```

**정렬 규칙:**
1. `order` 없는 시트 먼저 (입력 순서대로)
2. `order` 있는 시트는 오름차순 정렬

**결과 시트 순서:** 참고 → 요약 → 상세

---

## ❓ FAQ

### Q1: List와 Stream은 언제 사용하나요?

**A:** 데이터 크기에 따라 선택하세요.
- **1만 건 이하**: `excelFromList()` (간단, 빠름)
- **1만 건 초과**: `excelFromStream()` (메모리 효율)
- **100만 건 초과**: `excelFromStream()` 필수 (List는 1M 제한)

### Q2: 파일명에 타임스탬프는 언제 추가되나요?

**A:** **기본 파일명에만** 충돌 방지를 위해 타임스탬프가 추가됩니다.

```java
// 명시적 파일명 → 타임스탬프 없음
ExcelExporter.excelFromList(response, "report.xlsx", data);
// 실제 다운로드: report.xlsx

// 기본 파일명 → 타임스탬프 추가
ExcelExporter.excelFromList(outputStream, data);  // 또는 "download"
// 결과: download_20250119_143025.xlsx

// 이미 타임스탬프 패턴 존재 → 중복 추가 안 함
ExcelExporter.excelFromList(response, "report_20251219_132153.xlsx", data);
// 실제 다운로드: report_20251219_132153.xlsx
```

### Q3: 조건부 스타일 우선순위는 어떻게 동작하나요?

**A:** `priority` 값이 **높을수록** 우선 적용됩니다.

```java
@ExcelColumn(
    conditionalStyles = {
        @ConditionalStyle(when = "value < 0", style = RedStyle.class, priority = 30),
        @ConditionalStyle(when = "value < -1000", style = DarkRedStyle.class, priority = 20)
    }
)
```

값이 -2000일 때:
- 두 조건 모두 만족
- priority 30 > 20 → `RedStyle` 적용

### Q4: 어노테이션 없는 필드는 어떻게 되나요?

**A:** `@ExcelColumn`이 없는 필드는 Excel에 포함되지 않습니다.

```java
@ExcelSheet("고객")
public class CustomerDTO {
    @ExcelColumn(header = "ID", order = 1)
    private Long id;

    private String internalCode;  // Excel에 포함되지 않음
}
```

### Q5: 빈 데이터로 Excel을 생성할 수 있나요?

**A:** 아니요. 빈 리스트/스트림은 `ExcelExporterException` (E001)을 발생시킵니다.

**해결:**
```java
List<CustomerDTO> customers = customerService.getCustomers();
if (customers.isEmpty()) {
    throw new CustomException("No customers found");
}
ExcelExporter.excelFromList(response, "customers.xlsx", customers);
```

### Q6: 멀티시트 병합 규칙은?

**A:** `@ExcelSheet.value()`가 같으면 하나의 시트로 병합됩니다.

```java
// DTO A: @ExcelSheet("고객") + order=1,2
// DTO B: @ExcelSheet("고객") + order=3,4
// 결과: 단일 시트 "고객"에 컬럼 4개 (order: 1,2,3,4)
```

### Q7: 64K 스타일 제한은 어떻게 회피하나요?

**A:** 라이브러리가 자동으로 스타일을 캐싱하여 중복을 제거합니다.

**조언:**
- 조건부 스타일을 최소화하세요 (범위로 통합)
- 유사한 스타일은 하나로 병합하세요

### Q8: 여러 스레드에서 동시에 사용해도 안전한가요?

**A:** 네, 스레드 안전합니다.

```java
@Async
public void exportCustomers(Long userId, HttpServletResponse response) {
    List<CustomerDTO> customers = customerService.getCustomersByUser(userId);
    ExcelExporter.excelFromList(response, "customers.xlsx", customers);
}
```

---

## 🛠️ 에러 처리

### 주요 에러 코드

| 코드 | 메시지 | 해결 방법 |
|------|--------|----------|
| E001 | Empty data collection | 빈 데이터 체크 후 처리 |
| E005 | No @ExcelSheet annotation | DTO에 `@ExcelSheet` 추가 |
| E006 | No @ExcelColumn fields | 최소 1개 `@ExcelColumn` 필드 추가 |
| E016 | Exceeded maximum rows for List API | Stream API 사용 |
| E017 | Stream already consumed | 새 스트림 생성 |

### Try-Catch 예제

```java
@PostMapping("/download/customers")
public ResponseEntity<?> downloadCustomers(HttpServletResponse response) {
    try {
        List<CustomerDTO> customers = customerService.getCustomers();
        ExcelExporter.excelFromList(response, "고객목록.xlsx", customers);
        return ResponseEntity.ok().build();

    } catch (ExcelExporterException ex) {
        log.error("Excel export failed: {}", ex.getMessage(), ex);

        switch (ex.getCode()) {
            case "E001":
                return ResponseEntity.badRequest()
                    .body("데이터가 없습니다.");
            case "E016":
                return ResponseEntity.badRequest()
                    .body("데이터가 너무 많습니다. 기간을 줄여주세요.");
            default:
                return ResponseEntity.internalServerError()
                    .body("Excel 생성 오류: " + ex.getMessage());
        }
    }
}
```

---

## 📦 설치 (Installation)

### Maven

```xml
<dependency>
    <groupId>io.github.takoeats</groupId>
    <artifactId>excel-annotator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.github.takoeats:excel-annotator:1.0.0'
```

### 필요 의존성

| 라이브러리 | 버전 | 설명 |
|-----------|------|------|
| Apache POI | 5.4.0 | Excel 파일 조작 |
| Commons Lang3 | 3.18.0 | 문자열 유틸리티 |
| SLF4J API | 2.0.17 | 로깅 API |
| Servlet API | 3.1.0 (provided) | HttpServletResponse |
| Lombok | 1.18.30 (provided) | 보일러플레이트 제거 |

---

## 🎯 실전 예제

### 1. Spring Boot 컨트롤러

```java
@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final CustomerService customerService;

    @GetMapping("/customers")
    public void downloadCustomers(HttpServletResponse response) {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        ExcelExporter.excelFromList(response, "고객목록.xlsx", customers);
    }

    @GetMapping("/monthly-report")
    public void downloadMonthlyReport(
        @RequestParam int year,
        @RequestParam int month,
        HttpServletResponse response
    ) {
        Map<String, List<?>> report = new LinkedHashMap<>();
        report.put("customers", customerService.getCustomersByMonth(year, month));
        report.put("orders", orderService.getOrdersByMonth(year, month));

        String fileName = String.format("월간리포트_%d년%d월.xlsx", year, month);
        ExcelExporter.excelFromList(response, fileName, report);
    }
}
```

### 2. 조건부 스타일이 적용된 재무 리포트

```java
@Data
@ExcelSheet("재무 요약")
public class FinancialSummaryDTO {

    @ExcelColumn(header = "항목", order = 1)
    private String category;

    @ExcelColumn(
        header = "금액",
        order = 2,
        columnStyle = CurrencyStyle.class,
        conditionalStyles = {
            @ConditionalStyle(
                when = "value < 0",
                style = CriticalAlertStyle.class,
                priority = 30
            ),
            @ConditionalStyle(
                when = "value > 10000000",
                style = HighlightStyle.class,
                priority = 20
            )
        }
    )
    private BigDecimal amount;

    @ExcelColumn(
        header = "증감율",
        order = 3,
        columnStyle = PercentageStyle.class,
        conditionalStyles = {
            @ConditionalStyle(
                when = "value < -0.1",  // -10% 이하
                style = CriticalAlertStyle.class,
                priority = 20
            ),
            @ConditionalStyle(
                when = "value > 0.2",   // +20% 이상
                style = SignatureStyle.class,
                priority = 10
            )
        }
    )
    private Double changeRate;
}
```

### 3. 대용량 배치 처리

```java
@Service
@RequiredArgsConstructor
public class ExcelBatchService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public String exportAllCustomers() throws Exception {
        String outputPath = "/batch/output/customers.xlsx";

        try (FileOutputStream fos = new FileOutputStream(outputPath);
             Stream<CustomerEntity> stream = customerRepository.streamAll()) {

            Stream<CustomerDTO> dtoStream = stream.map(this::toDTO);
            String fileName = ExcelExporter.excelFromStream(fos, "customers.xlsx", dtoStream);

            log.info("Batch export completed: {}", fileName);
            return fileName;
        }
    }
}
```

---

## 🔒 보안 기능

### 자동 파일명 보안 (Filename Sanitization)

사용자가 전달한 파일명은 **화이트리스트 기반 검증 → 정제 → 의미 검증**을 거쳐 처리됩니다.  
위험하거나 의미 없는 파일명은 **자동으로 안전한 기본 파일명으로 대체**됩니다.

---

### ❌ 위험한 입력 예시

(Java 코드 예시)

ExcelExporter.excelFromList(response, "../../../etc/passwd.xlsx", data);

처리 결과

download_20251216_143025.xlsx

경로 탐색(Path Traversal) 패턴이 감지되면  
부분 정제 없이 즉시 차단 후 기본 파일명으로 대체됩니다.

---

### ❌ 의미 없는 파일명 예시

(Java 코드 예시)

ExcelExporter.excelFromList(response, "!!!@@@###", data);

처리 결과

download_20251216_143025.xlsx

- 모든 문자가 제거·치환되어 의미가 사라진 경우
- 언더스코어(_)만 남는 경우  
  → 기본 파일명 적용

---

### ✅ 다국어 파일명 지원

다음 언어의 파일명은 허용됩니다.

- 한국어 (가–힣)
- 일본어 (히라가나, 가타카나)
- 중국어 (CJK 통합 한자)
- 서유럽 문자 (악센트 문자)

(Java 코드 예시)

ExcelExporter.excelFromList(response, "매출보고서.xlsx", data);

처리 결과

매출보고서_20251216_143025.xlsx

---

### 🚫 차단되는 패턴

다음 패턴이 하나라도 감지되면 **즉시 기본 파일명으로 대체**됩니다.

- 경로 탐색(Path Traversal)  
  .., /, \, :
- 숨김 파일  
  .으로 시작하는 파일명
- 제어 문자  
  \x00–\x1F, \x7F
- URL 인코딩 공격  
  %2e, %2f, %5c, %00
- OS 예약 파일명
    - Windows: CON, PRN, AUX, NUL, COM1–9, LPT1–9
    - Unix/Linux: null, stdin, stdout, stderr, random 등
- 파일명 길이 제한  
  최대 200자 초과 시 자동 절단

---

### 📌 처리 원칙 요약

- 화이트리스트 기반 허용
- 위험 패턴은 정제하지 않고 즉시 차단
- 의미 없는 결과는 기본 파일명 사용
- 확장자 및 timestamp는 검증 이후 시스템에서 부여

---

## 📄 라이선스

본 프로젝트는 **Apache-2.0** 라이선스를 따릅니다.

---

## 🤝 기여하기

버그 리포트 및 기능 요청은 [GitHub Issues](https://github.com/takoeats/excel-annotator/issues)에 등록해주세요.

---

<div align="center">

**⭐ 이 프로젝트가 유용하셨다면 Star를 눌러주세요! ⭐**

Made with ❤️ by [Junho](https://github.com/takoeats)

</div>
