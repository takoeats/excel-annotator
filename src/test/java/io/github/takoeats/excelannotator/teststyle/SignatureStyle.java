package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.ExcelColors;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class SignatureStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(175, 238, 238)
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(ExcelColors.blue())
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.DOUBLE)
                .width(130);
    }
}
