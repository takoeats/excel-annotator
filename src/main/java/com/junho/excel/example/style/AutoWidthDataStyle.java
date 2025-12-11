package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 자동 너비 조정 데이터 스타일
 * - 자동 너비 조정 (autoWidth)
 * - 흰색 배경
 * - 검은색 일반 글씨
 * - 우측 정렬 (숫자 데이터에 적합)
 * - 얇은 테두리
 */
public class AutoWidthDataStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 255, 255)  // WHITE -> RGB
                  .font("맑은 고딕", 10, FontStyle.NORMAL)
                  .fontColor(0, 0, 0)  // BLACK -> RGB
                  .alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER)
                  .border(BorderStyle.THIN)
                  .autoWidth();  // 자동 너비 조정
    }
}