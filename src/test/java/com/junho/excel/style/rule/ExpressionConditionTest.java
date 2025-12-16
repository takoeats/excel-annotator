package com.junho.excel.style.rule;

import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.style.rule.node.ExpressionNode;
import com.junho.excel.style.rule.node.LeafNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionConditionTest {

    @AfterEach
    void cleanup() {
        CellContext context = CellContext.acquire();
        context.close();
    }

    @Test
    void constructor_withValidString_createsInstance() {
        ExpressionCondition condition = new ExpressionCondition("value > 0");
        assertNotNull(condition);
    }

    @Test
    void constructor_withInvalidString_throwsException() {
        assertThrows(ExcelExporterException.class, () -> {
            new ExpressionCondition("invalid <> syntax");
        });
    }

    @Test
    void constructor_withNull_throwsException() {
        assertThrows(Exception.class, () -> {
            new ExpressionCondition((String) null);
        });
    }

    @Test
    void constructor_withEmptyString_throwsException() {
        assertThrows(ExcelExporterException.class, () -> {
            new ExpressionCondition("");
        });
    }

    @Test
    void constructor_withExpressionNode_createsInstance() {
        ExpressionNode node = ExpressionParser.parseToTree("value > 0");
        ExpressionCondition condition = new ExpressionCondition(node);
        assertNotNull(condition);
    }

    @Test
    void constructor_withNullNode_allowsCreation() {
        assertDoesNotThrow(() -> {
            new ExpressionCondition((ExpressionNode) null);
        });
    }

    @Test
    void test_simpleNumericCondition_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition("value > 50");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(30, null, 0, 0, "field");
        assertFalse(condition.test(context));
    }

    @Test
    void test_simpleStringCondition_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition("value equals '완료'");
        CellContext context = CellContext.acquire();

        context.update("완료", null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update("진행중", null, 0, 0, "field");
        assertFalse(condition.test(context));
    }

    @Test
    void test_complexAndCondition_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition("value > 0 && value < 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(-10, null, 0, 0, "field");
        assertFalse(condition.test(context));

        context.update(150, null, 0, 0, "field");
        assertFalse(condition.test(context));
    }

    @Test
    void test_complexOrCondition_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition("value < 0 || value > 100");
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(50, null, 0, 0, "field");
        assertFalse(condition.test(context));
    }

    @Test
    void test_nestedParentheses_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition(
            "(value > 0 && value < 50) || (value > 100 && value < 150)"
        );
        CellContext context = CellContext.acquire();

        context.update(25, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(125, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(75, null, 0, 0, "field");
        assertFalse(condition.test(context));
    }

    @Test
    void test_notCondition_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition("!(value > 100)");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(150, null, 0, 0, "field");
        assertFalse(condition.test(context));
    }

    @Test
    void test_xorCondition_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition("value < 0 ^ value > 100");
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(50, null, 0, 0, "field");
        assertFalse(condition.test(context));

        context.update(-10, null, 0, 0, "field");
        context.update(150, null, 0, 0, "field");
    }

    @Test
    void test_betweenCondition_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition("value between 10 and 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(10, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(100, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(5, null, 0, 0, "field");
        assertFalse(condition.test(context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "value is_negative",
        "value is_positive",
        "value is_zero",
        "value is_null",
        "value is_not_null",
        "value is_empty",
        "value is_not_empty"
    })
    void test_specialConditions_evaluatesCorrectly(String expression) {
        ExpressionCondition condition = new ExpressionCondition(expression);
        CellContext context = CellContext.acquire();

        context.update(0, null, 0, 0, "field");
        assertNotNull(condition);
    }

    @Test
    void test_nullContext_handlesGracefully() {
        ExpressionCondition condition = new ExpressionCondition("value > 0");

        assertThrows(NullPointerException.class, () -> {
            condition.test(null);
        });
    }

    @Test
    void test_nullCellValue_handlesGracefully() {
        ExpressionCondition condition = new ExpressionCondition("value is_null");
        CellContext context = CellContext.acquire();

        context.update(null, null, 0, 0, "field");
        assertTrue(condition.test(context));
    }

    @Test
    void test_multipleEvaluations_consistentResults() {
        ExpressionCondition condition = new ExpressionCondition("value > 50");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertTrue(condition.test(context));
        assertTrue(condition.test(context));
        assertTrue(condition.test(context));

        context.update(30, null, 0, 0, "field");
        assertFalse(condition.test(context));
        assertFalse(condition.test(context));
        assertFalse(condition.test(context));
    }

    @Test
    void test_complexExpression_evaluatesCorrectly() {
        ExpressionCondition condition = new ExpressionCondition(
            "((value > 0 && value < 50) || (value > 100 && value < 150)) && !(value == 25)"
        );
        CellContext context = CellContext.acquire();

        context.update(30, null, 0, 0, "field");
        assertTrue(condition.test(context));

        context.update(25, null, 0, 0, "field");
        assertFalse(condition.test(context));

        context.update(125, null, 0, 0, "field");
        assertTrue(condition.test(context));
    }

    @Test
    void test_stringComparisons_evaluateCorrectly() {
        ExpressionCondition equalsCondition = new ExpressionCondition("value equals '완료'");
        ExpressionCondition containsCondition = new ExpressionCondition("value contains '진행'");
        ExpressionCondition startsWithCondition = new ExpressionCondition("value starts_with '주문'");

        CellContext context = CellContext.acquire();

        context.update("완료", null, 0, 0, "field");
        assertTrue(equalsCondition.test(context));

        context.update("진행중", null, 0, 0, "field");
        assertTrue(containsCondition.test(context));

        context.update("주문완료", null, 0, 0, "field");
        assertTrue(startsWithCondition.test(context));
    }
}
