package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 진한 보라색 스타일
 * - 진한 보라색 배경 (PLUM)
 * - 흰색 굵은 글씨
 * - 중앙 정렬
 * - 이중 테두리
 */
public class DarkPurpleStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(221, 160, 221)  // PLUM
                .font("맑은 고딕", 12, FontStyle.BOLD)
                .fontColor(255, 255, 255)  // WHITE
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.DOUBLE)
                .width(110);
    }
}