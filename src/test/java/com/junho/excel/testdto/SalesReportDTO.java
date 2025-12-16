package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.annotation.ConditionalStyle;
import com.junho.excel.example.style.AttentionStyle;
import com.junho.excel.example.style.CriticalAlertStyle;
import com.junho.excel.example.style.CurrencyStyle;
import com.junho.excel.example.style.HighlightStyle;
import com.junho.excel.example.style.PurpleHeaderStyle;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("매출 현황")
public class SalesReportDTO {

    @ExcelColumn(header = "제품명", order = 1, width = 150, headerStyle = PurpleHeaderStyle.class)
    private String productName;

    @ExcelColumn(header = "판매수량", order = 2, width = 100,
        headerStyle = PurpleHeaderStyle.class,
        conditionalStyles = {
            @ConditionalStyle(when = "value <= 0", style = CriticalAlertStyle.class, priority = 20),
            @ConditionalStyle(when = "value >= 100", style = HighlightStyle.class, priority = 10)
        })
    private Integer quantity;

    @ExcelColumn(header = "매출액", order = 3, width = 150,
        headerStyle = PurpleHeaderStyle.class,
        columnStyle = CurrencyStyle.class,
        conditionalStyles = {
            @ConditionalStyle(when = "value < 0", style = CriticalAlertStyle.class, priority = 30),
            @ConditionalStyle(when = "value >= 10000000", style = HighlightStyle.class, priority = 20),
            @ConditionalStyle(when = "value < 1000000", style = AttentionStyle.class, priority = 10)
        })
    private BigDecimal revenue;

    @ExcelColumn(header = "목표 달성률", order = 4, width = 120,
        headerStyle = PurpleHeaderStyle.class,
        format = "0.00%")
    private BigDecimal achievementRate;

    @ExcelColumn(header = "담당자", order = 5, width = 100, headerStyle = PurpleHeaderStyle.class)
    private String salesPerson;
}
