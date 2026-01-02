# Excel Annotator

✨ Export Excel files the declarative way

## Why Annotations?

❌ Before (POI):
// 50 lines of boilerplate...

✅ After (Excel Annotator):
@ExcelColumn(header = "Name")
private String name;

If you love `@RestController` and `@Service`,
you'll love Excel Annotator.

**[한국어](README_KR.md)** | **English**

[![Java](https://img.shields.io/badge/Java-1.8+-007396?style=flat&logo=java)](https://www.oracle.com/java/)
[![Apache POI](https://img.shields.io/badge/Apache%20POI-5.4.0-D22128?style=flat)](https://poi.apache.org/)
[![Version](https://img.shields.io/badge/version-2.3.1-blue.svg)](https://github.com/takoeats/excel-annotator)
[![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)](LICENSE)

**Generate Excel files with annotations only - no POI code required!**

---

## ⚡ Quick Start

### 1. Add Maven Dependency

```xml
<dependency>
    <groupId>io.github.takoeats</groupId>
    <artifactId>excel-annotator</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 2. Add Annotations to DTO

```java
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.annotation.ExcelColumn;

@ExcelSheet("Customer List")
public class CustomerDTO {
    @ExcelColumn(header = "Customer ID", order = 1)
    private Long customerId;

    @ExcelColumn(header = "Name", order = 2)
    private String customerName;

    @ExcelColumn(header = "Email", order = 3)
    private String email;
}
```

**Or use `autoColumn` for even simpler setup:**

```java
import io.github.takoeats.excelannotator.annotation.ExcelSheet;

@ExcelSheet(value = "Customer List", autoColumn = true)
public class CustomerDTO {
    private Long customerId;     // Auto-exported: header = "customerId"
    private String customerName; // Auto-exported: header = "customerName"
    private String email;        // Auto-exported: header = "email"
}
```

### 3. Download Excel

```java
import io.github.takoeats.excelannotator.ExcelExporter;

@PostMapping("/download/customers")
public void downloadExcel(HttpServletResponse response) {
    List<CustomerDTO> customers = customerService.getCustomers();

    // Fluent API (Recommended)
    ExcelExporter.excel(response)
        .fileName("customers.xlsx")
        .write(customers);  // Return value (final filename) can be ignored
}
```

**Done!** 🎉 The browser downloads `customers.xlsx`.

---

## 📖 API Entry Points

### ✨ Fluent API

Simple, intuitive builder pattern for all export scenarios:

#### Excel Export

```java
// HttpServletResponse (Web Download)
ExcelExporter.excel(response)
    .fileName("customers.xlsx")
    .write(customerList);  // Return value (final filename) can be ignored

ExcelExporter.excel(response)
    .fileName("customers.xlsx")
    .write(customerStream);  // Return value (final filename) can be ignored

ExcelExporter.excel(response)
    .fileName("report.xlsx")
    .write(multiSheetMap);  // Return value (final filename) can be ignored

ExcelExporter.excel(response)
    .fileName("customers.xlsx")
    .write(query, dataProvider, converter);  // Return value (final filename) can be ignored

// OutputStream (File Save)
String fileName = ExcelExporter.excel(outputStream)
    .fileName("customers.xlsx")
    .write(customerList);  // Returns processed filename
```

#### CSV Export

```java
// HttpServletResponse (Web Download)
// List
ExcelExporter.csv(response)
    .fileName("customers.csv")
    .write(customerList);  // Return value (final filename) can be ignored

// Stream
ExcelExporter.csv(response)
    .fileName("customers.csv")
    .write(customerStream);  // Return value (final filename) can be ignored

// OutputStream (File Save)
String fileName = ExcelExporter.csv(outputStream)
    .fileName("customers.csv")
    .write(customerList);
```

**Key Benefits:**

- **Type-safe**: Compile-time guarantees for return types
- **Unified interface**: Same pattern for Response/OutputStream, Excel/CSV
- **Flexible data**: Supports List, Stream, Map (mixed List/Stream values)
- **Cleaner code**: No method name confusion (`excelFromList` vs `excelFromStream`)

---

### 📚 Legacy API (Deprecated ⚠️)

> **⚠️ Deprecation Notice:** The legacy static methods (`excelFromList`, `excelFromStream`, etc.) are deprecated and
> will be removed in version **3.0.0**. Please migrate to the Fluent API above for better type safety and readability.

ExcelExporter provides **17 static methods** (deprecated) for various use cases.

#### List-based API (Small datasets - max 1M rows)

| Method Signature                              | Output              | Filename | Description                 |
|-----------------------------------------------|---------------------|----------|-----------------------------|
| `excelFromList(response, fileName, list)`     | HttpServletResponse | Required | Web download (single sheet) |
| `excelFromList(response, fileName, map)`      | HttpServletResponse | Required | Web download (multi-sheet)  |
| `excelFromList(outputStream, fileName, list)` | OutputStream        | Required | File save (single sheet)    |
| `excelFromList(outputStream, list)`           | OutputStream        | Auto     | File save (auto filename)   |
| `excelFromList(outputStream, fileName, map)`  | OutputStream        | Required | File save (multi-sheet)     |

#### Data Provider Pattern API (Separate query/transform)

| Method Signature                                                    | Output              | Filename | Description                                |
|---------------------------------------------------------------------|---------------------|----------|--------------------------------------------|
| `excelFromList(response, fileName, query, provider, converter)`     | HttpServletResponse | Required | Web download (query separated)             |
| `excelFromList(outputStream, fileName, query, provider, converter)` | OutputStream        | Required | File save (query separated)                |
| `excelFromList(outputStream, query, provider, converter)`           | OutputStream        | Auto     | File save (query separated, auto filename) |

#### Stream-based API (Large datasets - 100M+ rows supported)

| Method Signature                                     | Output              | Filename | Description                           |
|------------------------------------------------------|---------------------|----------|---------------------------------------|
| `excelFromStream(response, fileName, stream)`        | HttpServletResponse | Required | Web download (single sheet streaming) |
| `excelFromStream(response, fileName, streamMap)`     | HttpServletResponse | Required | Web download (multi-sheet streaming)  |
| `excelFromStream(outputStream, fileName, stream)`    | OutputStream        | Required | File save (single sheet streaming)    |
| `excelFromStream(outputStream, stream)`              | OutputStream        | Auto     | File save (auto filename)             |
| `excelFromStream(outputStream, fileName, streamMap)` | OutputStream        | Required | File save (multi-sheet streaming)     |

#### CSV-based API (RFC 4180 Compliant)

| Method Signature                                | Output              | Filename | Description               |
|-------------------------------------------------|---------------------|----------|---------------------------|
| `csvFromList(response, fileName, list)`         | HttpServletResponse | Required | CSV web download (List)   |
| `csvFromList(outputStream, fileName, list)`     | OutputStream        | Required | CSV file save (List)      |
| `csvFromStream(response, fileName, stream)`     | HttpServletResponse | Required | CSV web download (Stream) |
| `csvFromStream(outputStream, fileName, stream)` | OutputStream        | Required | CSV file save (Stream)    |

**📄 CSV Format Features:**

- ✅ RFC 4180 standard fully compliant
- ✅ All fields quoted (safe special character handling)
- ✅ CRLF (\r\n) line breaks
- ✅ UTF-8 BOM included (Excel compatibility)
- ✅ Preserves newlines, commas, quotes within fields

**💡 Selection Guide:**

- **< 10K rows**: List API (simple, fast)
- **10K~1M rows**: Stream API recommended (memory efficient)
- **> 1M rows**: Stream API required (List has 1M limit)
- **Query reuse needed**: Data Provider pattern
- **Simple data exchange**: CSV API (no styling, high compatibility)

---

## 📚 Core Features

### 1️⃣ Basic Excel Generation

#### 1-1. Web Download with HttpServletResponse (Most Common)

```java
@RestController
public class ExcelController {

    @GetMapping("/download/customers")
    public void downloadCustomers(HttpServletResponse response) {
        // 💡 Library preserves user-set headers (security tokens, etc.)
        // response.setHeader("X-Custom-Token", securityToken); // This header will be kept

        List<CustomerDTO> customers = customerService.getAllCustomers();

        // Download immediately in browser using Fluent API
        ExcelExporter.excel(response)
            .fileName("customers.xlsx")
            .write(customers);  // Return value (final filename) can be ignored
        // Actual download: customers.xlsx (explicit filename - no timestamp)

        // 📌 Headers automatically set by library:
        // - Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
        // - Content-Disposition: attachment; filename="..."
        // - Cache-Control: no-store, no-cache (only if user hasn't set it)
    }
}
```

#### 1-2. Save to File with OutputStream

```java
// Specify filename
try (FileOutputStream fos = new FileOutputStream("output.xlsx")) {
    List<CustomerDTO> customers = customerService.getCustomers();
    String fileName = ExcelExporter.excel(fos)
        .fileName("customers.xlsx")
        .write(customers);
    System.out.println("Created: " + fileName);
    // Output: Created: customers.xlsx (explicit filename - no timestamp)
}
```

#### 1-3. Auto-generated Filename

```java
// Auto-generates "download_yyyyMMdd_HHmmss.xlsx" if fileName() is not called
try (FileOutputStream fos = new FileOutputStream("output.xlsx")) {
    List<CustomerDTO> customers = customerService.getCustomers();
    String fileName = ExcelExporter.excel(fos)
        .write(customers);  // No fileName() call → auto-generated
    System.out.println("Created: " + fileName);
    // Output: Created: download_20250108_143025.xlsx
}
```

#### 1-4. In-memory Generation with ByteArrayOutputStream (Test/API Response)

```java
// Generate in memory and return as byte array
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ExcelExporter.excel(baos)
    .fileName("customers.xlsx")
    .write(customers);

byte[] excelBytes = baos.toByteArray();

// Can send to other APIs or save to DB
return ResponseEntity.ok()
    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.xlsx")
    .contentType(MediaType.APPLICATION_OCTET_STREAM)
    .body(excelBytes);
```

---

### 2️⃣ Styling

#### Creating Custom Styles

Create reusable styles by extending `CustomExcelCellStyle`:

**Example: Currency Style**

```java
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class CurrencyStyle extends CustomExcelCellStyle {
    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .dataFormat("₩#,##0")  // or "$#,##0" for USD
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);
    }
}
```

**Example: Date Style**

```java
public class DateOnlyStyle extends CustomExcelCellStyle {
    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .dataFormat("yyyy-MM-dd")
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }
}
```

**Example: Percentage Style**

```java
public class PercentageStyle extends CustomExcelCellStyle {
    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .dataFormat("0.00%")
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);
    }
}
```

**Example: Alert Style**

```java
import io.github.takoeats.excelannotator.style.FontStyle;

public class CriticalAlertStyle extends CustomExcelCellStyle {
    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(220, 20, 60)  // Crimson
                .fontColor(255, 255, 255)      // White
                .font("Arial", 11, FontStyle.BOLD)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }
}
```

> **Important**: Use appropriate field types for formatted columns:
> - Currency/Numeric styles → `BigDecimal`, `Integer`, `Long`, `Double`
> - Date styles → `LocalDate`, `LocalDateTime`, `Date`
> - Percentage styles → `Double` or `BigDecimal`

**Usage in DTO:**

```java
@ExcelSheet("Sales Records")
public class SalesDTO {

