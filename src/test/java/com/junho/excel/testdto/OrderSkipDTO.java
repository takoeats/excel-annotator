package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("OrderSkip")
public class OrderSkipDTO {

    @ExcelColumn(header = "Field Order 1", order = 1)
    private String field1;

    @ExcelColumn(header = "Field Order 3", order = 3)
    private String field3;

    @ExcelColumn(header = "Field Order 5", order = 5)
    private String field5;

    @ExcelColumn(header = "Field Order 7", order = 7)
    private String field7;
}
