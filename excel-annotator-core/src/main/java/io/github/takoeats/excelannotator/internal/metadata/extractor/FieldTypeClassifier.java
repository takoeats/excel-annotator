package io.github.takoeats.excelannotator.internal.metadata.extractor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldTypeClassifier {


    public static boolean isNumericType(Class<?> fieldType) {
        return fieldType == int.class || fieldType == Integer.class
                || fieldType == long.class || fieldType == Long.class
                || fieldType == float.class || fieldType == Float.class
                || fieldType == double.class || fieldType == Double.class
                || fieldType == short.class || fieldType == Short.class
                || fieldType == byte.class || fieldType == Byte.class
                || fieldType == BigDecimal.class
                || fieldType == BigInteger.class;
    }

    public static boolean isDateType(Class<?> fieldType) {
        return fieldType == LocalDate.class
                || fieldType == LocalDateTime.class
                || fieldType == Date.class
                || fieldType == java.sql.Date.class
                || fieldType == java.sql.Timestamp.class
                || fieldType == Calendar.class;
    }
}