    @ExcelColumn(
            header = "Amount",
            order = 1,
            columnStyle = CurrencyStyle.class
    )
    private BigDecimal amount;

    @ExcelColumn(
            header = "Sale Date",
            order = 2,
            columnStyle = DateOnlyStyle.class
    )
    private LocalDate saleDate;

    @ExcelColumn(
            header = "Achievement Rate",
            order = 3,
            columnStyle = PercentageStyle.class
    )
    private Double achievementRate;
}
```

---

### 3️⃣ Conditional Styling

#### Basic Conditional Style

```java
import io.github.takoeats.excelannotator.annotation.ConditionalStyle;

@ExcelSheet("Financial Report")
public class FinanceDTO {

    @ExcelColumn(
            header = "Profit/Loss",
            order = 1,
            conditionalStyles = {
                    @ConditionalStyle(
                            when = "value < 0",                   // When negative
                            style = CriticalAlertStyle.class,     // Red background
                            priority = 10
                    )
            }
    )
    private BigDecimal profitLoss;
}
```

#### Complex Conditions

```java
@ExcelColumn(
    header = "Amount",
    order = 2,
    conditionalStyles = {
        // Highest priority: negative → red
        @ConditionalStyle(
            when = "value < 0",
            style = CriticalAlertStyle.class,
            priority = 30
        ),
        // Medium: over million → yellow highlight
        @ConditionalStyle(
            when = "value > 1000000",
            style = HighlightStyle.class,
            priority = 20
        ),
        // Low: normal range → green
        @ConditionalStyle(
            when = "value > 0 && value <= 1000000",
            style = SignatureStyle.class,
            priority = 10
        )
    }
)
private BigDecimal amount;
```

#### String Conditions

```java
@ExcelColumn(
    header = "Status",
    order = 3,
    conditionalStyles = {
        @ConditionalStyle(
            when = "value equals 'Complete' || value equals 'Approved'",
            style = SignatureStyle.class,
            priority = 10
        ),
        @ConditionalStyle(
            when = "value contains 'In Progress'",
            style = HighlightStyle.class,
            priority = 9
        )
    }
)
private String status;
```

**Supported Expressions:**

| Operator          | Example                    | Description              |
|-------------------|----------------------------|--------------------------|
| `<` `<=` `>` `>=` | `value > 100`              | Numeric comparison       |
| `==` `equals`     | `value equals 100`         | Equality                 |
| `!=`              | `value != 0`               | Inequality               |
| `between`         | `value between 10 and 100` | Range (10 ≤ value ≤ 100) |
| `contains`        | `value contains 'text'`    | String contains          |
| `is_null`         | `value is_null`            | Null check               |
| `is_empty`        | `value is_empty`           | Empty string             |
| `is_negative`     | `value is_negative`        | Negative number          |
| `&&` `\|\|` `!`   | `value > 0 && value < 100` | Logical operators        |

---

### 4️⃣ Multi-sheet Creation

#### 4-1. Multi-sheet Download with HttpServletResponse

```java
@PostMapping("/download/report")
public void downloadMultiSheetReport(HttpServletResponse response) {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();

    // Keys are identifiers, actual sheet names come from @ExcelSheet.value()
    sheetData.put("customers", customerService.getCustomers());   // @ExcelSheet("Customers")
    sheetData.put("orders", orderService.getOrders());           // @ExcelSheet("Orders")
    sheetData.put("products", productService.getProducts());     // @ExcelSheet("Products")

    // Use Fluent API with Map
    ExcelExporter.excel(response)
        .fileName("integrated_report.xlsx")
        .write(sheetData);  // Return value (final filename) can be ignored
}
```

**Result:** Excel file with 3 sheets

- Sheet1: "Customers"
- Sheet2: "Orders"
- Sheet3: "Products"

#### 4-2. Multi-sheet File Save with OutputStream

```java
try (FileOutputStream fos = new FileOutputStream("report.xlsx")) {
    Map<String, List<?>> sheetData = new LinkedHashMap<>();
    sheetData.put("customers", customerList);
    sheetData.put("orders", orderList);

    // Fluent API with OutputStream + Map
    String fileName = ExcelExporter.excel(fos)
        .fileName("report.xlsx")
        .write(sheetData);
    System.out.println("Multi-sheet created: " + fileName);
}
```

#### 4-3. Merging Columns in Same Sheet

```java
// CustomerBasicDTO
@ExcelSheet("Customers")
public class CustomerBasicDTO {
    @ExcelColumn(header = "ID", order = 1)
    private Long id;

