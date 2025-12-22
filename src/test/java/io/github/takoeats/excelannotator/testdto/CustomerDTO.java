package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.teststyle.DateOnlyStyle;
import io.github.takoeats.excelannotator.teststyle.PurpleHeaderStyle;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("고객 목록")
public class CustomerDTO {

    @ExcelColumn(header = "고객번호", order = 1, width = 120, headerStyle = PurpleHeaderStyle.class)
    private String customerId;

    @ExcelColumn(header = "고객명", order = 2, width = 120, headerStyle = PurpleHeaderStyle.class)
    private String customerName;

    @ExcelColumn(header = "이메일", order = 3, width = 200, headerStyle = PurpleHeaderStyle.class)
    private String email;

    @ExcelColumn(header = "전화번호", order = 4, width = 130, headerStyle = PurpleHeaderStyle.class)
    private String phone;

    @ExcelColumn(header = "가입일", order = 5, width = 120,
        headerStyle = PurpleHeaderStyle.class,
        columnStyle = DateOnlyStyle.class)
    private LocalDate joinDate;

    @ExcelColumn(header = "VIP 여부", order = 6, width = 100, headerStyle = PurpleHeaderStyle.class)
    private Boolean vip;
}
