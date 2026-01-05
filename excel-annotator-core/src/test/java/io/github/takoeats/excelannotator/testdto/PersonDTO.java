package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;

@ExcelSheet(value = "Persons", hasHeader = true)
public class PersonDTO {
    @ExcelColumn(header = "Name", order = 1, width = 120)
    private String name;

    @ExcelColumn(header = "Age", order = 2, width = 80, format = "0")
    private Integer age;

    @ExcelColumn(header = "Salary", order = 3, width = 120, format = "#,##0.00")
    private java.math.BigDecimal salary;

    public PersonDTO(String name, Integer age, java.math.BigDecimal salary) {
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public java.math.BigDecimal getSalary() {
        return salary;
    }
}