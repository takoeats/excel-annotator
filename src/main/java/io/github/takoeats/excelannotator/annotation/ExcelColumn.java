package io.github.takoeats.excelannotator.annotation;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultColumnStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultHeaderStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel 컬럼 정보를 정의하는 어노테이션
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    /**
     * 엑셀 컬럼 헤더명
     *
     * @return 헤더명
     */
    String header() default "";

    /**
     * 컬럼 순서 (1부터 시작)
     * int 범위 내 사용 (1 ~ Integer.MAX_VALUE)
     *
     * @return 순서
     */
    int order() default Integer.MAX_VALUE;

    /**
     * 컬럼 너비 (기본값: 0 = 스타일 또는 자동 결정)
     * <p>0: 스타일 또는 자동 결정</p>
     * <p>양수: 명시적 너비 지정</p>
     * <p>-1: 자동 너비 (autoWidth)</p>
     *
     * @return 너비
     */
    int width() default 0;

    /**
     * 데이터 포맷 (날짜, 숫자 등)
     *
     * @return 포맷 문자열
     */
    String format() default "";

    /**
     * 컬럼을 엑셀 내보내기에서 제외할지 여부
     *
     * @return true이면 제외
     */
    boolean exclude() default false;

    /**
     * 이 컬럼이 속할 시트명 (Case 1: 단일 DTO에서 컬럼별 시트 분리)
     * <p>미지정 시 클래스 레벨 @ExcelSheet.value() 사용</p>
     *
     * @return 시트명
     */
    String sheetName() default "";

    // ===== 신규 스타일 속성 (선택적) =====

    /**
     * 헤더 스타일 클래스
     *
     * @return 헤더 스타일 클래스
     */
    Class<? extends CustomExcelCellStyle> headerStyle() default DefaultHeaderStyle.class;

    /**
     * 컬럼 스타일 클래스
     *
     * @return 컬럼 스타일 클래스
     */
    Class<? extends CustomExcelCellStyle> columnStyle() default DefaultColumnStyle.class;

    /**
     * 조건부 스타일 규칙
     * <p>조건이 만족되면 지정된 스타일이 적용됩니다.</p>
     * <p>여러 조건이 동시에 만족될 경우 우선순위가 높은 스타일이 적용됩니다.</p>
     * <p>어떤 조건도 만족하지 않으면 columnStyle이 적용됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @ExcelColumn(
     *     header = "금액",
     *     conditionalStyles = {
     *         @ConditionalStyle(
     *             condition = NegativeNumberCondition.class,
     *             style = RedBackgroundStyle.class,
     *             priority = 10
     *         )
     *     }
     * )
     * private BigDecimal amount;
     * }</pre>
     *
     * @return 조건부 스타일 배열
     */
    ConditionalStyle[] conditionalStyles() default {};
}