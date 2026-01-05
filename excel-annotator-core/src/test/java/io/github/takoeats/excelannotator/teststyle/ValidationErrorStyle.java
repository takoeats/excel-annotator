package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class ValidationErrorStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(255, 192, 203)
                .font("맑은 고딕", 10, FontStyle.BOLD)
                .fontColor(255, 0, 0)
                .alignment(HorizontalAlign.LEFT, VerticalAlign.CENTER)
                .border(BorderType.THICK)
                .width(120);
    }
}
