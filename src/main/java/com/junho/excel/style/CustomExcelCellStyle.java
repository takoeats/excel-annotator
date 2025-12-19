package com.junho.excel.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 커스텀 Excel 셀 스타일 추상 클래스
 * <p>Thread-safe lazy initialization 기반 스타일 설정</p>
 */
public abstract class CustomExcelCellStyle {

    private final AtomicReference<ExcelCellStyleConfigurer> configurerRef = new AtomicReference<>();

    /**
     * 하위 클래스에서 스타일 설정
     */
    protected abstract void configure(ExcelCellStyleConfigurer configurer);

    /**
     * Workbook을 전달받는 apply 메서드
     */
    public void apply(CellStyle cellStyle, Workbook workbook) {
        ExcelCellStyleConfigurer configurer = getOrCreateConfigurer();
        configurer.configure(cellStyle, workbook);
    }

    /**
     * Thread-safe configurer 인스턴스 반환 (CAS 기반)
     */
    private ExcelCellStyleConfigurer getOrCreateConfigurer() {
        ExcelCellStyleConfigurer configurer = configurerRef.get();
        if (configurer == null) {
            ExcelCellStyleConfigurer newConfigurer = new ExcelCellStyleConfigurer();
            configure(newConfigurer);

            if (configurerRef.compareAndSet(null, newConfigurer)) {
                return newConfigurer;
            } else {
                return configurerRef.get();
            }
        }
        return configurer;
    }

    /**
     * 설정된 너비 값 반환
     * <p>우선순위: 어노테이션 > 스타일 > 기본값(100)</p>
     */
    public int getColumnWidth() {
        ExcelCellStyleConfigurer configurer = getOrCreateConfigurer();
        return configurer.getColumnWidth();
    }

    /**
     * 자동 너비 여부 확인
     */
    public boolean isAutoWidth() {
        ExcelCellStyleConfigurer configurer = getOrCreateConfigurer();
        return configurer.isAutoWidth();
    }

    /**
     * 데이터 포맷 반환
     * <p>스타일에서 정의된 포맷 문자열 반환 (예: "#,##0.00", "yyyy-MM-dd")</p>
     *
     * @return 포맷 문자열 (미정의 시 "General")
     */
    public String getDataFormat() {
        ExcelCellStyleConfigurer configurer = getOrCreateConfigurer();
        return configurer.getDataFormat();
    }

}