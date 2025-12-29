package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;

@ExcelSheet(value = "충돌테스트", hasHeader = true)
public class OrderConflictDTO1 {
    @ExcelColumn(header = "필드A1", order = 1)
    private String fieldA1;

    @ExcelColumn(header = "필드A2", order = 2)
    private String fieldA2;

    public OrderConflictDTO1(String fieldA1, String fieldA2) {
        this.fieldA1 = fieldA1;
        this.fieldA2 = fieldA2;
    }

    public String getFieldA1() {
        return fieldA1;
    }

    public String getFieldA2() {
        return fieldA2;
    }
}
