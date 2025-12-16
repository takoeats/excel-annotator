package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import lombok.Getter;

@Getter
@ExcelSheet(value = "고객", hasHeader = true)
public class CustomerPartBDTO {
    @ExcelColumn(header = "이메일", order = 3)
    private String email;

    @ExcelColumn(header = "전화번호", order = 4)
    private String phoneNumber;

    public CustomerPartBDTO(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}
