package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class CriticalAlertStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(255, 192, 203)
                .font("맑은 고딕", 12, FontStyle.BOLD)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlign.CENTER, VerticalAlign.CENTER)
                .border(BorderType.DASHED)
                .width(120);
    }
}
