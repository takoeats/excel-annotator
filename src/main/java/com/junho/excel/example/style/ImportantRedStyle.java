package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 중요한 빨간색 스타일
 * - 빨간색 배경
 * - 흰색 굵은 글씨
 * - 중앙 정렬
 * - 큰 폰트 (14pt)
 */
public class ImportantRedStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 0, 0)  // RED -> RGB
                  .font("맑은 고딕", 14, FontStyle.BOLD)
                  .fontColor(255, 255, 255)  // WHITE -> RGB
                  .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                  .border(BorderStyle.THICK)
                  .width(130);
    }
}