    @ExcelColumn(header = "Name", order = 2)
    private String name;
}

// CustomerExtraDTO
@ExcelSheet("Customers")  // Same sheet name!
public class CustomerExtraDTO {
    @ExcelColumn(header = "Email", order = 3)
    private String email;

    @ExcelColumn(header = "Phone", order = 4)
    private String phone;
}

// Usage
Map<String, List<?>> data = new LinkedHashMap<>();
data.put("basic", customerBasicList);
data.put("extra", customerExtraList);

ExcelExporter.excel(response)
    .fileName("customers.xlsx")
    .write(data);  // Return value (final filename) can be ignored
```

**Result:** Single sheet "Customers" with 4 columns (ID, Name, Email, Phone)

---

### 5️⃣ Large Datasets (Streaming API)

#### 5-1. Stream Download with HttpServletResponse (Single Sheet)

```java
@PostMapping("/download/large-customers")
public void downloadLargeCustomers(HttpServletResponse response) {
    // JPA Repository returns Stream (cursor-based)
    Stream<CustomerDTO> customerStream = customerRepository.streamAllCustomers();

    // Use Fluent API with Stream
    ExcelExporter.excel(response)
        .fileName("large_customers.xlsx")
        .write(customerStream);  // Return value (final filename) can be ignored
}
```

**Benefits:**

- ✅ Can handle 1M+ rows
- ✅ Keeps only 100 rows in memory (SXSSF)
- ✅ Doesn't load entire dataset into memory

#### 5-2. Stream File Save with OutputStream

```java
// Specify filename
try (FileOutputStream fos = new FileOutputStream("customers.xlsx");
     Stream<CustomerDTO> stream = customerRepository.streamAll()) {

    String fileName = ExcelExporter.excel(fos)
        .fileName("customers.xlsx")
        .write(stream);
    System.out.println("Large file created: " + fileName);
}

