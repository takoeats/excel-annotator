package io.github.takoeats.excelannotator.style;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 조건부 스타일 표현식에서 사용할 수 있는 특수 조건 상수 클래스
 * <p>타입 안전성과 IDE 자동완성을 위해 자주 사용되는 특수 조건들을 상수로 제공합니다.</p>
 *
 * <h3>사용 예시</h3>
 * <pre>{@code
 * @ExcelColumn(
 *     header = "금액",
 *     conditionalStyles = {
 *         @ConditionalStyle(when = Conditions.IS_NEGATIVE, style = RedStyle.class),
 *         @ConditionalStyle(when = Conditions.IS_ZERO, style = GrayStyle.class)
 *     }
 * )
 * private BigDecimal amount;
 *
 * @ExcelColumn(
 *     header = "비고",
 *     conditionalStyles = {
 *         @ConditionalStyle(when = Conditions.IS_EMPTY, style = WarningStyle.class),
 *         @ConditionalStyle(when = Conditions.IS_NOT_NULL, style = HighlightStyle.class)
 *     }
 * )
 * private String note;
 * }</pre>
 *
 * <h3>숫자/문자열 비교 표현식</h3>
 * <p>숫자 비교나 문자열 비교는 직접 문자열로 작성하세요:</p>
 * <pre>{@code
 * // 숫자 비교
 * @ConditionalStyle(when = "value < 100", style = LowStyle.class)
 * @ConditionalStyle(when = "value >= 1000000", style = HighStyle.class)
 * @ConditionalStyle(when = "value between 10 and 100", style = MidStyle.class)
 *
 * // 문자열 비교
 * @ConditionalStyle(when = "value equals '완료'", style = DoneStyle.class)
 * @ConditionalStyle(when = "value contains '진행'", style = InProgressStyle.class)
 * @ConditionalStyle(when = "value starts_with '주문'", style = OrderStyle.class)
 * }</pre>
 *
 * @see io.github.takoeats.excelannotator.annotation.ConditionalStyle
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Conditions {

    /**
     * 값이 음수인지 확인
     * <p>숫자 타입 필드에 사용합니다.</p>
     */
    public static final String IS_NEGATIVE = "value is_negative";

    /**
     * 값이 양수인지 확인
     * <p>숫자 타입 필드에 사용합니다.</p>
     */
    public static final String IS_POSITIVE = "value is_positive";

    /**
     * 값이 0인지 확인
     * <p>숫자 타입 필드에 사용합니다.</p>
     */
    public static final String IS_ZERO = "value is_zero";

    /**
     * 값이 null인지 확인
     * <p>모든 타입 필드에 사용할 수 있습니다.</p>
     */
    public static final String IS_NULL = "value is_null";

    /**
     * 값이 null이 아닌지 확인
     * <p>모든 타입 필드에 사용할 수 있습니다.</p>
     */
    public static final String IS_NOT_NULL = "value is_not_null";

    /**
     * 값이 비어있는지 확인 (null 또는 빈 문자열)
     * <p>문자열 타입 필드에 사용합니다. null이거나 빈 문자열("")인 경우 true를 반환합니다.</p>
     */
    public static final String IS_EMPTY = "value is_empty";

    /**
     * 값이 비어있지 않은지 확인 (null이 아니고 빈 문자열도 아님)
     * <p>문자열 타입 필드에 사용합니다. null이 아니고 빈 문자열("")도 아닌 경우 true를 반환합니다.</p>
     */
    public static final String IS_NOT_EMPTY = "value is_not_empty";
}
