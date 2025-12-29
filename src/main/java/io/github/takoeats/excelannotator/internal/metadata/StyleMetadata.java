package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.rule.StyleRule;

import java.util.List;

/**
 * 셀 스타일 관련 메타데이터 인터페이스
 * 컬럼 스타일, 조건부 스타일, 포맷 정보를 제공
 */
public interface StyleMetadata {

    /**
     * 특정 인덱스의 컬럼 스타일 조회
     * @param index 컬럼 인덱스
     * @return 컬럼 스타일, 범위 밖이거나 없으면 null
     */
    CustomExcelCellStyle getColumnStyleAt(int index);

    /**
     * 특정 인덱스의 조건부 스타일 규칙 목록 조회
     * @param index 컬럼 인덱스
     * @return 조건부 스타일 규칙 리스트, 범위 밖이거나 없으면 빈 리스트
     */
    List<StyleRule> getConditionalStyleRulesAt(int index);

    /**
     * 특정 인덱스의 셀 포맷 조회
     * @param index 컬럼 인덱스
     * @return 셀 포맷 문자열, 범위 밖이거나 없으면 null
     */
    String getFormatAt(int index);
}
