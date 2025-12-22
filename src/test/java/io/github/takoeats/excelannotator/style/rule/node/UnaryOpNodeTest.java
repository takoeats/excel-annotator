package io.github.takoeats.excelannotator.style.rule.node;

import io.github.takoeats.excelannotator.style.rule.CellContext;
import io.github.takoeats.excelannotator.style.rule.ExpressionParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnaryOpNodeTest {

    @AfterEach
    void cleanup() {
        CellContext context = CellContext.acquire();
        context.close();
    }

    @Test
    void evaluate_not_trueOperand_returnsFalse() {
        ExpressionNode node = ExpressionParser.parseToTree("!(value > 50)");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_not_falseOperand_returnsTrue() {
        ExpressionNode node = ExpressionParser.parseToTree("!(value > 50)");
        CellContext context = CellContext.acquire();

        context.update(30, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_doubleNot_returnsOriginal() {
        ExpressionNode node = ExpressionParser.parseToTree("!!(value > 50)");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(30, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_tripleNot_returnsNegated() {
        ExpressionNode node = ExpressionParser.parseToTree("!!!(value > 50)");
        CellContext context = CellContext.acquire();

        context.update(100, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(30, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_notWithComplexExpression_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree("!(value > 0 && value < 100)");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_notWithAnd_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree("!(value < 0) && value < 100");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_notWithOr_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree("!(value < 0) || !(value > 100)");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_notWithParentheses_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree("!((value < 0) || (value > 100))");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertFalse(node.evaluate(context));
    }

    @Test
    void evaluate_notWithStringCondition_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree("!(value equals '\uc644\ub8cc')");
        CellContext context = CellContext.acquire();

        context.update("\uc644\ub8cc", null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("\uc9c4\ud589\uc911", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_notWithNull_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree("!(value is_null)");
        CellContext context = CellContext.acquire();

        context.update(null, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update("test", null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_notWithBetween_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree("!(value between 10 and 100)");
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(5, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(150, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_complexNestedNot_evaluatesCorrectly() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "!((value > 0 && value < 50) || (value > 100 && value < 150))"
        );
        CellContext context = CellContext.acquire();

        context.update(25, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(125, null, 0, 0, "field");
        assertFalse(node.evaluate(context));

        context.update(75, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertTrue(node.evaluate(context));

        context.update(200, null, 0, 0, "field");
        assertTrue(node.evaluate(context));
    }

    @Test
    void evaluate_notWithSpecialConditions_evaluatesCorrectly() {
        ExpressionNode positiveNode = ExpressionParser.parseToTree("!(value is_positive)");
        ExpressionNode negativeNode = ExpressionParser.parseToTree("!(value is_negative)");
        ExpressionNode zeroNode = ExpressionParser.parseToTree("!(value is_zero)");

        CellContext context = CellContext.acquire();

        context.update(10, null, 0, 0, "field");
        assertFalse(positiveNode.evaluate(context));
        assertTrue(negativeNode.evaluate(context));
        assertTrue(zeroNode.evaluate(context));

        context.update(-10, null, 0, 0, "field");
        assertTrue(positiveNode.evaluate(context));
        assertFalse(negativeNode.evaluate(context));
        assertTrue(zeroNode.evaluate(context));

        context.update(0, null, 0, 0, "field");
        assertTrue(positiveNode.evaluate(context));
        assertTrue(negativeNode.evaluate(context));
        assertFalse(zeroNode.evaluate(context));
    }

    @Test
    void evaluate_unsupportedAndOperator_throwsIllegalStateException() {
        ExpressionNode operand = ExpressionParser.parseToTree("value > 0");
        UnaryOpNode node = new UnaryOpNode(ExpressionNode.LogicalOperator.AND, operand);
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> node.evaluate(context)
        );
        assertEquals("Unsupported unary operator: AND", exception.getMessage());
    }

    @Test
    void evaluate_unsupportedOrOperator_throwsIllegalStateException() {
        ExpressionNode operand = ExpressionParser.parseToTree("value > 0");
        UnaryOpNode node = new UnaryOpNode(ExpressionNode.LogicalOperator.OR, operand);
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> node.evaluate(context)
        );
        assertEquals("Unsupported unary operator: OR", exception.getMessage());
    }

    @Test
    void evaluate_unsupportedXorOperator_throwsIllegalStateException() {
        ExpressionNode operand = ExpressionParser.parseToTree("value > 0");
        UnaryOpNode node = new UnaryOpNode(ExpressionNode.LogicalOperator.XOR, operand);
        CellContext context = CellContext.acquire();

        context.update(50, null, 0, 0, "field");
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> node.evaluate(context)
        );
        assertEquals("Unsupported unary operator: XOR", exception.getMessage());
    }
}
