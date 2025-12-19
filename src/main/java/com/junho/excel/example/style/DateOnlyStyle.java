package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 날짜만 표시 스타일
 * - 연한 오렌지 배경
 * - 중앙 정렬
 * - 날짜 포맷 (yyyy-MM-dd)
 */
public class DateOnlyStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 218, 185)  // LIGHT_ORANGE
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)  // BLACK
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .numberFormat("yyyy-MM-dd")
                .width(110);
    }
}