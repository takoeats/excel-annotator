package com.junho.excel.style.defaultstyle;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * 기본 헤더 스타일
 */
public class DefaultHeaderStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.font("맑은 고딕", 12, FontStyle.BOLD)
                .alignment(HorizontalAlignment.CENTER)
                .backgroundColor(192, 192, 192)  // GREY_25_PERCENT -> RGB
                .border(BorderStyle.THIN)
                .width(100);
    }
}