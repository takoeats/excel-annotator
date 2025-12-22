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
@ExcelSheet("ExcludeTest")
public class ExcludeTestDTO {

    @ExcelColumn(header = "Included Field 1", order = 1)
    private String includedField1;

    @ExcelColumn(header = "Excluded Field", order = 2, exclude = true)
    private String excludedField;

    @ExcelColumn(header = "Included Field 2", order = 3)
    private String includedField2;

    @ExcelColumn(header = "Default Exclude", order = 4, exclude = false)
    private String defaultExcludeField;
}
