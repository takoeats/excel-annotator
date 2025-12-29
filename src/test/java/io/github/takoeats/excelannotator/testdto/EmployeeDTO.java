package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.teststyle.CurrencyStyle;
import io.github.takoeats.excelannotator.teststyle.DateOnlyStyle;
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
@ExcelSheet("직원 목록")
public class EmployeeDTO {

    @ExcelColumn(header = "사번", order = 1, width = 100, headerStyle = PurpleHeaderStyle.class)
    private String employeeId;

    @ExcelColumn(header = "이름", order = 2, width = 120, headerStyle = PurpleHeaderStyle.class)
    private String name;

    @ExcelColumn(header = "부서", order = 3, width = 120, headerStyle = PurpleHeaderStyle.class)
    private String department;

    @ExcelColumn(header = "직급", order = 4, width = 100, headerStyle = PurpleHeaderStyle.class)
    private String position;

    @ExcelColumn(header = "급여", order = 5, width = 150,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = CurrencyStyle.class)
    private BigDecimal salary;

    @ExcelColumn(header = "입사일", order = 6, width = 120,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DateOnlyStyle.class)
    private LocalDate hireDate;
}
