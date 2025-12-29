package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;

@ExcelSheet(value = "DefaultSheet", hasHeader = true)
public class MultiSheetColumnDTO {
    @ExcelColumn(header = "ColA", order = 1, sheetName = "SheetA")
    private String a;

    @ExcelColumn(header = "ColB", order = 2, sheetName = "SheetB")
    private String b;

    public MultiSheetColumnDTO(String a, String b) {
        this.a = a;
        this.b = b;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }
}