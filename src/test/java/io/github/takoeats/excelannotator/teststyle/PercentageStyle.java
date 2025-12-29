package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class PercentageStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(173, 216, 230)
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlign.CENTER, VerticalAlign.CENTER)
                .border(BorderType.THIN)
                .dataFormat("0.00%")
                .width(90);
    }
}
