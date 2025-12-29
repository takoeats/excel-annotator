package io.github.takoeats.excelannotator.internal.metadata;

/**
 * 시트 관련 메타데이터 인터페이스
 * Sheet 생성 및 구조 정보를 제공
 */
public interface SheetMetadata {

    /**
     * 시트 이름 조회
     * @return 시트 이름
     */
    String getSheetName();

    /**
     * 헤더 존재 여부 확인
     * @return 헤더가 있으면 true, 없으면 false
     */
    boolean hasHeader();

    /**
     * 헤더 행 개수 조회
     * 병합 헤더가 있으면 2, 없으면 1
     * @return 헤더 행 개수
     */
    int getHeaderRowCount();

    /**
     * 병합 헤더 존재 여부 확인
     * @return 병합 헤더가 하나라도 있으면 true, 없으면 false
     */
    boolean hasAnyMergeHeader();
}
