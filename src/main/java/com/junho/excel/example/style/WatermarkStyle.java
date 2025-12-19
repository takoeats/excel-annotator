package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 워터마크 스타일
 * - 매우 연한 회색 배경
 * - 연한 회색 글씨 (반투명 효과)
 * - 중앙 정렬
 * - 기울임꼴
 * - 테두리 없음
 */
public class WatermarkStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(192, 192, 192)  // GREY_25_PERCENT
                .font("맑은 고딕", 9, FontStyle.ITALIC)
                .fontColor(128, 128, 128)  // GREY_50_PERCENT
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.NONE)
                .width(100);
    }
}