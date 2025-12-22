package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 노란색 강조 스타일
 * - 밝은 노란색 배경
 * - 검은색 굵은 글씨
 * - 중앙 정렬
 */
public class HighlightStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 255, 0)  // YELLOW -> RGB
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 0)  // BLACK -> RGB
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .width(100);
    }
}