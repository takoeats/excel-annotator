package com.junho.excel.style.rule.node;

import com.junho.excel.style.rule.CellContext;
import com.junho.excel.style.rule.ExpressionParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeafNodeTest {

    @AfterEach
    void cleanup() {
        CellContext context = CellContext.acquire();
        context.close();
    }

    @Test
    void evaluate_lessThan_true() {
        LeafNode node = createLeafNode("value < 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_lessThan_false() {
        LeafNode node = createLeafNode("value < 100");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_lessThan_null() {
        LeafNode node = createLeafNode("value < 100");
        CellContext context = CellContext.acquire();

        context.update(null, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_lessThanOrEqual_true() {
        LeafNode node = createLeafNode("value <= 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_lessThanOrEqual_equalBoundary() {
        LeafNode node = createLeafNode("value <= 100");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_lessThanOrEqual_false() {
        LeafNode node = createLeafNode("value <= 100");
        CellContext context = CellContext.acquire();

        context.update(150, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_greaterThan_true() {
        LeafNode node = createLeafNode("value > 100");
        CellContext context = CellContext.acquire();

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(200, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_greaterThan_false() {
        LeafNode node = createLeafNode("value > 100");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_greaterThanOrEqual_true() {
        LeafNode node = createLeafNode("value >= 100");
        CellContext context = CellContext.acquire();

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_greaterThanOrEqual_equalBoundary() {
        LeafNode node = createLeafNode("value >= 100");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_equals_true() {
        LeafNode node = createLeafNode("value == 100");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(100.00001, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_equals_false() {
        LeafNode node = createLeafNode("value == 100");
        CellContext context = CellContext.acquire();

        context.update(101, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_equals_floatingPoint() {
        LeafNode node = createLeafNode("value == 100.5");
        CellContext context = CellContext.acquire();

        context.update(100.5, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(100.50001, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_notEquals_true() {
        LeafNode node = createLeafNode("value != 100");
        CellContext context = CellContext.acquire();

        context.update(101, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_notEquals_false() {
        LeafNode node = createLeafNode("value != 100");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_between_inRange() {
        LeafNode node = createLeafNode("value between 10 and 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(75, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_between_onMinBoundary() {
        LeafNode node = createLeafNode("value between 10 and 100");
        CellContext context = CellContext.acquire();

        context.update(10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_between_onMaxBoundary() {
        LeafNode node = createLeafNode("value between 10 and 100");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_between_outsideRange() {
        LeafNode node = createLeafNode("value between 10 and 100");
        CellContext context = CellContext.acquire();

        context.update(5, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_stringEquals_true() {
        LeafNode node = createLeafNode("value equals '완료'");
        CellContext context = CellContext.acquire();

        context.update("완료", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_stringEquals_false() {
        LeafNode node = createLeafNode("value equals '완료'");
        CellContext context = CellContext.acquire();

        context.update("진행중", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_stringEquals_caseSensitive_exactMatch() {
        LeafNode node = createLeafNode("value equals '\ud14c\uc2a4\ud2b8'");
        CellContext context = CellContext.acquire();

        context.update("\ud14c\uc2a4\ud2b8", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_stringEquals_caseSensitive_different() {
        LeafNode node = createLeafNode("value equals '\ud14c\uc2a4\ud2b8'");
        CellContext context = CellContext.acquire();

        context.update("\uc644\ub8cc", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_stringEqualsIgnoreCase_true() {
        LeafNode node = createLeafNode("value equals_ignore_case 'Test'");
        CellContext context = CellContext.acquire();

        context.update("Test", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("test", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("TEST", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("TeSt", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_stringEqualsIgnoreCase_false() {
        LeafNode node = createLeafNode("value equals_ignore_case 'Test'");
        CellContext context = CellContext.acquire();

        context.update("Other", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_stringContains_true() {
        LeafNode node = createLeafNode("value contains '진행'");
        CellContext context = CellContext.acquire();

        context.update("진행중", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("작업 진행 중입니다", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("진행", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_stringContains_false() {
        LeafNode node = createLeafNode("value contains '진행'");
        CellContext context = CellContext.acquire();

        context.update("완료", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("대기", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_stringStartsWith_true() {
        LeafNode node = createLeafNode("value starts_with '주문'");
        CellContext context = CellContext.acquire();

        context.update("주문완료", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("주문", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("주문취소", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_stringStartsWith_false() {
        LeafNode node = createLeafNode("value starts_with '주문'");
        CellContext context = CellContext.acquire();

        context.update("배송완료", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("취소주문", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_stringEndsWith_true() {
        LeafNode node = createLeafNode("value ends_with '완료'");
        CellContext context = CellContext.acquire();

        context.update("주문완료", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("완료", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("배송완료", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_stringEndsWith_false() {
        LeafNode node = createLeafNode("value ends_with '완료'");
        CellContext context = CellContext.acquire();

        context.update("완료됨", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("진행중", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_isNegative_true() {
        LeafNode node = createLeafNode("value is_negative");
        CellContext context = CellContext.acquire();

        context.update(-1, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(-100.5, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(-0.1, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isNegative_false() {
        LeafNode node = createLeafNode("value is_negative");
        CellContext context = CellContext.acquire();

        context.update(1, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(0, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(100.5, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_isPositive_true() {
        LeafNode node = createLeafNode("value is_positive");
        CellContext context = CellContext.acquire();

        context.update(1, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(100.5, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(0.1, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isPositive_false() {
        LeafNode node = createLeafNode("value is_positive");
        CellContext context = CellContext.acquire();

        context.update(-1, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(0, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(-100.5, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_isZero_true() {
        LeafNode node = createLeafNode("value is_zero");
        CellContext context = CellContext.acquire();

        context.update(0, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(0.0, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(0.00001, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isZero_false() {
        LeafNode node = createLeafNode("value is_zero");
        CellContext context = CellContext.acquire();

        context.update(1, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(-1, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(0.1, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_isNull_true() {
        LeafNode node = createLeafNode("value is_null");
        CellContext context = CellContext.acquire();

        context.update(null, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isNull_false() {
        LeafNode node = createLeafNode("value is_null");
        CellContext context = CellContext.acquire();

        context.update(0, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("test", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_isNotNull_true() {
        LeafNode node = createLeafNode("value is_not_null");
        CellContext context = CellContext.acquire();

        context.update(0, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("test", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isNotNull_false() {
        LeafNode node = createLeafNode("value is_not_null");
        CellContext context = CellContext.acquire();

        context.update(null, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_isEmpty_null() {
        LeafNode node = createLeafNode("value is_empty");
        CellContext context = CellContext.acquire();

        context.update(null, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isEmpty_emptyString() {
        LeafNode node = createLeafNode("value is_empty");
        CellContext context = CellContext.acquire();

        context.update("", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isEmpty_whitespace() {
        LeafNode node = createLeafNode("value is_empty");
        CellContext context = CellContext.acquire();

        context.update("   ", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("\t", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("\n", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isEmpty_false() {
        LeafNode node = createLeafNode("value is_empty");
        CellContext context = CellContext.acquire();

        context.update("test", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(" test ", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(0, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_isNotEmpty_true() {
        LeafNode node = createLeafNode("value is_not_empty");
        CellContext context = CellContext.acquire();

        context.update("test", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(" test ", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(0, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_isNotEmpty_false() {
        LeafNode node = createLeafNode("value is_not_empty");
        CellContext context = CellContext.acquire();

        context.update(null, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("   ", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    private LeafNode createLeafNode(String expression) {
        ExpressionNode node = ExpressionParser.parseToTree(expression);
        assertTrue(node instanceof LeafNode, "Expected LeafNode but got: " + node.getClass().getName());
        return (LeafNode) node;
    }
}
