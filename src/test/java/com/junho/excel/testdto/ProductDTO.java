package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;

@ExcelSheet(value = "Products", hasHeader = true)
public class ProductDTO {
    @ExcelColumn(header = "Product", order = 1)
    private String product;

    public ProductDTO(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }
}