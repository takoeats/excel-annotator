package io.github.takoeats.excelannotator.style.defaultstyle;

import io.github.takoeats.excelannotator.style.BorderType;
import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.HorizontalAlign;

/**
 * 기본 컬럼 스타일
 */
public class DefaultColumnStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.font("맑은 고딕", 10)
                .alignment(HorizontalAlign.LEFT)
                .border(BorderType.THIN)
                .width(100);
    }
}