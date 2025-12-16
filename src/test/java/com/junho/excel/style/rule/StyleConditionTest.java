package com.junho.excel.style.rule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StyleConditionTest {

    @AfterEach
    void cleanup() {
        CellContext context = CellContext.acquire();
        context.close();
    }

    @Test
    void and_bothTrue_returnsTrue() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 100;
        };

        StyleCondition combined = condition1.and(condition2);
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(combined.test(context));
    }

    @Test
    void and_leftFalse_returnsFalse() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 100;
        };

        StyleCondition combined = condition1.and(condition2);
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertFalse(combined.test(context));
    }

    @Test
    void and_rightFalse_returnsFalse() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 100;
        };

        StyleCondition combined = condition1.and(condition2);
        CellContext context = CellContext.acquire();

        context.update(150, null, 0, 0, "field");
        assertFalse(combined.test(context));
    }

    @Test
    void and_bothFalse_returnsFalse() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 100;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 0;
        };

        StyleCondition combined = condition1.and(condition2);
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertFalse(combined.test(context));
    }

    @Test
    void and_chainMultiple_evaluatesCorrectly() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 100;
        };
        StyleCondition condition3 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value != 50;
        };

        StyleCondition combined = condition1.and(condition2).and(condition3);
        CellContext context = CellContext.acquire();

        context.update(30, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(50, null, 0, 0, "field");
        assertFalse(combined.test(context));
    }

    @Test
    void or_bothTrue_returnsTrue() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 100;
        };

        StyleCondition combined = condition1.or(condition2);
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(combined.test(context));
    }

    @Test
    void or_leftTrue_returnsTrue() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 100;
        };

        StyleCondition combined = condition1.or(condition2);
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(combined.test(context));
    }

    @Test
    void or_rightTrue_returnsTrue() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 100;
        };

        StyleCondition combined = condition1.or(condition2);
        CellContext context = CellContext.acquire();

        context.update(150, null, 0, 0, "field");
        assertTrue(combined.test(context));
    }

    @Test
    void or_bothFalse_returnsFalse() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 100;
        };

        StyleCondition combined = condition1.or(condition2);
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertFalse(combined.test(context));
    }

    @Test
    void or_chainMultiple_evaluatesCorrectly() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 100;
        };
        StyleCondition condition3 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value == 50;
        };

        StyleCondition combined = condition1.or(condition2).or(condition3);
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(50, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(30, null, 0, 0, "field");
        assertFalse(combined.test(context));
    }

    @Test
    void negate_true_returnsFalse() {
        StyleCondition condition = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 50;
        };

        StyleCondition negated = condition.negate();
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertFalse(negated.test(context));
    }

    @Test
    void negate_false_returnsTrue() {
        StyleCondition condition = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 50;
        };

        StyleCondition negated = condition.negate();
        CellContext context = CellContext.acquire();

        context.update(30, null, 0, 0, "field");
        assertTrue(negated.test(context));
    }

    @Test
    void combinedConditions_andOrNegate_evaluatesCorrectly() {
        StyleCondition condition1 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 0;
        };
        StyleCondition condition2 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 50;
        };
        StyleCondition condition3 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value == 25;
        };

        StyleCondition combined = condition1.and(condition2).or(condition3.negate());
        CellContext context = CellContext.acquire();

        context.update(30, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(75, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(25, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(-10, null, 0, 0, "field");
        assertTrue(combined.test(context));
    }

    @Test
    void stringConditions_andOr_evaluatesCorrectly() {
        StyleCondition containsCondition = context -> {
            Object value = context.getCellValue();
            return value != null && value.toString().contains("\uc9c4\ud589");
        };
        StyleCondition equalsCondition = context -> {
            Object value = context.getCellValue();
            return value != null && value.toString().equals("\uc644\ub8cc");
        };

        StyleCondition combined = containsCondition.or(equalsCondition);
        CellContext context = CellContext.acquire();

        context.update("\uc9c4\ud589\uc911", null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update("\uc644\ub8cc", null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update("\ub300\uae30", null, 0, 0, "field");
        assertFalse(combined.test(context));
    }

    @Test
    void nullValueConditions_handledCorrectly() {
        StyleCondition notNullCondition = context -> context.getCellValue() != null;
        StyleCondition typeCheckCondition = context -> {
            Object value = context.getCellValue();
            return value instanceof String;
        };

        StyleCondition combined = notNullCondition.and(typeCheckCondition);
        CellContext context = CellContext.acquire();

        context.update("test", null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(null, null, 0, 0, "field");
        assertFalse(combined.test(context));

        context.update(123, null, 0, 0, "field");
        assertFalse(combined.test(context));
    }

    @Test
    void complexCombination_multipleChains_evaluatesCorrectly() {
        StyleCondition positive = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 0;
        };
        StyleCondition lessThan100 = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value < 100;
        };
        StyleCondition notEqual50 = context -> {
            Object value = context.getCellValue();
            return !(value instanceof Integer && (Integer) value == 50);
        };

        StyleCondition combined = positive.and(lessThan100).and(notEqual50);
        CellContext context = CellContext.acquire();

        context.update(30, null, 0, 0, "field");
        assertTrue(combined.test(context));

        context.update(50, null, 0, 0, "field");
        assertFalse(combined.test(context));

        context.update(-10, null, 0, 0, "field");
        assertFalse(combined.test(context));

        context.update(150, null, 0, 0, "field");
        assertFalse(combined.test(context));
    }
}
