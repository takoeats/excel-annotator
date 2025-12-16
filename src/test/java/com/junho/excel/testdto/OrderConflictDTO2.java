package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import lombok.Getter;

@Getter
@ExcelSheet(value = "충돌테스트", hasHeader = true)
public class OrderConflictDTO2 {
    @ExcelColumn(header = "필드B2", order = 2)
    private String fieldB2;

    @ExcelColumn(header = "필드B3", order = 3)
    private String fieldB3;

    public OrderConflictDTO2(String fieldB2, String fieldB3) {
        this.fieldB2 = fieldB2;
        this.fieldB3 = fieldB3;
    }

}
