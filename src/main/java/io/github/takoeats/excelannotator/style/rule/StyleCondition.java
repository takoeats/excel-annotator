package io.github.takoeats.excelannotator.style.rule;

/**
 * 스타일 적용 조건을 평가하는 함수형 인터페이스
 * <p>CellContext를 받아 조건 만족 여부를 반환합니다.</p>
 */
@FunctionalInterface
public interface StyleCondition {

    /**
     * 조건 평가
     *
     * @param context 셀 컨텍스트
     * @return 조건 만족 시 true
     */
    boolean test(CellContext context);

    /**
     * AND 조건 결합
     *
     * @param other 다른 조건
     * @return 결합된 조건
     */
    default StyleCondition and(StyleCondition other) {
        return context -> this.test(context) && other.test(context);
    }

    /**
     * OR 조건 결합
     *
     * @param other 다른 조건
     * @return 결합된 조건
     */
    default StyleCondition or(StyleCondition other) {
        return context -> this.test(context) || other.test(context);
    }

    /**
     * NOT 조건
     *
     * @return 반전된 조건
     */
    default StyleCondition negate() {
        return context -> !this.test(context);
    }
}
