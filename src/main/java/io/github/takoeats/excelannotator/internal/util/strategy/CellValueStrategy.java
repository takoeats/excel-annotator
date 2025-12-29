package io.github.takoeats.excelannotator.internal.util.strategy;

import org.apache.poi.ss.usermodel.Cell;

/**
 * 셀 값 타입별 변환 전략 인터페이스
 */
public interface CellValueStrategy {

    /**
     * 이 전략이 주어진 값을 지원하는지 확인
     *
     * @param value 변환할 값
     * @return 지원 여부
     */
    boolean supports(Object value);

    /**
     * 값을 셀에 설정
     *
     * @param cell  POI Cell 객체
     * @param value 설정할 값
     */
    void apply(Cell cell, Object value);
}
