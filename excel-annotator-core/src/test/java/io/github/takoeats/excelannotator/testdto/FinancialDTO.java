package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.teststyle.CurrencyStyle;
import io.github.takoeats.excelannotator.teststyle.DateOnlyStyle;
import io.github.takoeats.excelannotator.teststyle.PercentageStyle;
import io.github.takoeats.excelannotator.teststyle.PurpleHeaderStyle;
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
