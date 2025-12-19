package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 인쇄용 헤더 스타일
 * - 검은색 배경
 * - 흰색 굵은 글씨
 * - 중앙 정렬
 * - 큰 폰트 (14pt)
 * - 굵은 테두리
 * - 인쇄 시 명확한 구분
 */
public class PrintHeaderStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(0, 0, 0)  // BLACK
                .font("맑은 고딕", 14, FontStyle.BOLD)
                .fontColor(255, 255, 255)  // WHITE
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THICK)
                .width(150);
    }
}