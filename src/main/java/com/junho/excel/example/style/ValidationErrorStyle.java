package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 유효성 검사 오류 스타일
 * - 분홍색 배경 (255, 192, 203) PINK
 * - 빨간색 굵은 글씨
 * - 왼쪽 정렬 (상세 오류 메시지용)
 * - 굵은 테두리 (THICK)
 *
 * <h3>📌 사용 용도</h3>
 * <ul>
 *   <li>입력 오류, 검증 실패 <strong>상세 메시지</strong></li>
 *   <li>왼쪽 정렬 → 긴 텍스트 메시지에 적합</li>
 * </ul>
 *
 * <h3>🔗 관련 스타일</h3>
 * <ul>
 *   <li><strong>CriticalAlertStyle</strong>: 동일 배경색, 중앙 정렬, 점선 테두리 (요약 메시지용)</li>
 *   <li><strong>차이점</strong>: ValidationError=상세메시지/왼쪽, CriticalAlert=요약/중앙</li>
 * </ul>
 *
 * @see CriticalAlertStyle
 */
public class ValidationErrorStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 192, 203)
                .font("맑은 고딕", 10, FontStyle.BOLD)
                .fontColor(255, 0, 0)
                .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                .border(BorderStyle.THICK)
                .width(120);
    }
}