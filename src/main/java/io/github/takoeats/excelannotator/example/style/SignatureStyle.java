package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.ExcelColors;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 서명/승인 필드 스타일
 * - 연한 파란색 배경
 * - 파란색 굵은 글씨
 * - 중앙 정렬
 * - 이중 테두리 (공식 문서 느낌)
 * - 서명, 승인자, 날인 등에 사용
 */
public class SignatureStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(175, 238, 238)  // PALE_BLUE
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(ExcelColors.blue())  // BLUE
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.DOUBLE)
                .width(130);
    }
}