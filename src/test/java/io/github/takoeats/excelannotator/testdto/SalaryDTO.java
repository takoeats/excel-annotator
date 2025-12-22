package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.example.style.CurrencyStyle;

import java.math.BigDecimal;

@ExcelSheet(value = "Salaries", hasHeader = true)
public class SalaryDTO {
    @ExcelColumn(header = "Employee", order = 1, width = 120)
    private String employeeName;

    @ExcelColumn(header = "Salary (Currency Style)", order = 2, columnStyle = CurrencyStyle.class)
    private BigDecimal salary;

    @ExcelColumn(header = "Bonus (Annotation Format)", order = 3, format = "#,##0.00")
    private BigDecimal bonus;

    public SalaryDTO(String employeeName, BigDecimal salary, BigDecimal bonus) {
        this.employeeName = employeeName;
        this.salary = salary;
        this.bonus = bonus;
    }

    public String getEmployeeName() { return employeeName; }
    public BigDecimal getSalary() { return salary; }
    public BigDecimal getBonus() { return bonus; }
}
