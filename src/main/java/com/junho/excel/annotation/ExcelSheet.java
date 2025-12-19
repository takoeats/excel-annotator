package com.junho.excel.annotation;

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
}