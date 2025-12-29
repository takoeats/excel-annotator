package io.github.takoeats.excelannotator.internal.metadata;

import io.github.takoeats.excelannotator.masking.Masking;

import java.util.List;
import java.util.function.Function;

/**
 * 데이터 추출 관련 메타데이터 인터페이스
 * 필드값 추출 및 마스킹 정보를 제공
 *
 * @param <T> 데이터 타입
 */
public interface DataExtractionMetadata<T> {

    /**
     * 필드값 추출 함수 목록 조회
     *
     * @return 추출 함수 리스트
     */
    List<Function<T, Object>> getExtractors();

    /**
     * 특정 인덱스의 마스킹 정보 조회
     *
     * @param index 컬럼 인덱스
     * @return 마스킹 정보, 범위 밖이거나 없으면 Masking.NONE
     */
    Masking getMaskingAt(int index);
}
