package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.testdto.*;
import io.github.takoeats.excelannotator.util.ExcelTestHelper;
import io.github.takoeats.excelannotator.util.TestDataFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.takoeats.excelannotator.util.ExcelAssertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BusinessScenarioIntegrationTest {

    @Test
    void employeeSalaryReport_generatesCorrectExcel() throws Exception {
        List<EmployeeDTO> employees = TestDataFactory.createEmployees(50);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "employee_salary.xlsx", employees);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        assertSheetCount(wb, 1);

        Sheet sheet = wb.getSheetAt(0);
        assertEquals("직원 목록", sheet.getSheetName());
        assertRowCount(sheet, 51);

        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(0), "사번");
        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(1), "이름");
        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(4), "급여");

        wb.close();
    }

    @Test
    void departmentStatistics_multiSheet_generatesCorrectly() throws Exception {
        List<DepartmentDTO> departments = TestDataFactory.createDepartments(10);
        List<EmployeeDTO> employees = TestDataFactory.createEmployees(30);

        Map<String, List<?>> sheetDataMap = new LinkedHashMap<String, List<?>>();
        sheetDataMap.put("departments", departments);
        sheetDataMap.put("employees", employees);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "department_stats.xlsx", sheetDataMap);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        assertSheetCount(wb, 2);

        assertEquals("부서 목록", wb.getSheetAt(0).getSheetName());
        assertEquals("직원 목록", wb.getSheetAt(1).getSheetName());

        assertRowCount(wb.getSheetAt(0), 11);
        assertRowCount(wb.getSheetAt(1), 31);

        wb.close();
    }

    @Test
    void salesReport_withConditionalFormatting_generatesCorrectly() throws Exception {
        List<SalesReportDTO> salesReports = TestDataFactory.createSalesReports(100);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "sales_report.xlsx", salesReports);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        Sheet sheet = wb.getSheetAt(0);

        assertEquals("매출 현황", sheet.getSheetName());
        assertRowCount(sheet, 101);

        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(0), "제품명");
        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(2), "매출액");

        wb.close();
    }

    @Test
    void customerList_largeDataset_usingStream() throws Exception {
        int customerCount = 100000;
        Stream<CustomerDTO> customerStream = Stream.iterate(1, n -> n + 1)
                .limit(customerCount)
                .map(TestDataFactory::createCustomer);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromStream(baos, "customers.xlsx", customerStream);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        Sheet sheet = wb.getSheetAt(0);

        assertEquals("고객 목록", sheet.getSheetName());
        assertNotNull(sheet.getRow(0));

        wb.close();
    }

    @Test
    void orderHistory_generatesCorrectExcel() throws Exception {
        List<OrderDTO> orders = TestDataFactory.createOrders(200);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "order_history.xlsx", orders);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        Sheet sheet = wb.getSheetAt(0);

        assertEquals("주문 내역", sheet.getSheetName());
        assertRowCount(sheet, 201);

        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(0), "주문번호");
        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(5), "주문일시");

        wb.close();
    }

    @Test
    void financialReport_withCurrencyFormatting_generatesCorrectly() throws Exception {
        List<FinancialDTO> financials = TestDataFactory.createFinancials(20);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "financial_report.xlsx", financials);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        Sheet sheet = wb.getSheetAt(0);

        assertEquals("재무 리포트", sheet.getSheetName());
        assertRowCount(sheet, 21);

        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(1), "매출액");
        ExcelTestHelper.assertCellValue(sheet.getRow(0).getCell(4), "이익률");

        wb.close();
    }

    @Test
    void hrIntegratedReport_multiSheet_generatesCorrectly() throws Exception {
        List<EmployeeDTO> employees = TestDataFactory.createEmployees(50);
        List<DepartmentDTO> departments = TestDataFactory.createDepartments(5);
        List<FinancialDTO> salaryBudget = TestDataFactory.createFinancials(5);

        Map<String, List<?>> sheetDataMap = new LinkedHashMap<String, List<?>>();
        sheetDataMap.put("employees", employees);
        sheetDataMap.put("departments", departments);
        sheetDataMap.put("budget", salaryBudget);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "hr_integrated_report.xlsx", sheetDataMap);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        assertSheetCount(wb, 3);

        assertEquals("직원 목록", wb.getSheetAt(0).getSheetName());
        assertEquals("부서 목록", wb.getSheetAt(1).getSheetName());
        assertEquals("재무 리포트", wb.getSheetAt(2).getSheetName());

        assertRowCount(wb.getSheetAt(0), 51);
        assertRowCount(wb.getSheetAt(1), 6);
        assertRowCount(wb.getSheetAt(2), 6);

        wb.close();
    }

    @Test
    void complexBusinessReport_allFeatures_generatesCorrectly() throws Exception {
        List<EmployeeDTO> employees = TestDataFactory.createEmployees(100);
        List<SalesReportDTO> sales = TestDataFactory.createSalesReports(150);
        List<OrderDTO> orders = TestDataFactory.createOrders(200);

        Map<String, List<?>> sheetDataMap = new LinkedHashMap<String, List<?>>();
        sheetDataMap.put("employees", employees);
        sheetDataMap.put("sales", sales);
        sheetDataMap.put("orders", orders);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "complex_business_report.xlsx", sheetDataMap);

        byte[] excelBytes = baos.toByteArray();
        assertExcelFileValid(excelBytes);

        Workbook wb = ExcelTestHelper.workbookFromBytes(excelBytes);
        assertSheetCount(wb, 3);

        Sheet employeeSheet = wb.getSheetAt(0);
        Sheet salesSheet = wb.getSheetAt(1);
        Sheet orderSheet = wb.getSheetAt(2);

        assertEquals("직원 목록", employeeSheet.getSheetName());
        assertEquals("매출 현황", salesSheet.getSheetName());
        assertEquals("주문 내역", orderSheet.getSheetName());

        assertRowCount(employeeSheet, 101);
        assertRowCount(salesSheet, 151);
        assertRowCount(orderSheet, 201);

        wb.close();
    }
}
