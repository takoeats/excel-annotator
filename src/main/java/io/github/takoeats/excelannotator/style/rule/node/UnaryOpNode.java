package io.github.takoeats.excelannotator.style.rule.node;

import io.github.takoeats.excelannotator.style.rule.CellContext;
import lombok.Getter;

/**
 * 단항 연산자 노드
 * <p>예: {@code !(value < 0)}</p>
 */
@Getter
public class UnaryOpNode extends ExpressionNode {

    private final LogicalOperator operator;
    private final ExpressionNode operand;

    public UnaryOpNode(LogicalOperator operator, ExpressionNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public boolean evaluate(CellContext context) {
        boolean operandResult = operand.evaluate(context);

        if (operator == LogicalOperator.NOT) {
            return !operandResult;
        }
        throw new IllegalStateException("Unsupported unary operator: " + operator);
    }
}
