package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 임원 요약 스타일 - 최고급 임원진 보고서용
 * - 황금색 배경 (고급스러움)
 * - 검은색 굵은 글씨
 * - 중앙 정렬
 * - 굵은 테두리
 * - 퍼센트 포맷
 * - 큰 폰트 (13pt)
 * - 넓은 폭 (160px)
 */
public class ExecutiveSummaryStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 215, 0)  // GOLD
                .font("맑은 고딕", 13, FontStyle.BOLD)
                .fontColor(0, 0, 0)  // BLACK
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THICK)
                .numberFormat("0.00%")
                .width(160);
    }
}