package com.junho.excel.style;

import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 * RGB 색상 처리 유틸리티 클래스
 *
 * <p>Excel 스타일에서 RGB 색상을 처리하기 위한 헬퍼 메서드를 제공합니다.</p>
 *
 * <h3>주요 기능</h3>
 * <ul>
 *   <li>RGB 값 유효성 검증 (0-255 범위)</li>
 *   <li>RGB 값을 XSSFColor 객체로 변환</li>
 * </ul>
 *
 * <h3>사용 예시</h3>
 * <pre>
 * // RGB 값 검증
 * RgbColorHelper.validateRgb(255, 0, 0);
 *
 * // XSSFColor 생성
 * XSSFColor red = RgbColorHelper.createRgbColor(255, 0, 0);
 * </pre>
 *
 * @since 1.0
 */
public final class RgbColorHelper {

    private RgbColorHelper() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * RGB 값의 유효성을 검증합니다.
     *
     * @param red   빨간색 값 (0-255)
     * @param green 초록색 값 (0-255)
     * @param blue  파란색 값 (0-255)
     * @throws ExcelExporterException RGB 값이 0-255 범위를 벗어난 경우
     */
    public static void validateRgb(int red, int green, int blue) {
        if (red < 0 || red > 255) {
            throw new ExcelExporterException(
                    ErrorCode.INVALID_RGB_VALUE,
                    String.format("Red 값이 유효 범위를 벗어났습니다: %d (0-255 범위 필요)", red)
            );
        }
        if (green < 0 || green > 255) {
            throw new ExcelExporterException(
                    ErrorCode.INVALID_RGB_VALUE,
                    String.format("Green 값이 유효 범위를 벗어났습니다: %d (0-255 범위 필요)", green)
            );
        }
        if (blue < 0 || blue > 255) {
            throw new ExcelExporterException(
                    ErrorCode.INVALID_RGB_VALUE,
                    String.format("Blue 값이 유효 범위를 벗어났습니다: %d (0-255 범위 필요)", blue)
            );
        }
    }

    /**
     * RGB 값을 XSSFColor 객체로 변환합니다.
     *
     * <p>XSSF/SXSSF Workbook에서만 사용 가능합니다.</p>
     *
     * @param red   빨간색 값 (0-255)
     * @param green 초록색 값 (0-255)
     * @param blue  파란색 값 (0-255)
     * @return XSSFColor 객체
     * @throws IllegalArgumentException RGB 값이 0-255 범위를 벗어난 경우
     */
    public static XSSFColor createRgbColor(int red, int green, int blue) {
        validateRgb(red, green, blue);
        return new XSSFColor(new byte[]{
                (byte) red,
                (byte) green,
                (byte) blue
        }, null);
    }
}