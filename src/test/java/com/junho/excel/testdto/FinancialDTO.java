package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.CurrencyStyle;
import com.junho.excel.example.style.DateOnlyStyle;
import com.junho.excel.example.style.PercentageStyle;
import com.junho.excel.example.style.PurpleHeaderStyle;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("재무 리포트")
public class FinancialDTO {

    @ExcelColumn(header = "계정과목", order = 1, width = 150, headerStyle = PurpleHeaderStyle.class)
    private String accountName;

    @ExcelColumn(header = "매출액", order = 2, width = 150,
        headerStyle = PurpleHeaderStyle.class,
        columnStyle = CurrencyStyle.class)
    private BigDecimal revenue;

    @ExcelColumn(header = "비용", order = 3, width = 150,
        headerStyle = PurpleHeaderStyle.class,
        columnStyle = CurrencyStyle.class)
    private BigDecimal expense;

    @ExcelColumn(header = "영업이익", order = 4, width = 150,
        headerStyle = PurpleHeaderStyle.class,
        columnStyle = CurrencyStyle.class)
    private BigDecimal operatingProfit;

    @ExcelColumn(header = "이익률", order = 5, width = 120,
        headerStyle = PurpleHeaderStyle.class,
        columnStyle = PercentageStyle.class)
    private BigDecimal profitMargin;

    @ExcelColumn(header = "결산일", order = 6, width = 120,
        headerStyle = PurpleHeaderStyle.class,
        columnStyle = DateOnlyStyle.class)
    private LocalDate settlementDate;
}
