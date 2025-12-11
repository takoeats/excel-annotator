package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 시간만 표시 스타일
 * - 연한 파란색 배경
 * - 중앙 정렬
 * - 시간 포맷 (HH:mm:ss)
 */
public class TimeOnlyStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(173, 216, 230)  // LIGHT_BLUE
                  .font("맑은 고딕", 10, FontStyle.NORMAL)
                  .fontColor(0, 0, 0)  // BLACK
                  .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                  .border(BorderStyle.THIN)
                  .numberFormat("HH:mm:ss")
                  .width(90);
    }
}