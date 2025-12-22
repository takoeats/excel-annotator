package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.teststyle.PurpleHeaderStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("DefaultSheet")
public class PriorityTestDTO {

    @ExcelColumn(
        header = "Sheet Name Priority Test",
        order = 1,
        sheetName = "ColumnSheetName"
    )
    private String testField;

    @ExcelColumn(
        header = "Width Priority",
        order = 2,
        width = 200,
        headerStyle = PurpleHeaderStyle.class
    )
    private String widthField;

    @ExcelColumn(
        header = "Order Field",
        order = 1
    )
    private String orderField;
}
