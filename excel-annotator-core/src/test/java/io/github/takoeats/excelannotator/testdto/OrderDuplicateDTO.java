package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("OrderDuplicate")
public class OrderDuplicateDTO {

    @ExcelColumn(header = "Field A", order = 1)
    private String fieldA;

    @ExcelColumn(header = "Field B", order = 1)
    private String fieldB;

    @ExcelColumn(header = "Field C", order = 2)
    private String fieldC;
}
