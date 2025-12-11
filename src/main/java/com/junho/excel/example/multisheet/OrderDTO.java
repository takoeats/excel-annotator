package com.junho.excel.example.multisheet;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.CurrencyStyle;
import com.junho.excel.example.style.DateTimeStyle;
import com.junho.excel.example.style.TableHeaderStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("주문 내역")
public class OrderDTO {

    @ExcelColumn(
            header = "주문번호",
            order = 1,
            width = 100,
            headerStyle = TableHeaderStyle.class
    )
    private String orderNumber;

    @ExcelColumn(
            header = "고객ID",
            order = 2,
            width = 80,
            headerStyle = TableHeaderStyle.class
    )
    private Long customerId;

    @ExcelColumn(
            header = "상품명",
            order = 3,
            width = 200,
            headerStyle = TableHeaderStyle.class
    )
    private String productName;

    @ExcelColumn(
            header = "수량",
            order = 4,
            width = 80,
            headerStyle = TableHeaderStyle.class
    )
    private Integer quantity;

    @ExcelColumn(
            header = "주문금액",
            order = 5,
            width = 120,
            headerStyle = TableHeaderStyle.class,
            columnStyle = CurrencyStyle.class
    )
    private BigDecimal orderAmount;

    @ExcelColumn(
            header = "주문일시",
            order = 6,
            width = 150,
            headerStyle = TableHeaderStyle.class,
            columnStyle = DateTimeStyle.class
    )
    private LocalDateTime orderDateTime;

    @ExcelColumn(
            header = "배송상태",
            order = 7,
            width = 100,
            headerStyle = TableHeaderStyle.class
    )
    private String deliveryStatus;
}
