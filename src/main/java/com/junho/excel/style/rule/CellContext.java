package com.junho.excel.style.rule;

import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import lombok.Getter;

/**
 * 셀의 컨텍스트 정보를 담는 클래스
 * <p>조건부 스타일 평가 시 사용되는 정보를 제공합니다.</p>
 * <p>메모리 최적화를 위해 ThreadLocal 기반 재사용 가능한 Mutable 객체로 설계되었습니다.</p>
 *
 * <h3>사용 예시</h3>
 * <pre>{@code
 * try (CellContext context = CellContext.acquire()) {
 *     context.update(cellValue, rowObject, columnIndex, rowIndex, fieldName);
 *     // 조건부 스타일 평가
 * }
 * }</pre>
 */
@Getter
public final class CellContext implements AutoCloseable {

    private static final ThreadLocal<CellContext> THREAD_LOCAL = ThreadLocal.withInitial(CellContext::new);

    private Object cellValue;
    private Object rowObject;
    private int columnIndex;
    private int rowIndex;
    private String fieldName;

    private CellContext() {
    }

    public static CellContext acquire() {
        return THREAD_LOCAL.get();
    }

    public CellContext update(Object cellValue, Object rowObject, int columnIndex, int rowIndex, String fieldName) {
        this.cellValue = cellValue;
        this.rowObject = rowObject;
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.fieldName = fieldName;
        return this;
    }

    @Override
    public void close() {
        this.cellValue = null;
        this.rowObject = null;
        this.fieldName = null;
    }

  /**
     * 셀 값을 특정 타입으로 안전하게 변환
     *
     * @param clazz 변환할 타입
     * @param <T>   타입 파라미터
     * @return 변환된 값, 실패 시 null
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueAs(Class<T> clazz) {
        if (cellValue == null) {
            return null;
        }

        if (clazz.isInstance(cellValue)) {
            return (T) cellValue;
        }

        return null;
    }

    /**
     * 행 객체에서 특정 필드 값을 가져옴
     *
     * @param fieldName 필드명
     * @return 필드 값, 실패 시 null
     */
    public Object getFieldValue(String fieldName) {
        if (rowObject == null || fieldName == null) {
            return null;
        }

        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            return rowObject.getClass().getMethod(getterName).invoke(rowObject);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e) {
            throw new ExcelExporterException(
                    ErrorCode.FIELD_ACCESS_FAILED,
                    "필드 값 접근 실패: " + fieldName,
                    e
            );
        }
    }

}
