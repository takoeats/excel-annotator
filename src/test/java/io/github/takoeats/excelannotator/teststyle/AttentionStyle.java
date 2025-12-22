package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class AttentionStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(255, 160, 122)
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 139)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.MEDIUM)
                .width(110);
    }
}
