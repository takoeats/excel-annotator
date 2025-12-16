package com.junho.excel.testdto;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.example.style.HighlightStyle;
import com.junho.excel.example.style.PurpleHeaderStyle;
import com.junho.excel.example.style.TableHeaderStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("StylePriority")
public class StylePriorityDTO {

    @ExcelColumn(
        header = "Header Style Only",
        order = 1,
        headerStyle = PurpleHeaderStyle.class
    )
    private String headerStyleOnly;

    @ExcelColumn(
        header = "Column Style Only",
        order = 2,
        columnStyle = HighlightStyle.class
    )
    private String columnStyleOnly;

    @ExcelColumn(
        header = "Both Styles",
        order = 3,
        headerStyle = TableHeaderStyle.class,
        columnStyle = HighlightStyle.class
    )
    private String bothStyles;

    @ExcelColumn(
        header = "No Style",
        order = 4
    )
    private String noStyle;
}
