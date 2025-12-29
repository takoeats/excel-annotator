package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelSheet;

@ExcelSheet(value = "AutoColumn", autoColumn = true)
public class AutoColumnDTO {
    private String name;
    private Integer age;
    private String email;
    private Double salary;

    public AutoColumnDTO(String name, Integer age, String email, Double salary) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public Double getSalary() {
        return salary;
    }
}
