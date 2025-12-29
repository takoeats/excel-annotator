package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class AttentionStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(255, 160, 122)
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 139)
                .alignment(HorizontalAlign.CENTER, VerticalAlign.CENTER)
                .border(BorderType.MEDIUM)
                .width(110);
    }
}
