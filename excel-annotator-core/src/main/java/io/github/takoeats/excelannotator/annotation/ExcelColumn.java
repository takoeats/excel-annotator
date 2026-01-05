package io.github.takoeats.excelannotator.annotation;

import io.github.takoeats.excelannotator.masking.Masking;
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

    /**
     * 데이터 마스킹 전략
     * <p>민감한 개인정보, 금융정보 등을 마스킹 처리하여 보안을 강화합니다.</p>
     * <p>마스킹은 String 타입 필드에만 적용됩니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @ExcelColumn(header = "전화번호", masking = Masking.PHONE)
     * private String phoneNumber;
     *
     * @ExcelColumn(header = "이메일", masking = Masking.EMAIL)
     * private String email;
     *
     * @ExcelColumn(header = "주민번호", masking = Masking.SSN)
     * private String ssn;
     * }</pre>
     *
     * @return 마스킹 전략
     */
    Masking masking() default Masking.NONE;

    /**
     * 병합 헤더명 (2행 헤더 생성 시 최상단 행에 표시될 그룹 헤더)
     * <p>같은 mergeHeader 값을 가진 컬럼들이 하나의 병합된 헤더로 표시됩니다.</p>
     * <p>병합 그룹 내 order는 반드시 연속적이어야 합니다.</p>
     *
     * <h3>사용 예시</h3>
     * <pre>{@code
     * @ExcelColumn(header = "Name", order = 2, mergeHeader = "Customer")
     * private String name;
     *
     * @ExcelColumn(header = "Email", order = 3, mergeHeader = "Customer")
     * private String email;
     *
     * // 결과:
     * // Row 0: [  Customer  ]
     * // Row 1: [Name | Email]
     * }</pre>
     *
     * @return 병합 헤더명
     */
    String mergeHeader() default "";

    /**
     * 병합 헤더 스타일 클래스
     * <p>mergeHeader가 지정된 경우 최상단 행의 병합된 헤더에 적용될 스타일입니다.</p>
     *
     * @return 병합 헤더 스타일 클래스
     */
    Class<? extends CustomExcelCellStyle> mergeHeaderStyle() default DefaultHeaderStyle.class;
}