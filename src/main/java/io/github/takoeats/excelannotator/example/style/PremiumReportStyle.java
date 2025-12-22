package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 프리미엄 리포트 스타일 - 고급 보고서용
 * - 진한 파란색 배경
 * - 흰색 굵은 글씨
 * - 오른쪽 정렬
 * - 굵은 테두리
 * - 소수점 3자리 포맷
 * - 중간 폰트 (11pt)
 * - 표준 폭 (140px)
 */
public class PremiumReportStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(0, 0, 139)  // DARK_BLUE
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(255, 255, 255)  // WHITE
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER)
                .border(BorderStyle.THICK)
                .numberFormat("#,##0.000")
                .width(140);
    }
}