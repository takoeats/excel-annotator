package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import lombok.Getter;

@Getter
@ExcelSheet(value = "고객", hasHeader = true)
public class CustomerPartBDTO {
    @ExcelColumn(header = "이메일", order = 3)
    private String email;

    @ExcelColumn(header = "전화번호", order = 4)
    private String phoneNumber;

    public CustomerPartBDTO(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}
