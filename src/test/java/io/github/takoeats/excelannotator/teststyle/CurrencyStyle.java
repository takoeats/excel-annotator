package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class CurrencyStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(144, 238, 144)
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .dataFormat("₩#,##0")
                .width(120);
    }
}
