package com.junho.excel.example.style;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 연한 보라색 컬럼 스타일
 * - 연보라색 배경 (230, 230, 250) LAVENDER
 * - 검은색 일반 글씨 (BLACK)
 * - 왼쪽 정렬 (일반 텍스트 데이터용)
 * - 얇은 테두리 (THIN)
 *
 * <h3>📌 사용 용도</h3>
 * <ul>
 *   <li><strong>중요 컬럼 강조</strong> (일반 데이터 셀, 텍스트 정보)</li>
 *   <li>검은 글씨 → 읽기 편함, 데이터 시인성</li>
 * </ul>
 *
 * <h3>🔗 관련 스타일</h3>
 * <ul>
 *   <li><strong>PurpleHeaderStyle</strong>: 동일 배경색, 흰 글씨, 중앙 정렬 (헤더용)</li>
 *   <li><strong>차이점</strong>: LightPurple=컬럼/검은글씨/왼쪽/가벼움, PurpleHeader=헤더/흰글씨/중앙/강조</li>
 * </ul>
 *
 * @see PurpleHeaderStyle
 */
public class LightPurpleColumnStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
        configurer.backgroundColor(230, 230, 250)
                  .font("맑은 고딕", 10, FontStyle.NORMAL)
                  .fontColor(0, 0, 0)
                  .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
                  .border(BorderStyle.THIN)
                  .width(90);
    }
}