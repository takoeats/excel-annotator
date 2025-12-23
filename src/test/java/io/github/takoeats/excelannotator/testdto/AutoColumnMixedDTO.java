package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;

@ExcelSheet(value = "AutoColumnMixed", autoColumn = true)
public class AutoColumnMixedDTO {
    @ExcelColumn(header = "Full Name", order = 1, width = 150)
    private String name;

    private Integer age;

    @ExcelColumn(header = "Email Address", order = 3)
    private String email;

    private String phone;

    @ExcelColumn(exclude = true)
    private String internalId;

    public AutoColumnMixedDTO(String name, Integer age, String email, String phone, String internalId) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.internalId = internalId;
    }

    public String getName() { return name; }
    public Integer getAge() { return age; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getInternalId() { return internalId; }
}
