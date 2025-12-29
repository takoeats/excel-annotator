package io.github.takoeats.excelannotator.style.internal.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ConditionEvaluatorTest {

    @Test
    void greaterThan_validNumber_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.greaterThan(100, 50));
        assertTrue(ConditionEvaluator.NumberComparator.greaterThan(50.1, 50));
        assertTrue(ConditionEvaluator.NumberComparator.greaterThan(new BigDecimal("100"), 50));
    }

    @Test
    void greaterThan_equalNumber_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(50, 50));
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(50.0, 50));
    }

    @Test
    void greaterThan_lesserNumber_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(30, 50));
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(49.9, 50));
    }

    @Test
    void greaterThan_null_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(null, 50));
    }

    @ParameterizedTest
    @ValueSource(strings = {"100", "100.5", "1,000", "1,000.50"})
    void greaterThan_stringNumber_returnsTrue(String value) {
        assertTrue(ConditionEvaluator.NumberComparator.greaterThan(value, 50));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "123abc", "not a number", "", "  "})
    void greaterThan_invalidString_returnsFalse(String value) {
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(value, 50));
    }

    @Test
    void greaterThan_doubleMaxValue_handlesCorrectly() {
        assertTrue(ConditionEvaluator.NumberComparator.greaterThan(Double.MAX_VALUE, 0));
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(0, Double.MAX_VALUE));
    }

    @Test
    void greaterThan_doubleMinValue_handlesCorrectly() {
        assertTrue(ConditionEvaluator.NumberComparator.greaterThan(Double.MIN_VALUE, 0));
        assertTrue(ConditionEvaluator.NumberComparator.greaterThan(0, -Double.MAX_VALUE));
    }

    @Test
    void greaterThan_infinity_handlesCorrectly() {
        assertTrue(ConditionEvaluator.NumberComparator.greaterThan(Double.POSITIVE_INFINITY, 0));
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(Double.NEGATIVE_INFINITY, 0));
    }

    @Test
    void greaterThan_nan_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(Double.NaN, 0));
    }

    @Test
    void lessThan_validNumber_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.lessThan(30, 50));
        assertTrue(ConditionEvaluator.NumberComparator.lessThan(-100, 0));
    }

    @Test
    void lessThan_equalNumber_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.lessThan(50, 50));
    }

    @Test
    void lessThan_null_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.lessThan(null, 50));
    }

    @Test
    void lessThanOrEqual_validNumber_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.lessThanOrEqual(30, 50));
        assertTrue(ConditionEvaluator.NumberComparator.lessThanOrEqual(-100, 0));
    }

    @Test
    void lessThanOrEqual_equalNumber_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.lessThanOrEqual(50, 50));
        assertTrue(ConditionEvaluator.NumberComparator.lessThanOrEqual(0, 0));
    }

    @Test
    void lessThanOrEqual_greaterNumber_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.lessThanOrEqual(100, 50));
    }

    @Test
    void greaterThanOrEqual_validNumber_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.greaterThanOrEqual(100, 50));
        assertTrue(ConditionEvaluator.NumberComparator.greaterThanOrEqual(50, 0));
    }

    @Test
    void greaterThanOrEqual_equalNumber_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.greaterThanOrEqual(50, 50));
        assertTrue(ConditionEvaluator.NumberComparator.greaterThanOrEqual(0, 0));
    }

    @Test
    void equals_equalNumbers_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.equals(50, 50));
        assertTrue(ConditionEvaluator.NumberComparator.equals(0, 0));
        assertTrue(ConditionEvaluator.NumberComparator.equals(-100, -100));
    }

    @Test
    void equals_differentNumbers_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.equals(50, 51));
        assertFalse(ConditionEvaluator.NumberComparator.equals(0, 1));
    }

    @Test
    void equals_floatingPointPrecision_handlesCorrectly() {
        assertTrue(ConditionEvaluator.NumberComparator.equals(50.00001, 50));
        assertTrue(ConditionEvaluator.NumberComparator.equals(49.99999, 50));
        assertFalse(ConditionEvaluator.NumberComparator.equals(50.1, 50));
    }

    @Test
    void notEquals_differentNumbers_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.notEquals(50, 51));
        assertTrue(ConditionEvaluator.NumberComparator.notEquals(0, 1));
    }

    @Test
    void notEquals_equalNumbers_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.notEquals(50, 50));
    }

    @Test
    void between_inRange_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.between(50, 10, 100));
        assertTrue(ConditionEvaluator.NumberComparator.between(0, -10, 10));
    }

    @Test
    void between_outsideRange_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.between(5, 10, 100));
        assertFalse(ConditionEvaluator.NumberComparator.between(150, 10, 100));
    }

    @Test
    void between_onBoundary_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.between(10, 10, 100));
        assertTrue(ConditionEvaluator.NumberComparator.between(100, 10, 100));
    }

    @Test
    void isNegative_negativeNumber_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.isNegative(-1));
        assertTrue(ConditionEvaluator.NumberComparator.isNegative(-100.5));
        assertTrue(ConditionEvaluator.NumberComparator.isNegative(new BigDecimal("-0.1")));
    }

    @Test
    void isNegative_positiveNumber_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.isNegative(1));
        assertFalse(ConditionEvaluator.NumberComparator.isNegative(100.5));
    }

    @Test
    void isNegative_zero_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.isNegative(0));
        assertFalse(ConditionEvaluator.NumberComparator.isNegative(0.0));
    }

    @Test
    void isPositive_positiveNumber_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.isPositive(1));
        assertTrue(ConditionEvaluator.NumberComparator.isPositive(100.5));
        assertTrue(ConditionEvaluator.NumberComparator.isPositive(new BigDecimal("0.1")));
    }

    @Test
    void isPositive_negativeNumber_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.isPositive(-1));
        assertFalse(ConditionEvaluator.NumberComparator.isPositive(-100.5));
    }

    @Test
    void isPositive_zero_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.isPositive(0));
        assertFalse(ConditionEvaluator.NumberComparator.isPositive(0.0));
    }

    @Test
    void isZero_zero_returnsTrue() {
        assertTrue(ConditionEvaluator.NumberComparator.isZero(0));
        assertTrue(ConditionEvaluator.NumberComparator.isZero(0.0));
        assertTrue(ConditionEvaluator.NumberComparator.isZero(0.00001));
    }

    @Test
    void isZero_nonZero_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.isZero(1));
        assertFalse(ConditionEvaluator.NumberComparator.isZero(-1));
        assertFalse(ConditionEvaluator.NumberComparator.isZero(0.1));
    }

    @Test
    void stringEquals_equalStrings_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.equals("test", "test"));
        assertTrue(ConditionEvaluator.StringComparator.equals("완료", "완료"));
    }

    @Test
    void stringEquals_differentStrings_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.equals("test", "Test"));
        assertFalse(ConditionEvaluator.StringComparator.equals("완료", "진행중"));
    }

    @Test
    void stringEquals_caseSensitive_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.equals("test", "TEST"));
        assertFalse(ConditionEvaluator.StringComparator.equals("Test", "test"));
    }

    @Test
    void stringEquals_null_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.equals(null, "test"));
    }

    @Test
    void equalsIgnoreCase_differentCase_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.equalsIgnoreCase("test", "TEST"));
        assertTrue(ConditionEvaluator.StringComparator.equalsIgnoreCase("Test", "test"));
        assertTrue(ConditionEvaluator.StringComparator.equalsIgnoreCase("TeSt", "tEsT"));
    }

    @Test
    void equalsIgnoreCase_differentStrings_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.equalsIgnoreCase("test", "other"));
    }

    @Test
    void contains_substring_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.contains("hello world", "world"));
        assertTrue(ConditionEvaluator.StringComparator.contains("진행중", "진행"));
        assertTrue(ConditionEvaluator.StringComparator.contains("test", "test"));
    }

    @Test
    void contains_notFound_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.contains("hello", "world"));
    }

    @Test
    void contains_emptySubstring_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.contains("test", ""));
    }

    @Test
    void startsWith_validPrefix_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.startsWith("hello world", "hello"));
        assertTrue(ConditionEvaluator.StringComparator.startsWith("진행중", "진행"));
        assertTrue(ConditionEvaluator.StringComparator.startsWith("test", "test"));
    }

    @Test
    void startsWith_invalidPrefix_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.startsWith("hello world", "world"));
    }

    @Test
    void endsWith_validSuffix_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.endsWith("hello world", "world"));
        assertTrue(ConditionEvaluator.StringComparator.endsWith("진행중", "중"));
        assertTrue(ConditionEvaluator.StringComparator.endsWith("test", "test"));
    }

    @Test
    void endsWith_invalidSuffix_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.endsWith("hello world", "hello"));
    }

    @Test
    void isEmpty_null_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.isEmpty(null));
    }

    @Test
    void isEmpty_emptyString_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.isEmpty(""));
    }

    @Test
    void isEmpty_whitespaceOnly_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.isEmpty("   "));
        assertTrue(ConditionEvaluator.StringComparator.isEmpty("\t"));
        assertTrue(ConditionEvaluator.StringComparator.isEmpty("\n"));
    }

    @Test
    void isEmpty_nonEmpty_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.isEmpty("test"));
        assertFalse(ConditionEvaluator.StringComparator.isEmpty(" test "));
    }

    @Test
    void isNotEmpty_nonEmpty_returnsTrue() {
        assertTrue(ConditionEvaluator.StringComparator.isNotEmpty("test"));
        assertTrue(ConditionEvaluator.StringComparator.isNotEmpty(" test "));
    }

    @Test
    void isNotEmpty_empty_returnsFalse() {
        assertFalse(ConditionEvaluator.StringComparator.isNotEmpty(null));
        assertFalse(ConditionEvaluator.StringComparator.isNotEmpty(""));
        assertFalse(ConditionEvaluator.StringComparator.isNotEmpty("   "));
    }

    @Test
    void getConditionInstance_firstCall_createsInstance() {
        StyleCondition condition = ConditionEvaluator.getConditionInstance(TestCondition.class);
        assertNotNull(condition);
        assertTrue(condition instanceof TestCondition);
    }

    @Test
    void getConditionInstance_secondCall_returnsCachedInstance() {
        StyleCondition first = ConditionEvaluator.getConditionInstance(TestCondition.class);
        StyleCondition second = ConditionEvaluator.getConditionInstance(TestCondition.class);
        assertSame(first, second);
    }

    @Test
    void getConditionInstance_noDefaultConstructor_throwsException() {
        assertThrows(Exception.class, () ->
                ConditionEvaluator.getConditionInstance(NoDefaultConstructorCondition.class)
        );
    }

    @Test
    void evaluate_withValidCondition_returnsResult() {
        CellContext context = CellContext.acquire();
        try {
            context.update(100, null, 0, 0, "field");
            assertTrue(ConditionEvaluator.evaluate(TestCondition.class, context));
        } finally {
            context.close();
        }
    }

    @Test
    void evaluate_withNullContext_returnsFalse() {
        assertFalse(ConditionEvaluator.evaluate(TestCondition.class, null));
    }

    @Test
    void evaluate_withNullConditionClass_returnsFalse() {
        CellContext context = CellContext.acquire();
        try {
            context.update(100, null, 0, 0, "field");
            assertFalse(ConditionEvaluator.evaluate(null, context));
        } finally {
            context.close();
        }
    }

    @Test
    void greaterThan_booleanValue_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(Boolean.TRUE, 0));
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(Boolean.FALSE, 0));
    }

    @Test
    void greaterThan_listValue_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(new ArrayList<>(), 0));
    }

    @Test
    void greaterThan_mapValue_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(new HashMap<>(), 0));
    }

    @Test
    void greaterThan_customObject_returnsFalse() {
        Object customObject = new Object();
        assertFalse(ConditionEvaluator.NumberComparator.greaterThan(customObject, 0));
    }

    @Test
    void lessThan_booleanValue_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.lessThan(Boolean.TRUE, 100));
        assertFalse(ConditionEvaluator.NumberComparator.lessThan(Boolean.FALSE, 100));
    }

    @Test
    void between_booleanValue_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.between(Boolean.TRUE, 0, 100));
    }

    @Test
    void isNegative_booleanValue_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.isNegative(Boolean.TRUE));
        assertFalse(ConditionEvaluator.NumberComparator.isNegative(Boolean.FALSE));
    }

    @Test
    void isPositive_customObject_returnsFalse() {
        Object customObject = new Object();
        assertFalse(ConditionEvaluator.NumberComparator.isPositive(customObject));
    }

    @Test
    void isZero_listValue_returnsFalse() {
        assertFalse(ConditionEvaluator.NumberComparator.isZero(new ArrayList<>()));
    }

    public static class TestCondition implements StyleCondition {
        @Override
        public boolean test(CellContext context) {
            return true;
        }
    }

    public static class NoDefaultConstructorCondition implements StyleCondition {
        private final String value;

        public NoDefaultConstructorCondition(String value) {
            this.value = value;
        }

        @Override
        public boolean test(CellContext context) {
            return false;
        }
    }
}
