package io.github.takoeats.excelannotator.annotation;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultColumnStyle;
import io.github.takoeats.excelannotator.style.defaultstyle.DefaultHeaderStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel 시트 정보를 정의하는 어노테이션
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSheet {

    /**
     * 시트명
     *
     * @return 시트명
     */
    String value() default "Sheet1";

    /**
     * 헤더 행 생성 여부
     *
     * @return true이면 헤더 행 생성
     */
    boolean hasHeader() default true;

    /**
     * 시트 순서 (멀티 시트 생성 시 정렬 기준)
     * <p>order가 지정된 시트는 order 값에 따라 정렬되며,
     * order가 지정되지 않은 시트들이 먼저 나오고 그 뒤에 order가 있는 시트들이 정렬됩니다.</p>
     * <p>동일한 order 값을 가진 시트가 있으면 예외가 발생합니다.</p>
     *
     * @return 시트 순서 (기본값: Integer.MIN_VALUE - 미지정 상태)
     */
    int order() default Integer.MIN_VALUE;

    /**
     * 시트 전체의 기본 헤더 스타일
     * <p>개별 컬럼의 {@link ExcelColumn#headerStyle()}이 명시적으로 지정되지 않은 경우 이 스타일이 적용됩니다.</p>
     * <p>우선순위: 1) @ExcelColumn.headerStyle > 2) @ExcelSheet.defaultHeaderStyle > 3) DefaultHeaderStyle</p>
     *
     * @return 기본 헤더 스타일 클래스
     */
    Class<? extends CustomExcelCellStyle> defaultHeaderStyle() default DefaultHeaderStyle.class;

    /**
     * 시트 전체의 기본 컬럼 스타일
     * <p>개별 컬럼의 {@link ExcelColumn#columnStyle()}이 명시적으로 지정되지 않은 경우 이 스타일이 적용됩니다.</p>
     * <p>우선순위: 1) @ExcelColumn.columnStyle > 2) @ExcelSheet.defaultColumnStyle > 3) DefaultColumnStyle (또는 숫자 타입의 경우 DefaultNumberStyle)</p>
     *
     * @return 기본 컬럼 스타일 클래스
     */
    Class<? extends CustomExcelCellStyle> defaultColumnStyle() default DefaultColumnStyle.class;

    /**
     * 모든 필드를 자동으로 엑셀 컬럼으로 처리할지 여부
     * <p>true로 설정하면 @ExcelColumn 어노테이션이 없는 필드도 자동으로 엑셀 컬럼으로 변환됩니다.</p>
     * <p>필드 선언 순서대로 order가 자동 할당되며, 헤더명은 필드명이 사용됩니다.</p>
     * <p>제외하려면 {@link ExcelColumn#exclude()} = true를 명시하세요.</p>
     * <p>이미 @ExcelColumn이 있는 필드는 어노테이션의 설정이 우선 적용됩니다.</p>
     *
     * @return true이면 모든 필드를 자동으로 컬럼으로 처리
     */
    boolean autoColumn() default false;
}