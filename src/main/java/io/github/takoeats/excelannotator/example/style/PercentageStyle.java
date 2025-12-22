package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 퍼센트 표시 스타일
 * - 연한 파란색 배경
 * - 중앙 정렬
 * - 퍼센트 포맷 (0.00%)
 */
public class PercentageStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(173, 216, 230)  // LIGHT_BLUE
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)  // BLACK
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .numberFormat("0.00%")
                .width(90);
    }
}