// Auto-generate filename
try (FileOutputStream fos = new FileOutputStream("customers.xlsx");
     Stream<CustomerDTO> stream = customerRepository.streamAll()) {

    String fileName = ExcelExporter.excel(fos)
        .write(stream);  // No fileName() call → auto-generated
    System.out.println("Large file created: " + fileName);
    // Output: Large file created: download_20250108_143025.xlsx
}
```

#### 5-3. Multi-sheet Streaming

```java
@PostMapping("/download/large-report")
public void downloadLargeReport(HttpServletResponse response) {
    Map<String, Stream<?>> sheetStreams = new LinkedHashMap<>();

    // Provide each sheet as Stream
    sheetStreams.put("customers", customerRepository.streamAll());
    sheetStreams.put("orders", orderRepository.streamAll());

    // Fluent API with Stream Map
    ExcelExporter.excel(response)
        .fileName("large_report.xlsx")
        .write(sheetStreams);  // Return value (final filename) can be ignored
}
```

#### 5-4. JPA Repository Stream Example

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
            ExcelExporter.excel(response)
                .fileName("customers.xlsx")
                .write(dtoStream);  // Return value (final filename) can be ignored
        }
    }
}
```

#### When to Use Stream?

| Data Size   | Recommended API              | Reason                |
|-------------|------------------------------|-----------------------|
| < 10K rows  | `excelFromList()`            | Simple, fast          |
| 10K~1M rows | `excelFromStream()`          | Memory efficient      |
| > 1M rows   | `excelFromStream()` required | List API has 1M limit |

---

### 6️⃣ CSV File Generation

Generate CSV files with annotations. Fully compliant with RFC 4180 standard.

#### 6-1. CSV Download with HttpServletResponse

```java
@PostMapping("/download/customers-csv")
public void downloadCustomersAsCsv(HttpServletResponse response) {
    List<CustomerDTO> customers = customerService.getAllCustomers();

    // CSV download using Fluent API (uses same DTO as Excel)
    ExcelExporter.csv(response)
        .fileName("customers.csv")
        .write(customers);  // Return value (final filename) can be ignored
    // Actual download: customers.csv (explicit filename - no timestamp)
}
```

#### 6-2. CSV File Save with OutputStream

```java
try (FileOutputStream fos = new FileOutputStream("customers.csv")) {
    List<CustomerDTO> customers = customerService.getCustomers();
    String fileName = ExcelExporter.csv(fos)
        .fileName("customers.csv")
        .write(customers);
    System.out.println("CSV created: " + fileName);
}
```

#### 6-3. Large CSV Streaming

```java
@PostMapping("/download/large-customers-csv")
public void downloadLargeCustomersAsCsv(HttpServletResponse response) {
    Stream<CustomerDTO> stream = customerRepository.streamAllCustomers();

    // Large CSV streaming using Fluent API
    ExcelExporter.csv(response)
        .fileName("large_customers.csv")
        .write(stream);  // Return value (final filename) can be ignored
}
```

**CSV Format Example:**

```csv
"Name","Age","Salary"
"Alice","30","123.45"
"Bob","40","67.89"
"Charlie","25","50000.00"
```

**RFC 4180 Compliance:**

- All fields enclosed in double quotes (`"`)
- Double quotes within fields escaped as `""`
- Record separator is CRLF (`\r\n`)
- Preserves newlines and commas within fields
- UTF-8 BOM included (Excel compatibility)

**Excel vs CSV Selection Criteria:**

| Criteria               | Excel  | CSV   |
|------------------------|--------|-------|
| Styling needed         | ✅      | ❌     |
| Conditional formatting | ✅      | ❌     |
| Multi-sheet            | ✅      | ❌     |
| Simple data exchange   | ⚪      | ✅     |
| File size              | Large  | Small |
| Compatibility          | Medium | High  |
| Processing speed       | Medium | Fast  |

### 7️⃣ Data Masking

Mask sensitive personal information (PII) automatically with built-in presets.

#### Available Masking Presets

| Preset           | Input Example       | Output Example      | Use Case                |
|------------------|---------------------|---------------------|-------------------------|
| `PHONE`          | 010-1234-5678       | 010-****-5678       | Phone numbers           |
| `EMAIL`          | user@example.com    | u***@example.com    | Email addresses         |
| `SSN`            | 123456-1234567      | 123456-*******      | Social Security Numbers |
| `NAME`           | 홍길동                 | 홍*동                 | Personal names          |
| `CREDIT_CARD`    | 1234-5678-9012-3456 | ****-****-****-3456 | Credit card numbers     |
| `ACCOUNT_NUMBER` | 110-123-456789      | 110-***-***789      | Bank account numbers    |
| `ADDRESS`        | 서울시 강남구 테헤란로 123    | 서울시 강남구 ***         | Street addresses        |
| `ZIP_CODE`       | 12345               | 123**               | Postal codes            |
| `IP_ADDRESS`     | 192.168.1.100       | 192.168.*.*         | IP addresses            |
| `PASSPORT`       | M12345678           | M12***678           | Passport numbers        |
| `LICENSE_PLATE`  | 12가3456             | 12가**56             | Vehicle license plates  |
| `PARTIAL_LEFT`   | ABC12345            | ****2345            | Mask left, show right 4 |
| `PARTIAL_RIGHT`  | ABC12345            | ABC1****            | Mask right, show left 4 |
| `MIDDLE`         | ABC12345            | AB****45            | Mask middle, show sides |

#### Basic Usage

