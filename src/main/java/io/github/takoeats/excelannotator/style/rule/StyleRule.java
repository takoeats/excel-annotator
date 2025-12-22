package io.github.takoeats.excelannotator.style.rule;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import lombok.Builder;
import lombok.Getter;

/**
 * 조건과 스타일을 묶는 규칙 클래스
 * <p>조건이 만족되면 지정된 스타일이 적용됩니다.</p>
 */
@Getter
@Builder
public class StyleRule implements Comparable<StyleRule> {

    private final StyleCondition condition;
    private final Class<? extends CustomExcelCellStyle> styleClass;
    private final int priority;

    /**
     * 규칙 평가
     *
     * @param context 셀 컨텍스트
     * @return 조건 만족 시 true
     */
    public boolean evaluate(CellContext context) {
        if (condition == null || context == null) {
            return false;
        }
        return condition.test(context);
    }

    @Override
    public int compareTo(StyleRule other) {
        return Integer.compare(other.priority, this.priority);
    }

    public static class StyleRuleBuilder {
        private int priority = 0;
    }
}
