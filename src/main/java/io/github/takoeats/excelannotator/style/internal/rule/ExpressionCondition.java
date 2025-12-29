package io.github.takoeats.excelannotator.style.internal.rule;

import io.github.takoeats.excelannotator.style.internal.rule.node.ExpressionNode;
import lombok.extern.slf4j.Slf4j;

/**
 * 표현식 기반 조건
 * <p>파싱된 표현식 트리를 평가하여 조건 만족 여부를 판단합니다.</p>
 * <p>논리 연산자와 괄호를 포함한 복잡한 조건식을 지원합니다.</p>
 */
@Slf4j
public class ExpressionCondition implements StyleCondition {

    private final ExpressionNode expressionTree;

    /**
     * 표현식 문자열로부터 조건 생성
     *
     * @param expressionString 조건 표현식 (예: "value > 0 && value < 100")
     */
    public ExpressionCondition(String expressionString) {
        this.expressionTree = ExpressionParser.parseToTree(expressionString);
    }

    /**
     * 표현식 트리로부터 조건 생성
     *
     * @param expressionTree 파싱된 표현식 트리
     */
    public ExpressionCondition(ExpressionNode expressionTree) {
        this.expressionTree = expressionTree;
    }

    @Override
    public boolean test(CellContext context) {
        return expressionTree.evaluate(context);
    }
}
