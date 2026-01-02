package io.github.takoeats.excelannotator.annotation;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultColumnStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 조건부 스타일 적용을 위한 어노테이션
 * <p>표현식 조건이 만족되면 지정된 스타일이 적용됩니다.</p>
 *
 * <h3>사용 예시</h3>
 * <pre>{@code
 * @ExcelColumn(
 *     header = "금액",
 *     conditionalStyles = {
 *         @ConditionalStyle(
 *             when = "value < 0",
 *             style = NegativeRedStyle.class,
 *             priority = 20
 *         ),
 *         @ConditionalStyle(
 *             when = "value > 1000000",
 *             style = BoldHighlightStyle.class,
 *             priority = 10
 *         ),
 *         @ConditionalStyle(
 *             when = Conditions.IS_POSITIVE,
 *             style = PositiveBlueStyle.class,
 *             priority = 5
 *         )
 *     }
 * )
 * private BigDecimal amount;
 *
 * @ExcelColumn(
 *     header = "상태",
 *     conditionalStyles = {
 *         @ConditionalStyle(when = "value equals '완료'", style = GreenStyle.class),
 *         @ConditionalStyle(when = "value contains '진행'", style = YellowStyle.class)
 *     }
 * )
 * private String status;
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
 * <h3>지원하는 표현식</h3>
 * <ul>
 *     <li>숫자 비교: {@code value < 100}, {@code value >= 0}, {@code value == 50}, {@code value between 10 and 100}</li>
 *     <li>문자열: {@code value equals '완료'}, {@code value contains '진행'}, {@code value starts_with '주문'}</li>
 *     <li>특수 조건: {@link io.github.takoeats.excelannotator.style.Conditions Conditions} 클래스의 상수 사용 (타입 안전)</li>
 * </ul>
 *
 * @see io.github.takoeats.excelannotator.style.Conditions
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalStyle {

    /**
     * 조건 표현식
     * <p>조건이 만족되면 스타일이 적용됩니다.</p>
     *
     * <h4>숫자 비교</h4>
     * <ul>
     *     <li>{@code value < 100} - 100보다 작음</li>
     *     <li>{@code value <= 100} - 100 이하</li>
     *     <li>{@code value > 100} - 100보다 큼</li>
     *     <li>{@code value >= 100} - 100 이상</li>
     *     <li>{@code value == 100} - 100과 같음</li>
     *     <li>{@code value != 100} - 100과 다름</li>
     *     <li>{@code value between 10 and 100} - 10 이상 100 이하</li>
     * </ul>
     *
     * <h4>문자열 비교</h4>
     * <ul>
     *     <li>{@code value equals '완료'} - '완료'와 정확히 일치 (대소문자 구분)</li>
     *     <li>{@code value equals_ignore_case 'complete'} - 대소문자 무시하고 일치</li>
     *     <li>{@code value contains '진행'} - '진행' 포함</li>
     *     <li>{@code value starts_with '주문'} - '주문'으로 시작</li>
     *     <li>{@code value ends_with '완료'} - '완료'로 끝남</li>
     * </ul>
     *
     * <h4>특수 조건 (타입 안전 사용 권장)</h4>
     * <ul>
     *     <li>{@link io.github.takoeats.excelannotator.style.Conditions#IS_NEGATIVE Conditions.IS_NEGATIVE} - 음수</li>
     *     <li>{@link io.github.takoeats.excelannotator.style.Conditions#IS_POSITIVE Conditions.IS_POSITIVE} - 양수</li>
     *     <li>{@link io.github.takoeats.excelannotator.style.Conditions#IS_ZERO Conditions.IS_ZERO} - 0</li>
     *     <li>{@link io.github.takoeats.excelannotator.style.Conditions#IS_NULL Conditions.IS_NULL} - null</li>
     *     <li>{@link io.github.takoeats.excelannotator.style.Conditions#IS_NOT_NULL Conditions.IS_NOT_NULL} - null이 아님</li>
     *     <li>{@link io.github.takoeats.excelannotator.style.Conditions#IS_EMPTY Conditions.IS_EMPTY} - 빈 문자열 또는 null</li>
     *     <li>{@link io.github.takoeats.excelannotator.style.Conditions#IS_NOT_EMPTY Conditions.IS_NOT_EMPTY} - 값이 있음</li>
     * </ul>
     *
     * @return 조건 표현식
     * @see io.github.takoeats.excelannotator.style.Conditions
     */
    String when();

    /**
     * 조건 만족 시 적용할 스타일 클래스
     *
     * @return 스타일 클래스
     */
    Class<? extends CustomExcelCellStyle> style() default DefaultColumnStyle.class;

    /**
     * 우선순위 (높을수록 먼저 평가됨)
     * <p>여러 조건이 동시에 만족될 때 우선순위가 높은 스타일이 적용됩니다.</p>
     *
     * @return 우선순위 (기본값: 0)
     */
    int priority() default 0;
}
