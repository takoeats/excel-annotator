package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ConditionalStyle;
import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.teststyle.CriticalAlertStyle;
import io.github.takoeats.excelannotator.teststyle.HighlightStyle;
import io.github.takoeats.excelannotator.teststyle.SignatureStyle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class ConditionalStyleTestDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("NumericConditions")
    public static class NumericConditionDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Amount",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value < 0", style = CriticalAlertStyle.class, priority = 20),
                        @ConditionalStyle(when = "value > 1000000", style = HighlightStyle.class, priority = 10)
                }
        )
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("StringConditions")
    public static class StringConditionDTO {
        @ExcelColumn(header = "Task", order = 1)
        private String task;

        @ExcelColumn(
                header = "Status",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value equals '완료'", style = SignatureStyle.class, priority = 10),
                        @ConditionalStyle(when = "value contains '진행'", style = HighlightStyle.class, priority = 5)
                }
        )
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("LogicalOperators")
    public static class LogicalOperatorDTO {
        @ExcelColumn(header = "Item", order = 1)
        private String item;

        @ExcelColumn(
                header = "Value",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value > 0 && value <= 100", style = SignatureStyle.class, priority = 20),
                        @ConditionalStyle(when = "value < 0 || value > 100000", style = CriticalAlertStyle.class, priority = 10)
                }
        )
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("PriorityTest")
    public static class PriorityTestDTO {
        @ExcelColumn(header = "Name", order = 1)
        private String name;

        @ExcelColumn(
                header = "Amount",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value < 0", style = CriticalAlertStyle.class, priority = 30),
                        @ConditionalStyle(when = "value < 100", style = HighlightStyle.class, priority = 20),
                        @ConditionalStyle(when = "value < 1000", style = SignatureStyle.class, priority = 10)
                }
        )
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ExcelSheet("BetweenCondition")
    public static class BetweenConditionDTO {
        @ExcelColumn(header = "Item", order = 1)
        private String item;

        @ExcelColumn(
                header = "Quantity",
                order = 2,
                conditionalStyles = {
                        @ConditionalStyle(when = "value between 10 and 100", style = HighlightStyle.class, priority = 10)
                }
        )
        private Integer quantity;
    }
}
