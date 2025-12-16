package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.PurpleHeaderStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("부서 목록")
public class DepartmentDTO {

    @ExcelColumn(header = "부서코드", order = 1, width = 100, headerStyle = PurpleHeaderStyle.class)
    private String departmentCode;

    @ExcelColumn(header = "부서명", order = 2, width = 150, headerStyle = PurpleHeaderStyle.class)
    private String departmentName;

    @ExcelColumn(header = "위치", order = 3, width = 120, headerStyle = PurpleHeaderStyle.class)
    private String location;

    @ExcelColumn(header = "직원 수", order = 4, width = 100, headerStyle = PurpleHeaderStyle.class)
    private Integer employeeCount;

    @ExcelColumn(header = "부서장", order = 5, width = 120, headerStyle = PurpleHeaderStyle.class)
    private String manager;
}
