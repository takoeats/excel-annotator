package com.junho.excel.style.rule;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StyleRuleTest {

    @AfterEach
    void cleanup() {
        CellContext context = CellContext.acquire();
        context.close();
    }

    @Test
    void builder_minimalFields_createsInstance() {
        StyleRule rule = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .build();

        assertNotNull(rule);
        assertNotNull(rule.getCondition());
        assertEquals(TestStyle.class, rule.getStyleClass());
    }

    @Test
    void builder_allFields_createsInstance() {
        StyleCondition condition = context -> context.getCellValue() != null;

        StyleRule rule = StyleRule.builder()
            .condition(condition)
            .styleClass(TestStyle.class)
            .priority(10)
            .build();

        assertNotNull(rule);
        assertEquals(condition, rule.getCondition());
        assertEquals(TestStyle.class, rule.getStyleClass());
        assertEquals(10, rule.getPriority());
    }

    @Test
    void builder_defaultPriority_isZero() {
        StyleRule rule = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .build();

        assertEquals(0, rule.getPriority());
    }

    @Test
    void evaluate_conditionMet_returnsTrue() {
        StyleCondition condition = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 50;
        };

        StyleRule rule = StyleRule.builder()
            .condition(condition)
            .styleClass(TestStyle.class)
            .build();

        CellContext context = CellContext.acquire();
        context.update(100, null, 0, 0, "field");

        assertTrue(rule.evaluate(context));
    }

    @Test
    void evaluate_conditionNotMet_returnsFalse() {
        StyleCondition condition = context -> {
            Object value = context.getCellValue();
            return value instanceof Integer && (Integer) value > 50;
        };

        StyleRule rule = StyleRule.builder()
            .condition(condition)
            .styleClass(TestStyle.class)
            .build();

        CellContext context = CellContext.acquire();
        context.update(30, null, 0, 0, "field");

        assertFalse(rule.evaluate(context));
    }

    @Test
    void evaluate_nullCondition_returnsFalse() {
        StyleRule rule = StyleRule.builder()
            .condition(null)
            .styleClass(TestStyle.class)
            .build();

        CellContext context = CellContext.acquire();
        context.update(100, null, 0, 0, "field");

        assertFalse(rule.evaluate(context));
    }

    @Test
    void evaluate_nullContext_returnsFalse() {
        StyleRule rule = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .build();

        assertFalse(rule.evaluate(null));
    }

    @Test
    void evaluate_nullBoth_returnsFalse() {
        StyleRule rule = StyleRule.builder()
            .condition(null)
            .styleClass(TestStyle.class)
            .build();

        assertFalse(rule.evaluate(null));
    }

    @Test
    void compareTo_higherPriority_comesFirst() {
        StyleRule highPriority = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(10)
            .build();

        StyleRule lowPriority = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(5)
            .build();

        assertTrue(highPriority.compareTo(lowPriority) < 0);
    }

    @Test
    void compareTo_lowerPriority_comesSecond() {
        StyleRule highPriority = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(10)
            .build();

        StyleRule lowPriority = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(5)
            .build();

        assertTrue(lowPriority.compareTo(highPriority) > 0);
    }

    @Test
    void compareTo_samePriority_returnsZero() {
        StyleRule rule1 = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(10)
            .build();

        StyleRule rule2 = StyleRule.builder()
            .condition(context -> false)
            .styleClass(AnotherTestStyle.class)
            .priority(10)
            .build();

        assertEquals(0, rule1.compareTo(rule2));
    }

    @Test
    void compareTo_negativeVsPositive_ordersCorrectly() {
        StyleRule positive = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(10)
            .build();

        StyleRule negative = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(-5)
            .build();

        assertTrue(positive.compareTo(negative) < 0);
        assertTrue(negative.compareTo(positive) > 0);
    }

    @Test
    void compareTo_multipleRules_sortsCorrectly() {
        StyleRule priority20 = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(20)
            .build();

        StyleRule priority10 = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(10)
            .build();

        StyleRule priority5 = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(5)
            .build();

        StyleRule priority0 = StyleRule.builder()
            .condition(context -> true)
            .styleClass(TestStyle.class)
            .priority(0)
            .build();

        List<StyleRule> rules = new ArrayList<>();
        rules.add(priority5);
        rules.add(priority20);
        rules.add(priority0);
        rules.add(priority10);

        Collections.sort(rules);

        assertEquals(priority20, rules.get(0));
        assertEquals(priority10, rules.get(1));
        assertEquals(priority5, rules.get(2));
        assertEquals(priority0, rules.get(3));
    }

    @Test
    void getters_afterBuild_returnCorrectValues() {
        StyleCondition condition = context -> true;

        StyleRule rule = StyleRule.builder()
            .condition(condition)
            .styleClass(TestStyle.class)
            .priority(15)
            .build();

        assertEquals(condition, rule.getCondition());
        assertEquals(TestStyle.class, rule.getStyleClass());
        assertEquals(15, rule.getPriority());
    }

    @Test
    void evaluate_complexCondition_evaluatesCorrectly() {
        StyleCondition complexCondition = context -> {
            Object value = context.getCellValue();
            if (!(value instanceof Integer)) {
                return false;
            }
            int intValue = (Integer) value;
            return intValue > 0 && intValue < 100;
        };

        StyleRule rule = StyleRule.builder()
            .condition(complexCondition)
            .styleClass(TestStyle.class)
            .priority(10)
            .build();

        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(rule.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertFalse(rule.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertFalse(rule.evaluate(context));
    }

    public static class TestStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }

    public static class AnotherTestStyle extends CustomExcelCellStyle {
        @Override
        protected void configure(ExcelCellStyleConfigurer configurer) {
        }
    }
}
