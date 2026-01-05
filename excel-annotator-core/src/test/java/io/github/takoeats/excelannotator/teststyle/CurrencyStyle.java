package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class CurrencyStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(144, 238, 144)
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlign.RIGHT, VerticalAlign.CENTER)
                .border(BorderType.THIN)
                .dataFormat("₩#,##0")
                .width(120);
    }
}
