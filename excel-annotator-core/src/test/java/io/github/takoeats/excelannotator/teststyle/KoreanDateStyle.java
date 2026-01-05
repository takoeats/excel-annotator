package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.*;

public class KoreanDateStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(255, 255, 224)
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlign.CENTER, VerticalAlign.CENTER)
                .border(BorderType.THIN)
                .dataFormat("yyyy\"년\" MM\"월\" dd\"일\"")
                .width(130);
    }
}
