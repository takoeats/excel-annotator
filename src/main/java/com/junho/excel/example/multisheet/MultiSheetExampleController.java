package com.junho.excel.example.multisheet;

import com.junho.excel.ExcelExporter;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 멀티 시트 Excel 다운로드 예제 컨트롤러
 * <p>List 기반 및 Stream 기반 멀티 시트 생성 예제를 제공합니다.</p>
 */
public class MultiSheetExampleController {

    /**
     * 예제 1: List 기반 멀티 시트 Excel 다운로드
     * <p>여러 DTO를 각각의 시트로 생성합니다.</p>
     * <p>시트 이름은 각 DTO의 @ExcelSheet.value()에서 추출되며, Map의 key는 단순 식별자로만 사용됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @PostMapping("/multiSheetExcel")
     * public void downloadExcel(HttpServletResponse response) {
     *     Map<String, List<?>> sheetData = new LinkedHashMap<>();
     *     sheetData.put("identifier1", customerService.getCustomers());  // @ExcelSheet("고객 목록")
     *     sheetData.put("identifier2", orderService.getOrders());        // @ExcelSheet("주문 내역")
     *     sheetData.put("identifier3", productService.getProducts());    // @ExcelSheet("상품 목록")
     *
     *     ExcelExporter.downloadExcel(response, "통합_리포트.xlsx", sheetData);
     * }
     * }</pre>
     */
    public void exampleListBasedMultiSheet(HttpServletResponse response) {
        List<CustomerDTO> customers = createSampleCustomers();
        List<OrderDTO> orders = createSampleOrders();
        List<ProductDTO> products = createSampleProducts();

        Map<String, List<?>> sheetData = new LinkedHashMap<>();
        sheetData.put("고객 목록", customers);
        sheetData.put("주문 내역", orders);
        sheetData.put("상품 목록", products);

        ExcelExporter.excelFromList(response, "통합_리포트.xlsx", sheetData);
    }


    /**
     * 예제 3: 시트 순서 제어
     * <p>LinkedHashMap을 사용하여 시트 생성 순서를 보장합니다.</p>
     * <p>동일한 시트 이름을 가진 DTO들은 @ExcelColumn.order 순서대로 정렬되어 하나의 시트로 병합됩니다.</p>
     */
    public void exampleSheetOrdering(HttpServletResponse response) {
        Map<String, List<?>> sheetData = new LinkedHashMap<>();

        sheetData.put("요약", createSampleCustomers());
        sheetData.put("상세_주문", createSampleOrders());
        sheetData.put("상세_상품", createSampleProducts());

        ExcelExporter.excelFromList(response, "순서_보장_리포트.xlsx", sheetData);
    }

    private List<CustomerDTO> createSampleCustomers() {
        List<CustomerDTO> customers = new ArrayList<>();
        customers.add(CustomerDTO.builder()
                .customerId(1L)
                .customerName("김철수")
                .email("kim@example.com")
                .phoneNumber("010-1234-5678")
                .totalPurchase(new BigDecimal("1500000"))
                .joinDate(LocalDate.of(2023, 1, 15))
                .build());
        customers.add(CustomerDTO.builder()
                .customerId(2L)
                .customerName("이영희")
                .email("lee@example.com")
                .phoneNumber("010-2345-6789")
                .totalPurchase(new BigDecimal("2300000"))
                .joinDate(LocalDate.of(2023, 3, 20))
                .build());
        customers.add(CustomerDTO.builder()
                .customerId(3L)
                .customerName("박민수")
                .email("park@example.com")
                .phoneNumber("010-3456-7890")
                .totalPurchase(new BigDecimal("890000"))
                .joinDate(LocalDate.of(2023, 5, 10))
                .build());
        return customers;
    }

    private List<OrderDTO> createSampleOrders() {
        List<OrderDTO> orders = new ArrayList<>();
        orders.add(OrderDTO.builder()
                .orderNumber("ORD-2025-0001")
                .customerId(1L)
                .productName("노트북")
                .quantity(1)
                .orderAmount(new BigDecimal("1200000"))
                .orderDateTime(LocalDateTime.of(2025, 1, 10, 14, 30))
                .deliveryStatus("배송완료")
                .build());
        orders.add(OrderDTO.builder()
                .orderNumber("ORD-2025-0002")
                .customerId(2L)
                .productName("스마트폰")
                .quantity(2)
                .orderAmount(new BigDecimal("1800000"))
                .orderDateTime(LocalDateTime.of(2025, 1, 12, 9, 15))
                .deliveryStatus("배송중")
                .build());
        orders.add(OrderDTO.builder()
                .orderNumber("ORD-2025-0003")
                .customerId(3L)
                .productName("태블릿")
                .quantity(1)
                .orderAmount(new BigDecimal("650000"))
                .orderDateTime(LocalDateTime.of(2025, 1, 15, 16, 45))
                .deliveryStatus("주문확인")
                .build());
        return orders;
    }

    private List<ProductDTO> createSampleProducts() {
        List<ProductDTO> products = new ArrayList<>();
        products.add(ProductDTO.builder()
                .productCode("PROD-001")
                .productName("노트북")
                .category("전자제품")
                .price(new BigDecimal("1200000"))
                .stock(50)
                .discountRate(new BigDecimal("0.10"))
                .manufacturer("삼성전자")
                .build());
        products.add(ProductDTO.builder()
                .productCode("PROD-002")
                .productName("스마트폰")
                .category("전자제품")
                .price(new BigDecimal("900000"))
                .stock(120)
                .discountRate(new BigDecimal("0.05"))
                .manufacturer("LG전자")
                .build());
        products.add(ProductDTO.builder()
                .productCode("PROD-003")
                .productName("태블릿")
                .category("전자제품")
                .price(new BigDecimal("650000"))
                .stock(80)
                .discountRate(new BigDecimal("0.15"))
                .manufacturer("애플")
                .build());
        return products;
    }
}
