package io.github.takoeats.excelannotator.teststyle;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class TableRowEvenStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer
                .backgroundColor(192, 192, 192)
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .width(120);
    }
}
