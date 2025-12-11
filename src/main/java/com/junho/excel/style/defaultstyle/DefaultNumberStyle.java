package com.junho.excel.style.defaultstyle;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * 기본 숫자 스타일
 */
public class DefaultNumberStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.font("맑은 고딕", 10)
                  .alignment(HorizontalAlignment.RIGHT)
                  .numberFormat("#,##0_ ")
                  .border(BorderStyle.THIN)
                  .width(100);
    }
}