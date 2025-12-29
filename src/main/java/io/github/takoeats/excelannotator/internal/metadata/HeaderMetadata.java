package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;

/**
 * 헤더 관련 메타데이터 인터페이스
 * 헤더 스타일 및 병합 헤더 정보를 제공
 */
public interface HeaderMetadata {

    /**
     * 특정 인덱스의 헤더 스타일 조회
     *
     * @param index 컬럼 인덱스
     * @return 헤더 스타일, 범위 밖이거나 없으면 null
     */
    CustomExcelCellStyle getHeaderStyleAt(int index);

    /**
     * 특정 인덱스의 병합 헤더 텍스트 조회
     *
     * @param index 컬럼 인덱스
     * @return 병합 헤더 텍스트, 범위 밖이거나 없으면 빈 문자열
     */
    String getMergeHeaderAt(int index);

    /**
     * 특정 인덱스의 병합 헤더 스타일 조회
     *
     * @param index 컬럼 인덱스
     * @return 병합 헤더 스타일, 범위 밖이거나 없으면 null
     */
    CustomExcelCellStyle getMergeHeaderStyleAt(int index);
}
