package io.github.takoeats.excelannotator.internal.metadata;

import java.util.List;
import java.util.Set;

/**
 * 컬럼 기본 정보 메타데이터 인터페이스
 * 컬럼 개수, 헤더, 폭 등의 정보를 제공
 */
public interface ColumnMetadata {

    /**
     * 헤더 목록 조회
     *
     * @return 헤더 문자열 리스트
     */
    List<String> getHeaders();

    /**
     * 컬럼 폭 목록 조회
     *
     * @return 컬럼 폭 리스트
     */
    List<Integer> getColumnWidths();

    /**
     * 컬럼 개수 조회
     *
     * @return 컬럼 개수
     */
    int getColumnCount();

    /**
     * 최소 order 값 조회
     *
     * @return 최소 order 값, 컬럼이 없으면 Integer.MAX_VALUE
     */
    int getMinOrder();

    /**
     * 모든 order 값 조회
     *
     * @return order 값 집합, 컬럼이 없으면 빈 Set
     */
    Set<Integer> getAllOrders();

    /**
     * 특정 인덱스의 필드명 조회
     *
     * @param index 컬럼 인덱스
     * @return 필드명, 범위 밖이면 null
     */
    String getFieldNameAt(int index);
}
