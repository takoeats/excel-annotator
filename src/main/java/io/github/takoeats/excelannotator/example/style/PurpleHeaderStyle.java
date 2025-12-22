package io.github.takoeats.excelannotator.example.style;

import io.github.takoeats.excelannotator.style.CustomExcelCellStyle;
import io.github.takoeats.excelannotator.style.ExcelCellStyleConfigurer;
import io.github.takoeats.excelannotator.style.FontStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 연보라색 배경의 헤더 스타일
 * - 연보라색 배경 (230, 230, 250) LAVENDER
 * - 굵은 흰색 글씨 (WHITE)
 * - 중앙 정렬
 * - 두꺼운 테두리 (THICK)
 *
 * <h3>📌 사용 용도</h3>
 * <ul>
 *   <li><strong>테이블 헤더 강조</strong> (데이터 테이블의 컬럼 헤더)</li>
 *   <li>흰색 글씨 → 배경과 대비, 가독성 향상</li>
 * </ul>
 *
 * <h3>🔗 관련 스타일</h3>
 * <ul>
 *   <li><strong>LightPurpleColumnStyle</strong>: 동일 배경색, 검은 글씨, 왼쪽 정렬 (일반 컬럼용)</li>
 *   <li><strong>차이점</strong>: PurpleHeader=헤더/흰글씨/중앙/굵음, LightPurple=컬럼/검은글씨/왼쪽/일반</li>
 * </ul>
 *
 * @see LightPurpleColumnStyle
 */
public class PurpleHeaderStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(230, 230, 250)  // LAVENDER -> RGB
                .font("맑은 고딕", 11, FontStyle.BOLD)
                .fontColor(255, 255, 255)  // WHITE -> RGB
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .border(BorderStyle.THICK)
                .width(120);
    }
}