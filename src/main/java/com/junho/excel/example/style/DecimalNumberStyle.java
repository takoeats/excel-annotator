package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 소수점 숫자 스타일
 * - 연한 회색 배경
 * - 검은색 글씨
 * - 오른쪽 정렬
 * - 소수점 포맷 (0.000)
 */
public class DecimalNumberStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(192, 192, 192)  // GREY_25_PERCENT
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)  // BLACK
                .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .numberFormat("0.000")
                .width(100);
    }
}