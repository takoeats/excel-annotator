package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class SignatureStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(175, 238, 238)
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(ExcelColors.blue())
                .alignment(HorizontalAlign.CENTER, VerticalAlign.CENTER)
                .border(BorderType.DOUBLE)
                .width(130);
    }
}