```java
import io.github.takoeats.excelannotator.masking.Masking;

@ExcelSheet("Customer Information")
public class CustomerDTO {
    @ExcelColumn(header = "Name", order = 1, masking = Masking.NAME)
    private String name;

    @ExcelColumn(header = "Phone", order = 2, masking = Masking.PHONE)
    private String phoneNumber;

    @ExcelColumn(header = "Email", order = 3, masking = Masking.EMAIL)
    private String email;

    @ExcelColumn(header = "SSN", order = 4, masking = Masking.SSN)
    private String socialSecurityNumber;
}
```

#### Real-world Example: GDPR Compliance

```java

@ExcelSheet("User Data Export")
public class UserExportDTO {
    @ExcelColumn(header = "User ID", order = 1)
    private Long userId;  // No masking

    @ExcelColumn(header = "Name", order = 2, masking = Masking.NAME)
    private String fullName;  // 홍길동 → 홍*동

    @ExcelColumn(header = "Email", order = 3, masking = Masking.EMAIL)
    private String email;  // user@domain.com → u***@domain.com

    @ExcelColumn(header = "Phone", order = 4, masking = Masking.PHONE)
    private String phone;  // 010-1234-5678 → 010-****-5678

    @ExcelColumn(header = "Address", order = 5, masking = Masking.ADDRESS)
    private String address;  // 서울시 강남구 테헤란로 123 → 서울시 강남구 ***
}

// Controller
@PostMapping("/export/users")
public void exportUsers(HttpServletResponse response) {
    List<UserExportDTO> users = userService.getAllUsers();
    ExcelExporter.excel(response)
            .fileName("user_data.xlsx")
            .write(users);
    // Downloaded file contains masked sensitive data
}
```

#### Combined with Conditional Styling

```java

@ExcelSheet("Financial Report")
public class TransactionDTO {
    @ExcelColumn(header = "Account Number", order = 1, masking = Masking.ACCOUNT_NUMBER)
    private String accountNumber;

    @ExcelColumn(
            header = "Amount",
            order = 2,
            conditionalStyles = @ConditionalStyle(
                    when = "value < 0",
                    style = RedBackgroundStyle.class
            )
    )
    private BigDecimal amount;

    @ExcelColumn(header = "Card Number", order = 3, masking = Masking.CREDIT_CARD)
    private String cardNumber;
}
```

**Important Notes:**

- Masking only applies to **String fields**
- Non-string types (Integer, Date, etc.) are **ignored**
- For custom masking logic, apply masking **before** setting DTO values
- `null` and empty strings are handled gracefully (no errors)

---

## 🔧 Advanced Usage

### 8️⃣ Data Provider Pattern

Dedicated API that separates query logic and transformation logic for improved reusability.

#### API Signature

```java
// HttpServletResponse version
ExcelExporter.excelFromList(
        HttpServletResponse response,
        String fileName,
        Q queryParams,                        // Query parameter object
        ExcelDataProvider<Q, R> dataProvider, // Data fetch function
        Function<R, E> converter              // Entity → DTO conversion function
)
```

#### Usage Example

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

    // Data Provider: Complex query logic
    public List<CustomerEntity> searchCustomers(CustomerSearchRequest request) {
        return customerRepository.findByDateRangeAndType(
                request.getStartDate(),
                request.getEndDate(),
                request.getCustomerType()
        );
    }

    // Converter: Entity → DTO transformation
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
    // Separate three concerns: query, fetch, transform using Fluent API
    ExcelExporter.excel(response)
            .fileName("search_results.xlsx")
            .write(
                    request,                          // Q: Query params
                    customerService::searchCustomers,  // ExcelDataProvider<Q, R>
                    customerService::toDTO             // Function<R, E>
            );  // Return value (final filename) can be ignored
}
```

**Benefits:**

- ✅ Reusable query logic (can use `searchCustomers()` in other APIs)
- ✅ Reusable transform logic (can use `toDTO()` in other APIs)
- ✅ Testability (independently test each function)
- ✅ Code readability (separation of concerns)

### 9️⃣ Auto Column Generation

Automatically convert all fields to Excel columns without manually adding `@ExcelColumn` to each field.

#### Basic Usage

```java
import io.github.takoeats.excelannotator.annotation.ExcelSheet;

@ExcelSheet(value = "Customers", autoColumn = true)
public class CustomerDTO {
    private String name;        // Auto-included: header = "name", order = 1
    private Integer age;        // Auto-included: header = "age", order = 2
    private String email;       // Auto-included: header = "email", order = 3
    private Double salary;      // Auto-included: header = "salary", order = 4
}
```

**Result:**

- All fields are automatically exported to Excel
- Header names use field names
- Column order follows field declaration order

#### Excluding Specific Fields

```java
import io.github.takoeats.excelannotator.annotation.ExcelColumn;

@ExcelSheet(value = "Users", autoColumn = true)
public class UserDTO {
    private String username;    // Auto-included

    @ExcelColumn(exclude = true)
    private String password;    // Excluded from export

    private String email;       // Auto-included
    private Integer age;        // Auto-included
}
```

**Result:** Only username, email, and age are exported (password is excluded)

#### Mixing Auto Columns with Manual Annotations

```java

@ExcelSheet(value = "Products", autoColumn = true)
public class ProductDTO {
    @ExcelColumn(header = "Full Name", order = 1)
    private String name;        // Explicit annotation takes priority

    private Integer age;        // Auto: header = "age", order = 2

    @ExcelColumn(header = "Email Address", order = 3)
    private String email;       // Explicit annotation takes priority

    private String phone;       // Auto: header = "phone", order = 4

