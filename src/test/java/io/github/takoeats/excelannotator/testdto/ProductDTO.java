package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;

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