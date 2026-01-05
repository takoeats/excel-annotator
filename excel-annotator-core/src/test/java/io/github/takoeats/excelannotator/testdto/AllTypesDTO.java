package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.teststyle.DateOnlyStyle;
import io.github.takoeats.excelannotator.teststyle.DateTimeStyle;
import io.github.takoeats.excelannotator.teststyle.DecimalNumberStyle;
import io.github.takoeats.excelannotator.teststyle.PurpleHeaderStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("전체 타입 테스트")
public class AllTypesDTO {

    @ExcelColumn(header = "String", order = 1, width = 120, headerStyle = PurpleHeaderStyle.class)
    private String stringValue;

    @ExcelColumn(header = "Integer", order = 2, width = 100, headerStyle = PurpleHeaderStyle.class)
    private Integer integerValue;

    @ExcelColumn(header = "Long", order = 3, width = 120, headerStyle = PurpleHeaderStyle.class)
    private Long longValue;

    @ExcelColumn(header = "Double", order = 4, width = 120, headerStyle = PurpleHeaderStyle.class)
    private Double doubleValue;

    @ExcelColumn(header = "Float", order = 5, width = 120, headerStyle = PurpleHeaderStyle.class)
    private Float floatValue;

    @ExcelColumn(header = "Boolean", order = 6, width = 100, headerStyle = PurpleHeaderStyle.class)
    private Boolean booleanValue;

    @ExcelColumn(header = "BigDecimal", order = 7, width = 150,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DecimalNumberStyle.class)
    private BigDecimal bigDecimalValue;

    @ExcelColumn(header = "BigInteger", order = 8, width = 150,
            headerStyle = PurpleHeaderStyle.class)
    private BigInteger bigIntegerValue;

    @ExcelColumn(header = "LocalDate", order = 9, width = 120,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DateOnlyStyle.class)
    private LocalDate localDateValue;

    @ExcelColumn(header = "LocalDateTime", order = 10, width = 180,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DateTimeStyle.class)
    private LocalDateTime localDateTimeValue;

    @ExcelColumn(header = "ZonedDateTime", order = 11, width = 200,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DateTimeStyle.class)
    private ZonedDateTime zonedDateTimeValue;

    @ExcelColumn(header = "Enum", order = 12, width = 100, headerStyle = PurpleHeaderStyle.class)
    private StatusEnum enumValue;

    @ExcelColumn(header = "JavaUtilDate", order = 13, width = 180,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DateTimeStyle.class)
    private Date javaUtilDateValue;

    @ExcelColumn(header = "JavaSqlDate", order = 14, width = 120,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DateOnlyStyle.class)
    private java.sql.Date javaSqlDateValue;

    @ExcelColumn(header = "JavaSqlTimestamp", order = 15, width = 180,
            headerStyle = PurpleHeaderStyle.class,
            columnStyle = DateTimeStyle.class)
    private java.sql.Timestamp javaSqlTimestampValue;

    public enum StatusEnum {
        ACTIVE, INACTIVE, PENDING, COMPLETED, CANCELLED
    }
}