    @ExcelColumn(exclude = true)
    private String internalId;  // Excluded
}
```

**Result:**

- Fields with `@ExcelColumn` use the annotation settings
- Fields without annotation are auto-generated
- `exclude = true` fields are skipped

#### When to Use Auto Column

**✅ Good for:**

- Simple DTOs with many fields
- Quick prototyping
- Internal reports where field names are acceptable as headers

**❌ Not recommended for:**

- User-facing exports requiring professional headers
- Complex styling requirements per column
- When precise column ordering across multiple DTOs is needed

**💡 Tip:** You can start with `autoColumn = true` during development, then add explicit `@ExcelColumn` annotations as
your requirements become more specific.

---

### 🔟 Column Width Settings

#### Width Priority

The library determines column width in the following priority order:

1. **Explicit `@ExcelColumn(width=...)` specification** (highest priority)
2. **Style's `autoWidth()` configuration**
3. **Style's `width(...)` configuration**
4. **Default value (100 pixels)**

```java

@ExcelSheet("Customers")
public class CustomerDTO {

    @ExcelColumn(
            header = "Customer Name",
            order = 1,
            width = 150,  // Explicitly specify 150px (always applied)
            columnStyle = MyCustomStyle.class  // Style width is ignored
    )
    private String customerName;

    @ExcelColumn(
            header = "Email",
            order = 2,
            columnStyle = AutoWidthStyle.class  // Uses style's autoWidth()
    )
    private String email;

    @ExcelColumn(
            header = "Phone",
            order = 3
            // No width, no style → default 100px
    )
    private String phone;
}
```

#### Default Styles

The library automatically applies default styles when no custom style is specified:

| Field Type                                      | Default Style        | Behavior                        |
|-------------------------------------------------|----------------------|---------------------------------|
| Numeric types (Integer, Long, BigDecimal, etc.) | `DefaultNumberStyle` | Right-aligned, `#,##0` format   |
| Other types (String, Date, etc.)                | `DefaultColumnStyle` | Left-aligned, no special format |
| Headers (all columns)                           | `DefaultHeaderStyle` | Bold, center-aligned            |

**Example:**

```java
@ExcelColumn(header = "Amount", order = 1)
private BigDecimal amount;  // Automatically uses DefaultNumberStyle

@ExcelColumn(header = "Name", order = 2)
private String name;  // Automatically uses DefaultColumnStyle
```

**Overriding Defaults:**

```java
@ExcelColumn(
    header = "Amount",
    order = 1,
    columnStyle = CurrencyStyle.class  // Override DefaultNumberStyle
)
private BigDecimal amount;
```

### 1️⃣0️⃣ Merged Headers (2-Row Headers)

Create professional-looking Excel files with grouped column headers:

#### Basic Merged Header

```java
@ExcelSheet("Sales Report")
public class SalesDTO {
    @ExcelColumn(
        header = "Name",
        order = 1,
        mergeHeader = "Customer Info"  // Group header
    )
    private String customerName;

    @ExcelColumn(
        header = "Email",
        order = 2,
        mergeHeader = "Customer Info"  // Same group
    )
    private String email;

    @ExcelColumn(header = "Amount", order = 3)  // No merge → auto vertical merge
    private BigDecimal amount;
}
```

**Result:**

```
Row 0: [  Customer Info  ] [      ]
Row 1: [ Name  |  Email ] [Amount]
Data:  [Alice  | a@ex.com] [ $100 ]
```

#### Multiple Merge Groups

```java
@ExcelSheet("Employee Report")
public class EmployeeDTO {
    @ExcelColumn(header = "Name", order = 1, mergeHeader = "Personal")
    private String name;

    @ExcelColumn(header = "Age", order = 2, mergeHeader = "Personal")
    private Integer age;

    @ExcelColumn(header = "Street", order = 3, mergeHeader = "Address")
    private String street;

    @ExcelColumn(header = "City", order = 4, mergeHeader = "Address")
    private String city;

    @ExcelColumn(header = "Salary", order = 5)  // No merge group
    private BigDecimal salary;
}
```

**Result:**

```
Row 0: [  Personal  ] [  Address  ] [      ]
Row 1: [Name | Age] [St. | City] [Salary]
```

#### Styled Merged Headers

```java
public class BlueHeaderStyle extends CustomExcelCellStyle {
    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(ExcelColors.lightBlue())
                .fontColor(ExcelColors.darkBlue());
    }
}

@ExcelSheet("Report")
public class ReportDTO {
    @ExcelColumn(
            header = "Q1",
            order = 1,
            mergeHeader = "2024 Sales",
            mergeHeaderStyle = BlueHeaderStyle.class  // Custom style for merge header
    )
    private BigDecimal q1Sales;

    @ExcelColumn(
            header = "Q2",
            order = 2,
            mergeHeader = "2024 Sales"
    )
    private BigDecimal q2Sales;
}
```

**Important:**

- ✅ Columns in a merge group must have **consecutive order values**
- ❌ Gaps in order will throw `MERGE_HEADER_ORDER_GAP` exception
- ✅ Columns without `mergeHeader` are automatically merged vertically (1 column, 2 rows)

```java
// ❌ Invalid: Gap in order
@ExcelColumn(order = 1, mergeHeader = "Group")  // ✓
@ExcelColumn(order = 2)                         // ← Gap!
@ExcelColumn(order = 3, mergeHeader = "Group")  // ✗ Error!

// ✅ Valid: Consecutive orders
@ExcelColumn(order = 1, mergeHeader = "Group")  // ✓
@ExcelColumn(order = 2, mergeHeader = "Group")  // ✓
@ExcelColumn(order = 3)                         // ✓
```

---

### 1️⃣1️⃣ Header Control

#### Sheet without Header

```java
@ExcelSheet(value = "Data", hasHeader = false)  // Omit header row
public class DataDTO {
    @ExcelColumn(header = "ID", order = 1)  // header is required but not displayed
    private Long id;

    @ExcelColumn(header = "Name", order = 2)
    private String name;
}
```

#### Custom Header Style

```java
@ExcelColumn(
    header = "Total Amount",
    order = 1,
    headerStyle = MyCustomHeaderStyle.class,  // Header cell style
    columnStyle = CurrencyStyle.class         // Data cell style
)
private BigDecimal totalAmount;
```

