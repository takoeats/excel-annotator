package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.teststyle.CurrencyStyle;
import io.github.takoeats.excelannotator.teststyle.DateOnlyStyle;
import io.github.takoeats.excelannotator.teststyle.HighlightStyle;
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
@ExcelSheet("LargeData")
public class LargeDataDTO {

    @ExcelColumn(header = "ID", order = 1, headerStyle = PurpleHeaderStyle.class)
    private Long id;

    @ExcelColumn(header = "Name", order = 2, columnStyle = HighlightStyle.class)
    private String name;

    @ExcelColumn(header = "Amount", order = 3, columnStyle = CurrencyStyle.class)
    private BigDecimal amount;

    @ExcelColumn(header = "Date", order = 4, columnStyle = DateOnlyStyle.class)
    private LocalDate date;

    @ExcelColumn(header = "Status", order = 5)
    private String status;

    @ExcelColumn(header = "Category", order = 6)
    private String category;

    @ExcelColumn(header = "Score", order = 7)
    private Integer score;

    @ExcelColumn(header = "Note", order = 8)
    private String note;

    @ExcelColumn(header = "Region", order = 9)
    private String region;

    @ExcelColumn(header = "Type", order = 10)
    private String type;
}
