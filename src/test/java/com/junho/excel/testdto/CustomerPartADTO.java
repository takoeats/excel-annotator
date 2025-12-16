package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;

@ExcelSheet(value = "고객", hasHeader = true)
public class CustomerPartADTO {
    @ExcelColumn(header = "고객ID", order = 1)
    private String customerId;

    @ExcelColumn(header = "고객명", order = 2)
    private String customerName;

    public CustomerPartADTO(String customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
}
