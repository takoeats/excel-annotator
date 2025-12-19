package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 한국식 날짜 표시 스타일
 * - 연한 노란색 배경
 * - 중앙 정렬
 * - 한국 날짜 포맷 (yyyy년 MM월 dd일)
 */
public class KoreanDateStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(255, 255, 224)  // LIGHT_YELLOW
                .font("맑은 고딕", 10, FontStyle.NORMAL)
                .fontColor(0, 0, 0)  // BLACK
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THIN)
                .numberFormat("yyyy\"년\" MM\"월\" dd\"일\"")
                .width(130);
    }
}