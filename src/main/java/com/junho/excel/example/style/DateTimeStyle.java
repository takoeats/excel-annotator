package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 날짜시간 표시 스타일
 * - 연한 회색 배경
 * - 중앙 정렬
 * - 날짜시간 포맷 (yyyy-MM-dd HH:mm)
 */
public class DateTimeStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(192, 192, 192)  // GREY_25_PERCENT
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)  // BLACK
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .numberFormat("yyyy-MM-dd HH:mm")
                .width(140);
    }
}