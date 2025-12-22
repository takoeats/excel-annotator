package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class ValidationErrorStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(255, 192, 203)
                .font("맑은 고딕", 10, FontStyle.BOLD)
                .fontColor(255, 0, 0)
                .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                .border(BorderStyle.THICK)
                .width(120);
    }
}
