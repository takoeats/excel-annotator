package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 테이블 짝수 행 스타일
 * - 연한 회색 배경 (zebra stripe)
 * - 검은색 글씨
 * - 왼쪽 정렬
 * - 얇은 테두리
 */
public class TableRowEvenStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(192, 192, 192)
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)
                .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .width(120);
    }
}