package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 심각한 경고 스타일
 * - 분홍색 배경 (255, 192, 203) PINK
 * - 검은색 굵은 글씨 (12pt, 크게 표시)
 * - 중앙 정렬 (요약 메시지용)
 * - 점선 테두리 (DASHED, 주의 환기)
 *
 * <h3>📌 사용 용도</h3>
 * <ul>
 *   <li>심각한 오류, 경고 <strong>요약 메시지</strong></li>
 *   <li>중앙 정렬 → 짧은 텍스트 강조에 적합</li>
 * </ul>
 *
 * <h3>🔗 관련 스타일</h3>
 * <ul>
 *   <li><strong>ValidationErrorStyle</strong>: 동일 배경색, 왼쪽 정렬, 굵은 테두리 (상세 메시지용)</li>
 *   <li><strong>차이점</strong>: CriticalAlert=요약/중앙/큰글씨, ValidationError=상세/왼쪽/작은글씨</li>
 * </ul>
 *
 * @see ValidationErrorStyle
 */
public class CriticalAlertStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 192, 203)
                .font("맑은 고딕", 12, FontStyle.BOLD)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.DASHED)
                .width(120);
    }
}