### 1️⃣2️⃣ Sheet Order

```java
@ExcelSheet(value = "Summary", order = 1)  // First sheet
public class SummaryDTO { ... }

@ExcelSheet(value = "Details", order = 2)  // Second sheet
public class DetailDTO { ... }

@ExcelSheet(value = "Reference")  // No order → positioned first
public class ReferenceDTO { ... }
```

**Sorting Rules:**

1. Sheets without `order` come first (in input order)
2. Sheets with `order` sorted in ascending order

**Result Sheet Order:** Reference → Summary → Details

---

## ❓ FAQ

### Q1: When should I use List vs Stream?

**A:** Choose based on data size.

- **< 10K rows**: `excelFromList()` (simple, fast)
- **> 10K rows**: `excelFromStream()` (memory efficient)
- **> 1M rows**: `excelFromStream()` required (List has 1M limit)

### Q2: When is a timestamp added to filenames?

**A:** Timestamps are **only added to default filenames** to prevent collisions.

```java
// Explicit filename → no timestamp
ExcelExporter.excelFromList(response, "report.xlsx", data);
// Actual download: report.xlsx

// Default filename → timestamp added
ExcelExporter.excelFromList(outputStream, data);  // or "download"
// Result: download_20250119_143025.xlsx

// Already has timestamp pattern → no duplicate
ExcelExporter.excelFromList(response, "report_20251219_132153.xlsx", data);
// Actual download: report_20251219_132153.xlsx
```

### Q3: How does conditional style priority work?

**A:** **Higher** `priority` values take precedence.

```java
@ExcelColumn(
    conditionalStyles = {
        @ConditionalStyle(when = "value < 0", style = RedStyle.class, priority = 30),
        @ConditionalStyle(when = "value < -1000", style = DarkRedStyle.class, priority = 20)
    }
)
```

When value is -2000:

- Both conditions match
- priority 30 > 20 → `RedStyle` applied

### Q4: What happens to fields without annotations?

**A:** Fields without `@ExcelColumn` are not included in Excel.

```java
@ExcelSheet("Customers")
public class CustomerDTO {
    @ExcelColumn(header = "ID", order = 1)
    private Long id;

    private String internalCode;  // Not included in Excel
}
```

### Q5: Can I create Excel with empty data?

**A:** No. Empty lists/streams throw `ExcelExporterException` (E001).

**Solution:**

```java
List<CustomerDTO> customers = customerService.getCustomers();
if (customers.isEmpty()) {
    throw new CustomException("No customers found");
}
ExcelExporter.excel(response)
    .fileName("customers.xlsx")
    .write(customers);
```

### Q6: What are the multi-sheet merge rules?

**A:** DTOs with the same `@ExcelSheet.value()` merge into one sheet.

```java
// DTO A: @ExcelSheet("Customers") + order=1,2
// DTO B: @ExcelSheet("Customers") + order=3,4
// Result: Single sheet "Customers" with 4 columns (order: 1,2,3,4)
```

### Q7: How to avoid the 64K style limit?

**A:** The library automatically caches and deduplicates styles.

**Advice:**

- Minimize conditional styles (consolidate ranges)
- Merge similar styles

### Q8: Is it thread-safe?

**A:** Yes, it's thread-safe.

```java
@Async
public void exportCustomers(Long userId, HttpServletResponse response) {
    List<CustomerDTO> customers = customerService.getCustomersByUser(userId);
    ExcelExporter.excel(response)
        .fileName("customers.xlsx")
        .write(customers);
}
```

---

## 🛠️ Error Handling

### Main Error Codes

| Code | Message                            | Solution                               |
|------|------------------------------------|----------------------------------------|
| E001 | Empty data collection              | Check for empty data before processing |
| E005 | No @ExcelSheet annotation          | Add `@ExcelSheet` to DTO               |
| E006 | No @ExcelColumn fields             | Add at least 1 `@ExcelColumn` field    |
| E016 | Exceeded maximum rows for List API | Use Stream API                         |
| E017 | Stream already consumed            | Create new stream                      |

### Try-Catch Example

```java
@PostMapping("/download/customers")
public ResponseEntity<?> downloadCustomers(HttpServletResponse response) {
    try {
        List<CustomerDTO> customers = customerService.getCustomers();
        ExcelExporter.excel(response)
            .fileName("customers.xlsx")
            .write(customers);
        return ResponseEntity.ok().build();

    } catch (ExcelExporterException ex) {
        log.error("Excel export failed: {}", ex.getMessage(), ex);

        switch (ex.getCode()) {
            case "E001":
                return ResponseEntity.badRequest()
                    .body("No data available.");
            case "E016":
                return ResponseEntity.badRequest()
                    .body("Too much data. Please narrow the date range.");
            default:
                return ResponseEntity.internalServerError()
                    .body("Excel generation error: " + ex.getMessage());
        }
    }
}
```

---

## 📦 Installation

### Maven

```xml
<dependency>
    <groupId>io.github.takoeats</groupId>
    <artifactId>excel-annotator</artifactId>
    <version>2.3.1</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.github.takoeats:excel-annotator:2.3.1'
```

### Dependencies

| Library       | Version            | Description             |
|---------------|--------------------|-------------------------|
| Apache POI    | 5.4.0              | Excel file manipulation |
| Commons Lang3 | 3.18.0             | String utilities        |
| SLF4J API     | 2.0.17             | Logging API             |
| Servlet API   | 3.1.0 (provided)   | HttpServletResponse     |
| Lombok        | 1.18.30 (provided) | Boilerplate reduction   |

---

## 🎯 Real-world Examples

### 1. Spring Boot Controller

```java
@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final CustomerService customerService;

    @GetMapping("/customers")
    public void downloadCustomers(HttpServletResponse response) {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        ExcelExporter.excel(response)
            .fileName("customers.xlsx")
            .write(customers);
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

        String fileName = String.format("monthly_report_%d_%d.xlsx", year, month);
        ExcelExporter.excel(response)
            .fileName(fileName)
            .write(report);
    }
}
```

