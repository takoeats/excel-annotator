package com.junho.excel.style.rule.node;

import com.junho.excel.style.rule.CellContext;
import com.junho.excel.style.rule.ConditionEvaluator;
import com.junho.excel.style.rule.ExpressionParser;
import lombok.Getter;

/**
 * 단일 조건을 나타내는 리프 노드
 * <p>예: {@code value < 0}, {@code value equals '완료'}</p>
 */
@Getter
public class LeafNode extends ExpressionNode {

    private final ExpressionParser.ParsedExpression expression;

    public LeafNode(ExpressionParser.ParsedExpression expression) {
        this.expression = expression;
    }

    @Override
    public boolean evaluate(CellContext context) {
        Object value = context.getCellValue();

        switch (expression.getType()) {
            // 숫자 비교
            case LESS_THAN:
                return ConditionEvaluator.NumberComparator.lessThan(value, expression.getNumberValue());
            case LESS_THAN_OR_EQUAL:
                return ConditionEvaluator.NumberComparator.lessThanOrEqual(value, expression.getNumberValue());
            case GREATER_THAN:
                return ConditionEvaluator.NumberComparator.greaterThan(value, expression.getNumberValue());
            case GREATER_THAN_OR_EQUAL:
                return ConditionEvaluator.NumberComparator.greaterThanOrEqual(value, expression.getNumberValue());
            case EQUALS:
                return ConditionEvaluator.NumberComparator.equals(value, expression.getNumberValue());
            case NOT_EQUALS:
                return ConditionEvaluator.NumberComparator.notEquals(value, expression.getNumberValue());
            case BETWEEN:
                return ConditionEvaluator.NumberComparator.between(value,
                        expression.getNumberValue(), expression.getNumberValue2());

            // 문자열 비교
            case STRING_EQUALS:
                return ConditionEvaluator.StringComparator.equals(value, expression.getStringValue());
            case STRING_EQUALS_IGNORE_CASE:
                return ConditionEvaluator.StringComparator.equalsIgnoreCase(value, expression.getStringValue());
            case STRING_CONTAINS:
                return ConditionEvaluator.StringComparator.contains(value, expression.getStringValue());
            case STRING_STARTS_WITH:
                return ConditionEvaluator.StringComparator.startsWith(value, expression.getStringValue());
            case STRING_ENDS_WITH:
                return ConditionEvaluator.StringComparator.endsWith(value, expression.getStringValue());

            // 특수 조건
            case IS_NEGATIVE:
                return ConditionEvaluator.NumberComparator.isNegative(value);
            case IS_POSITIVE:
                return ConditionEvaluator.NumberComparator.isPositive(value);
            case IS_ZERO:
                return ConditionEvaluator.NumberComparator.isZero(value);
            case IS_NULL:
                return value == null;
            case IS_NOT_NULL:
                return value != null;
            case IS_EMPTY:
                return ConditionEvaluator.StringComparator.isEmpty(value);
            case IS_NOT_EMPTY:
                return ConditionEvaluator.StringComparator.isNotEmpty(value);

            default:
                return false;
        }
    }
}
