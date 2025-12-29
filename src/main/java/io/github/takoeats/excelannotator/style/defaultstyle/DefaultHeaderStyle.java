package io.github.takoeats.excelannotator.style.defaultstyle;

import io.github.takoeats.excelannotator.style.*;

/**
 * 기본 헤더 스타일
 */
public class DefaultHeaderStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.font("맑은 고딕", 12, FontStyle.BOLD)
                .alignment(HorizontalAlign.CENTER)
                .backgroundColor(192, 192, 192)
                .border(BorderType.THIN)
                .width(100);
    }
}