### 2. Financial Report with Conditional Styling

```java
@Data
@ExcelSheet("Financial Summary")
public class FinancialSummaryDTO {

    @ExcelColumn(header = "Category", order = 1)
    private String category;

    @ExcelColumn(
        header = "Amount",
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
        header = "Change Rate",
        order = 3,
        columnStyle = PercentageStyle.class,
        conditionalStyles = {
            @ConditionalStyle(
                when = "value < -0.1",  // Below -10%
                style = CriticalAlertStyle.class,
                priority = 20
            ),
            @ConditionalStyle(
                when = "value > 0.2",   // Above +20%
                style = SignatureStyle.class,
                priority = 10
            )
        }
    )
    private Double changeRate;
    
    @ExcelColumn(
            header = "Completion Status",
            order = 4,
            columnStyle = BooleanStyle.class,
            conditionalStyles = {
                    @ConditionalStyle(
                            when = "Conditions.IS_NEGATIVE",  // Below -10%
                            style = CriticalAlertStyle.class,
                            priority = 20
                    ),
                    @ConditionalStyle(
                            when = Conditions.IS_POSITIVE,   // Above +20%
                            style = SignatureStyle.class,
                            priority = 10
                    )
            }
    )
    private boolean isCompleted;
}
```

### 3. Large Dataset Batch Processing

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
            String fileName = ExcelExporter.excel(fos)
                .fileName("customers.xlsx")
                .write(dtoStream);

            log.info("Batch export completed: {}", fileName);
            return fileName;
        }
    }
}
```

---

## 📡 HttpServletResponse Header Behavior

### Library's Responsibility Scope

The `ExcelExporter.excelFromList(response, fileName, data)` method sets **only the minimum required headers**,
respecting user control.

### ✅ Automatically Set Headers

Headers that the library **always sets** (overwrite):

```java
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="download.xlsx"; filename*=UTF-8''...
```

### 🔄 Conditionally Set Headers

Applied **only if user hasn't set them**:

```java
Cache-Control: no-store, no-cache, must-revalidate, max-age=0
```

**Example: Custom Cache-Control**

```java
@GetMapping("/download/public-report")
public void downloadPublicReport(HttpServletResponse response) {
    // Allow caching if desired
    response.setHeader("Cache-Control", "public, max-age=3600");

    List<ReportDTO> data = reportService.getPublicData();
    ExcelExporter.excel(response)
        .fileName("report.xlsx")
        .write(data);
    // Cache-Control remains "public, max-age=3600"
}
```

### 🛡️ Custom Header Preservation

The library **does not call `response.reset()`**, so **all user-set headers are preserved**.

**Example: Maintaining Security Token Headers**

```java
@GetMapping("/download/secure-data")
public void downloadSecureData(HttpServletResponse response) {
    // Authentication/security custom headers
    response.setHeader("X-Custom-Auth-Token", securityService.generateToken());
    response.setHeader("X-Request-ID", requestId);
    response.setHeader("X-User-Role", currentUser.getRole());

    List<SecureDataDTO> data = secureDataService.getData();
    ExcelExporter.excel(response)
        .fileName("secure-data.xlsx")
        .write(data);
    // ✅ All custom headers are preserved
}
```

### 📌 Design Principles

1. **Minimal Intervention**: Set only headers essential for Excel generation
2. **User First**: Never remove user-set values
3. **Container Delegation**: Does not call `response.flushBuffer()` (Servlet container handles automatically)

---

## 🔒 Security Features

### Automatic Filename Sanitization

User-provided filenames go through **whitelist-based validation → sanitization → semantic validation**.
Risky or meaningless filenames are **automatically replaced with safe default filenames**.

---

### ❌ Dangerous Input Example

(Java code example)

ExcelExporter.excelFromList(response, "../../../etc/passwd.xlsx", data);

Processing result

download_20251216_143025.xlsx

Path traversal patterns detected
→ Immediately blocked without partial sanitization, replaced with default filename.

---

### ❌ Meaningless Filename Example

(Java code example)

ExcelExporter.excelFromList(response, "!!!@@@###", data);

Processing result

download_20251216_143025.xlsx

- All characters removed/replaced, meaning lost
- Only underscores (_) remaining
  → Default filename applied

---

### ✅ Multilingual Filename Support

Filenames in the following languages are allowed:

- Korean (가–힣)
- Japanese (Hiragana, Katakana)
- Chinese (CJK Unified Ideographs)
- Western European (accented characters)

(Java code example)

ExcelExporter.excelFromList(response, "Sales_Report.xlsx", data);

Processing result

Sales_Report.xlsx

---

### 🚫 Blocked Patterns

Any of the following patterns detected → **immediately replaced with default filename**:

- Path traversal
  .., /, \, :
- Hidden files
  Files starting with .
- Control characters
  \x00–\x1F, \x7F
- URL encoding attacks
  %2e, %2f, %5c, %00
- OS reserved filenames
    - Windows: CON, PRN, AUX, NUL, COM1–9, LPT1–9
    - Unix/Linux: null, stdin, stdout, stderr, random, etc.
- Filename length limit
  Auto-truncated if exceeds 200 characters

---

### 📌 Processing Principles Summary

- Whitelist-based allowance
- Dangerous patterns immediately blocked without sanitization
- Meaningless results use default filename
- Extension and timestamp added by system after validation

---

## 📄 License

This project is licensed under the **Apache-2.0** license.

---

## 🤝 Contributing

Please report bugs and feature requests on [GitHub Issues](https://github.com/takoeats/excel-annotator/issues).

---

<div align="center">

**⭐ Star this project if you find it useful! ⭐**

Made with ❤️ by [takoeats](https://github.com/takoeats)

</div>
