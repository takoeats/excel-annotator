package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ExcelSheet("TestSheet")
public class NoExcelColumnsDTO {
    private String field1;
    private String field2;
}
