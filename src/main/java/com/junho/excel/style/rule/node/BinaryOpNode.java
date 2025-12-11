package com.junho.excel.style.rule.node;

import com.junho.excel.style.rule.CellContext;
import lombok.Getter;

/**
 * 이항 연산자 노드
 * <p>예: {@code value > 0 && value < 100}</p>
 */
@Getter
public class BinaryOpNode extends ExpressionNode {

    private final LogicalOperator operator;
    private final ExpressionNode left;
    private final ExpressionNode right;

    public BinaryOpNode(LogicalOperator operator, ExpressionNode left, ExpressionNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(CellContext context) {
        boolean leftResult = left.evaluate(context);
        boolean rightResult = right.evaluate(context);

        switch (operator) {
            case AND:
                return leftResult && rightResult;
            case OR:
                return leftResult || rightResult;
            case XOR:
                return leftResult ^ rightResult;
            default:
                throw new IllegalStateException("Unsupported binary operator: " + operator);
        }
    }
}
