package io.github.takoeats.excelannotator.internal.metadata.extractor;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldTypeClassifierTest {

    @Test
    void isNumericType_withPrimitiveInt_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(int.class));
    }

    @Test
    void isNumericType_withPrimitiveLong_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(long.class));
    }

    @Test
    void isNumericType_withPrimitiveFloat_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(float.class));
    }

    @Test
    void isNumericType_withPrimitiveDouble_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(double.class));
    }

    @Test
    void isNumericType_withPrimitiveShort_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(short.class));
    }

    @Test
    void isNumericType_withPrimitiveByte_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(byte.class));
    }

    @Test
    void isNumericType_withInteger_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(Integer.class));
    }

    @Test
    void isNumericType_withLong_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(Long.class));
    }

    @Test
    void isNumericType_withFloat_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(Float.class));
    }

    @Test
    void isNumericType_withDouble_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(Double.class));
    }

    @Test
    void isNumericType_withShort_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(Short.class));
    }

    @Test
    void isNumericType_withByte_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(Byte.class));
    }

    @Test
    void isNumericType_withBigDecimal_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(BigDecimal.class));
    }

    @Test
    void isNumericType_withBigInteger_returnsTrue() {
        assertTrue(FieldTypeClassifier.isNumericType(BigInteger.class));
    }

    @Test
    void isNumericType_withString_returnsFalse() {
        assertFalse(FieldTypeClassifier.isNumericType(String.class));
    }

    @Test
    void isNumericType_withDate_returnsFalse() {
        assertFalse(FieldTypeClassifier.isNumericType(Date.class));
    }

    @Test
    void isNumericType_withObject_returnsFalse() {
        assertFalse(FieldTypeClassifier.isNumericType(Object.class));
    }

    @Test
    void isDateType_withLocalDate_returnsTrue() {
        assertTrue(FieldTypeClassifier.isDateType(LocalDate.class));
    }

    @Test
    void isDateType_withLocalDateTime_returnsTrue() {
        assertTrue(FieldTypeClassifier.isDateType(LocalDateTime.class));
    }

    @Test
    void isDateType_withDate_returnsTrue() {
        assertTrue(FieldTypeClassifier.isDateType(Date.class));
    }

    @Test
    void isDateType_withSqlDate_returnsTrue() {
        assertTrue(FieldTypeClassifier.isDateType(java.sql.Date.class));
    }

    @Test
    void isDateType_withSqlTimestamp_returnsTrue() {
        assertTrue(FieldTypeClassifier.isDateType(java.sql.Timestamp.class));
    }

    @Test
    void isDateType_withCalendar_returnsTrue() {
        assertTrue(FieldTypeClassifier.isDateType(Calendar.class));
    }

    @Test
    void isDateType_withString_returnsFalse() {
        assertFalse(FieldTypeClassifier.isDateType(String.class));
    }

    @Test
    void isDateType_withInteger_returnsFalse() {
        assertFalse(FieldTypeClassifier.isDateType(Integer.class));
    }

    @Test
    void isDateType_withObject_returnsFalse() {
        assertFalse(FieldTypeClassifier.isDateType(Object.class));
    }
}
