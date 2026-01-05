package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;

@ExcelSheet(value = "AutoColumnExclude", autoColumn = true)
public class AutoColumnWithExcludeDTO {
    private String username;

    @ExcelColumn(exclude = true)
    private String password;

    private String email;
    private Integer age;

    public AutoColumnWithExcludeDTO(String username, String password, String email, Integer age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }
}
