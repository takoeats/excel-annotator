package io.github.takoeats.excelannotator.util;

import io.github.takoeats.excelannotator.testdto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public final class TestDataFactory {

    private static final Random RANDOM = new Random();
    private static final String[] DEPARTMENTS = {"Sales", "Engineering", "HR", "Finance", "Marketing"};
    private static final String[] POSITIONS = {"Junior", "Senior", "Manager", "Director", "VP"};
    private static final String[] PRODUCT_CATEGORIES = {"Electronics", "Food", "Clothing", "Books", "Toys"};

    private TestDataFactory() {
    }

    public static BigDecimal randomBigDecimal(double min, double max) {
        double value = min + (max - min) * RANDOM.nextDouble();
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    public static int randomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static LocalDate randomLocalDate() {
        int daysBack = RANDOM.nextInt(730);
        return LocalDate.now().minusDays(daysBack);
    }

    public static LocalDateTime randomLocalDateTime() {
        int daysBack = RANDOM.nextInt(730);
        int hours = RANDOM.nextInt(24);
        int minutes = RANDOM.nextInt(60);
        return LocalDateTime.now().minusDays(daysBack).withHour(hours).withMinute(minutes);
    }

    public static String randomDepartment() {
        return DEPARTMENTS[RANDOM.nextInt(DEPARTMENTS.length)];
    }

    public static String randomPosition() {
        return POSITIONS[RANDOM.nextInt(POSITIONS.length)];
    }

    public static String randomProductCategory() {
        return PRODUCT_CATEGORIES[RANDOM.nextInt(PRODUCT_CATEGORIES.length)];
    }

    public static String generateEmployeeId(int index) {
        return String.format("EMP-%05d", index);
    }

    public static String generateProductCode(int index) {
        return String.format("PRD-%05d", index);
    }

    public static String generateCustomerId(int index) {
        return String.format("CUST-%06d", index);
    }

    public static String generateOrderId(int index) {
        return String.format("ORD-%08d", index);
    }

    public static <T> Stream<T> streamFromList(List<T> list) {
        return list.stream();
    }

    public static <T> Stream<T> infiniteStream(java.util.function.Supplier<T> supplier, int limit) {
        return Stream.generate(supplier).limit(limit);
    }

    public static EmployeeDTO createEmployee(int index) {
        return EmployeeDTO
                .builder()
                .employeeId(generateEmployeeId(index))
                .name("Employee-" + index)
                .department(randomDepartment())
                .position(randomPosition())
                .salary(randomBigDecimal(30000000, 100000000))
                .hireDate(randomLocalDate())
                .build();
    }

    public static List<EmployeeDTO> createEmployees(int count) {
        List<EmployeeDTO> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(createEmployee(i));
        }
        return list;
    }

    public static DepartmentDTO createDepartment(int index) {
        return DepartmentDTO
                .builder()
                .departmentCode("DEPT-" + String.format("%03d", index))
                .departmentName(randomDepartment())
                .location("Floor-" + randomInt(1, 10))
                .employeeCount(randomInt(5, 50))
                .manager("Manager-" + index)
                .build();
    }

    public static List<DepartmentDTO> createDepartments(int count) {
        List<DepartmentDTO> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(createDepartment(i));
        }
        return list;
    }

    public static SalesReportDTO createSalesReport(int index) {
        int quantity = randomInt(-10, 200);
        BigDecimal revenue = randomBigDecimal(-1000000, 15000000);
        BigDecimal achievement = randomBigDecimal(0, 2);

        return SalesReportDTO
                .builder()
                .productName("Product-" + index)
                .quantity(quantity)
                .revenue(revenue)
                .achievementRate(achievement)
                .salesPerson("Salesperson-" + randomInt(1, 10))
                .build();
    }

    public static List<SalesReportDTO> createSalesReports(int count) {
        List<SalesReportDTO> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(createSalesReport(i));
        }
        return list;
    }

    public static Stream<SalesReportDTO> createSalesReportStream(int count) {
        return Stream.iterate(1, n -> n + 1)
                .limit(count)
                .map(TestDataFactory::createSalesReport);
    }

    public static CustomerDTO createCustomer(int index) {
        return CustomerDTO
                .builder()
                .customerId(generateCustomerId(index))
                .customerName("Customer-" + index)
                .email("customer" + index + "@example.com")
                .phone("010-" + String.format("%04d", randomInt(1000, 9999)) + "-" + String.format("%04d", randomInt(1000, 9999)))
                .joinDate(randomLocalDate())
                .vip(randomInt(1, 10) > 8)
                .build();
    }

    public static List<CustomerDTO> createCustomers(int count) {
        List<CustomerDTO> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(createCustomer(i));
        }
        return list;
    }

    public static Stream<CustomerDTO> createCustomerStream(int count) {
        return Stream.generate(() -> createCustomer(randomInt(1, 1000000)))
                .limit(count);
    }

    public static OrderDTO createOrder(int index) {
        return OrderDTO
                .builder()
                .orderId(generateOrderId(index))
                .customerName("Customer-" + randomInt(1, 100))
                .productName("Product-" + randomInt(1, 50))
                .quantity(randomInt(1, 100))
                .orderAmount(randomBigDecimal(10000, 5000000))
                .orderDateTime(randomLocalDateTime())
                .deliveryStatus(new String[]{"주문접수", "배송준비", "배송중", "배송완료"}[randomInt(0, 3)])
                .build();
    }

    public static List<OrderDTO> createOrders(int count) {
        List<OrderDTO> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(createOrder(i));
        }
        return list;
    }

    public static FinancialDTO createFinancial(int index) {
        BigDecimal revenue = randomBigDecimal(100000000, 1000000000);
        BigDecimal expense = randomBigDecimal(50000000, 800000000);
        BigDecimal profit = revenue.subtract(expense);
        BigDecimal margin = profit.divide(revenue, 4, RoundingMode.HALF_UP);

        return FinancialDTO
                .builder()
                .accountName("Account-" + index)
                .revenue(revenue)
                .expense(expense)
                .operatingProfit(profit)
                .profitMargin(margin)
                .settlementDate(randomLocalDate())
                .build();
    }

    public static List<FinancialDTO> createFinancials(int count) {
        List<FinancialDTO> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(createFinancial(i));
        }
        return list;
    }

    public static AllTypesDTO createAllTypes(int index) {
        return AllTypesDTO
                .builder()
                .stringValue("String-" + index)
                .integerValue(index)
                .longValue((long) index * 1000)
                .doubleValue(index * 1.5)
                .floatValue(index * 2.5f)
                .booleanValue(index % 2 == 0)
                .bigDecimalValue(randomBigDecimal(1000, 10000))
                .bigIntegerValue(java.math.BigInteger.valueOf(index).multiply(java.math.BigInteger.valueOf(1000000)))
                .localDateValue(randomLocalDate())
                .localDateTimeValue(randomLocalDateTime())
                .zonedDateTimeValue(java.time.ZonedDateTime.now().minusDays(index))
                .enumValue(AllTypesDTO.StatusEnum.values()[index % 5])
                .build();
    }

    public static List<AllTypesDTO> createAllTypesList(int count) {
        List<AllTypesDTO> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(createAllTypes(i));
        }
        return list;
    }
}
