package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 테이블 푸터/합계 행 스타일
 * - 연한 파란색 배경
 * - 검은색 굵은 글씨
 * - 중앙 정렬
 * - 굵은 테두리
 */
public class TableFooterStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(173, 216, 230)  // LIGHT_BLUE
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(0, 0, 0)  // BLACK
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THICK)
                .width(120);
    }
}