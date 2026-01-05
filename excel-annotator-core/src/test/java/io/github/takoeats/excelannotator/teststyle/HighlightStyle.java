package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class HighlightStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(255, 255, 0)
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlign.CENTER, VerticalAlign.CENTER)
                .border(BorderType.THIN)
                .width(100);
    }
}
