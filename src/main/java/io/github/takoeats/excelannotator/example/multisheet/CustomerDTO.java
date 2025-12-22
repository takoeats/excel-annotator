package io.github.takoeats.excelannotator.example.multisheet;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.example.style.CurrencyStyle;
import io.github.takoeats.excelannotator.example.style.DateOnlyStyle;
import io.github.takoeats.excelannotator.example.style.PurpleHeaderStyle;
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
