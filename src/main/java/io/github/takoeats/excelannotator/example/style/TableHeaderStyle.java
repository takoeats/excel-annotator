package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 테이블 헤더 스타일
 * - 진한 회색 배경
 * - 흰색 굵은 글씨
 * - 중앙 정렬
 * - 굵은 테두리
 */
public class TableHeaderStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(51, 51, 51)  // GREY_80_PERCENT -> RGB
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(255, 255, 255)  // WHITE -> RGB
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THICK)
                .width(120);
    }
}