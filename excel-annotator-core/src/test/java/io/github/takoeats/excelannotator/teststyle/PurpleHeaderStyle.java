package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class PurpleHeaderStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(230, 230, 250)
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(255, 255, 255)
                .alignment(HorizontalAlign.CENTER, VerticalAlign.CENTER)
                .border(BorderType.THICK)
                .width(120);
    }
}
