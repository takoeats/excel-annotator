package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 읽기 전용 필드 스타일
 * - 연한 회색 배경 (수정 불가 표시)
 * - 진한 회색 글씨
 * - 왼쪽 정렬
 * - 점선 테두리
 */
public class ReadOnlyStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(192, 192, 192)  // GREY_25_PERCENT
                  .font("맑은 고딕", 10, FontStyle.NORMAL)
                  .fontColor(51, 51, 51)  // GREY_80_PERCENT
                  .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                  .border(BorderStyle.DOTTED)
                  .width(120);
    }
}