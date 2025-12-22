package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 통화 표시 스타일
 * - 연한 녹색 배경
 * - 오른쪽 정렬
 * - 통화 포맷 (₩#,##0)
 * - 굵은 글씨
 */
public class CurrencyStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(144, 238, 144)  // LIGHT_GREEN
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 0)  // BLACK
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .numberFormat("₩#,##0")
                .width(120);
    }
}