package com.junho.excel.example.multisheet;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.CurrencyStyle;
import com.junho.excel.example.style.DateOnlyStyle;
import com.junho.excel.example.style.PurpleHeaderStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("고객 목록")
public class CustomerDTO {

    @ExcelColumn(
            header = "고객ID",
            order = 1,
            width = 80,
            headerStyle = PurpleHeaderStyle.class
    )
    private Long customerId;

    @ExcelColumn(
            header = "고객명",
            order = 2,
            width = 120,
            headerStyle = PurpleHeaderStyle.class
    )
    private String customerName;

    @ExcelColumn(
            header = "이메일",
            order = 3,
            width = 200,
            headerStyle = PurpleHeaderStyle.class
    )
    private String email;

    @ExcelColumn(
            header = "전화번호",
            order = 4,
            width = 120,
            headerStyle = PurpleHeaderStyle.class
    )
    private String phoneNumber;

    @ExcelColumn(
            header = "총 구매액",
            order = 5,
            width = 120,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = CurrencyStyle.class
    )
    private BigDecimal totalPurchase;

    @ExcelColumn(
            header = "가입일",
            order = 6,
            width = 120,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DateOnlyStyle.class
    )
    private LocalDate joinDate;
}
