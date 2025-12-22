package io.github.takoeats.excelannotator.style.rule.node;

import io.github.takoeats.excelannotator.style.rule.CellContext;
import io.github.takoeats.excelannotator.style.rule.ExpressionParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryOpNodeTest {

    @AfterEach
    void cleanup() {
        CellContext context = CellContext.acquire();
        context.close();
    }

    @Test
    void evaluate_and_bothTrue_returnsTrue() {
        ExpressionNode node = ExpressionParser.parseToTree("value > 0 && value < 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_and_leftFalse_returnsFalse() {
        ExpressionNode node = ExpressionParser.parseToTree("value > 0 && value < 100");
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_and_rightFalse_returnsFalse() {
        ExpressionNode node = ExpressionParser.parseToTree("value > 0 && value < 100");
        CellContext context = CellContext.acquire();

        context.update(150, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_and_bothFalse_returnsFalse() {
        ExpressionNode node = ExpressionParser.parseToTree("value > 100 && value < 0");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_or_bothTrue_returnsTrue() {
        ExpressionNode node = ExpressionParser.parseToTree("value < 0 || value > 100");
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_or_leftTrue_returnsTrue() {
        ExpressionNode node = ExpressionParser.parseToTree("value < 0 || value > 100");
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_or_rightTrue_returnsTrue() {
        ExpressionNode node = ExpressionParser.parseToTree("value < 0 || value > 100");
        CellContext context = CellContext.acquire();

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_or_bothFalse_returnsFalse() {
        ExpressionNode node = ExpressionParser.parseToTree("value < 0 || value > 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_xor_bothTrue_returnsFalse() {
        ExpressionNode node = ExpressionParser.parseToTree("value > 0 ^ value < 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_xor_leftTrue_returnsTrue() {
        ExpressionNode node = ExpressionParser.parseToTree("value < 0 ^ value > 100");
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_xor_rightTrue_returnsTrue() {
        ExpressionNode node = ExpressionParser.parseToTree("value < 0 ^ value > 100");
        CellContext context = CellContext.acquire();

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_xor_bothFalse_returnsFalse() {
        ExpressionNode node = ExpressionParser.parseToTree("value < 0 ^ value > 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_complexNested_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "(value > 0 && value < 50) || (value > 100 && value < 150)"
        );
        CellContext context = CellContext.acquire();

        context.update(25, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(125, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(75, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(200, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_multipleAnd_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "value > 0 && value < 100 && value != 50"
        );
        CellContext context = CellContext.acquire();

        context.update(30, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_multipleOr_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "value < 0 || value > 100 || value == 50"
        );
        CellContext context = CellContext.acquire();

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(30, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_mixedOperators_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "(value > 0 && value < 50) || value > 100"
        );
        CellContext context = CellContext.acquire();

        context.update(25, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(75, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_stringConditionsWithAnd_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "value contains '\uc9c4\ud589' && value is_not_empty"
        );
        CellContext context = CellContext.acquire();

        context.update("\uc9c4\ud589\uc911", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("\uc644\ub8cc", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_stringConditionsWithOr_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "value equals '\uc644\ub8cc' || value equals '\uc9c4\ud589\uc911'"
        );
        CellContext context = CellContext.acquire();

        context.update("\uc644\ub8cc", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("\uc9c4\ud589\uc911", null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("\ub300\uae30", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_nullValueWithAnd_handlesSafely() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "value is_null && value is_empty"
        );
        CellContext context = CellContext.acquire();

        context.update(null, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update("test", null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_betweenWithAnd_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "value between 10 and 100 && value != 50"
        );
        CellContext context = CellContext.acquire();

        context.update(30, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(5, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_unsupportedOperator_throwsIllegalStateException() {
        ExpressionNode left = ExpressionParser.parseToTree("value > 0");
        ExpressionNode right = ExpressionParser.parseToTree("value < 100");
        BinaryOpNode node = new BinaryOpNode(ExpressionNode.LogicalOperator.NOT, left, right);
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> node.evaluate(context)
        );
        assertEquals("Unsupported binary operator: NOT", exception.getMessage());
    }
}
