package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.CurrencyStyle;
import com.junho.excel.example.style.DateOnlyStyle;
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
