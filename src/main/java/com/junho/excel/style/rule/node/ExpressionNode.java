package com.junho.excel.style.rule.node;

import com.junho.excel.style.rule.CellContext;

/**
 * 표현식 트리의 노드
 * <p>조건 표현식을 트리 구조로 표현하기 위한 추상 클래스입니다.</p>
 */
public abstract class ExpressionNode {

    /**
     * 노드를 평가합니다.
     * @param context 셀 컨텍스트
     * @return 평가 결과
     */
    public abstract boolean evaluate(CellContext context);

    /**
     * 논리 연산자
     */
    public enum LogicalOperator {
        AND,    // &&
        OR,     // ||
        XOR,    // ^
        NOT     // !
    }
}
