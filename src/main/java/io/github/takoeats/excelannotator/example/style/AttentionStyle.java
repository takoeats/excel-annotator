package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 주의 집중 스타일
 * - 밝은 주황색 배경
 * - 진한 파란색 굵은 글씨
 * - 중앙 정렬
 */
public class AttentionStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 160, 122)  // LIGHT_ORANGE -> RGB
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 139)  // DARK_BLUE -> RGB
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.MEDIUM)
                .width(110);
    }
}