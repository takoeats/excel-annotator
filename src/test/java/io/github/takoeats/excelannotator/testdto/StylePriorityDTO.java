package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.example.style.HighlightStyle;
import io.github.takoeats.excelannotator.example.style.PurpleHeaderStyle;
import io.github.takoeats.excelannotator.example.style.TableHeaderStyle;
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
