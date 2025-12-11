package com.junho.excel.example.multisheet;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.CurrencyStyle;
import com.junho.excel.example.style.HighlightStyle;
import com.junho.excel.example.style.PercentageStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("상품 목록")
public class ProductDTO {

    @ExcelColumn(
            header = "상품코드",
            order = 1,
            width = 100,
            headerStyle = HighlightStyle.class
    )
    private String productCode;

    @ExcelColumn(
            header = "상품명",
            order = 2,
            width = 200,
            headerStyle = HighlightStyle.class
    )
    private String productName;

    @ExcelColumn(
            header = "카테고리",
            order = 3,
            width = 120,
            headerStyle = HighlightStyle.class
    )
    private String category;

    @ExcelColumn(
            header = "판매가",
            order = 4,
            width = 120,
            headerStyle = HighlightStyle.class,
            columnStyle = CurrencyStyle.class
    )
    private BigDecimal price;

    @ExcelColumn(
            header = "재고수량",
            order = 5,
            width = 100,
            headerStyle = HighlightStyle.class
    )
    private Integer stock;

    @ExcelColumn(
            header = "할인율",
            order = 6,
            width = 100,
            headerStyle = HighlightStyle.class,
            columnStyle = PercentageStyle.class
    )
    private BigDecimal discountRate;

    @ExcelColumn(
            header = "제조사",
            order = 7,
            width = 150,
            headerStyle = HighlightStyle.class
    )
    private String manufacturer;
}
