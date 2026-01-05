package io.github.takoeats.excelannotator.style;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * IndexedColors를 RGB 값으로 제공하는 유틸리티 클래스
 *
 * <h3>📝 사용 예시</h3>
 * <pre>
 * // Static 메서드 직접 호출
 * configurer.backgroundColor(IndexedColorsToRgb.lavender())
 *           .fontColor(IndexedColorsToRgb.white());
 * </pre>
 *
 * @see ExcelCellStyleConfigurer
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExcelColors {


    // ========== 기본 색상 ==========

    public static int[] black() {
        return new int[]{0, 0, 0};
    }

    public static int[] white() {
        return new int[]{255, 255, 255};
    }

    public static int[] red() {
        return new int[]{255, 0, 0};
    }

    public static int[] brightGreen() {
        return new int[]{0, 255, 0};
    }

    public static int[] blue() {
        return new int[]{0, 0, 255};
    }

    public static int[] yellow() {
        return new int[]{255, 255, 0};
    }

    public static int[] pink() {
        return new int[]{255, 192, 203};
    }

    public static int[] turquoise() {
        return new int[]{64, 224, 208};
    }

    public static int[] darkRed() {
        return new int[]{139, 0, 0};
    }

    public static int[] green() {
        return new int[]{0, 128, 0};
    }

    public static int[] darkBlue() {
        return new int[]{0, 0, 139};
    }

    public static int[] darkYellow() {
        return new int[]{128, 128, 0};
    }

    public static int[] violet() {
        return new int[]{128, 0, 128};
    }

    public static int[] teal() {
        return new int[]{0, 128, 128};
    }

    // ========== 회색 계열 ==========

    public static int[] grey25Percent() {
        return new int[]{192, 192, 192};
    }

    public static int[] grey40Percent() {
        return new int[]{150, 150, 150};
    }

    public static int[] grey50Percent() {
        return new int[]{128, 128, 128};
    }

    public static int[] grey80Percent() {
        return new int[]{51, 51, 51};
    }

    // ========== 연한 색상 ==========

    public static int[] lightBlue() {
        return new int[]{173, 216, 230};
    }

    public static int[] lightGreen() {
        return new int[]{144, 238, 144};
    }

    public static int[] lightOrange() {
        return new int[]{255, 160, 122};
    }

    public static int[] lightTurquoise() {
        return new int[]{175, 238, 238};
    }

    public static int[] lightYellow() {
        return new int[]{255, 255, 224};
    }

    // ========== 파란 계열 확장 ==========

    public static int[] cornflowerBlue() {
        return new int[]{153, 153, 255};
    }

    public static int[] royalBlue() {
        return new int[]{0, 102, 204};
    }

    public static int[] lightCornflowerBlue() {
        return new int[]{204, 204, 255};
    }

    public static int[] paleBlue() {
        return new int[]{153, 204, 255};
    }

    public static int[] skyBlue() {
        return new int[]{0, 204, 255};
    }

    public static int[] blueGrey() {
        return new int[]{102, 102, 153};
    }

    // ========== 보라/라벤더 계열 ==========

    public static int[] lavender() {
        return new int[]{204, 153, 255};
    }

    public static int[] orchid() {
        return new int[]{102, 0, 102};
    }

    public static int[] plum() {
        return new int[]{153, 51, 102};
    }

    // ========== 노란/금색 계열 ==========

    public static int[] lemonChiffon() {
        return new int[]{255, 255, 204};
    }

    public static int[] gold() {
        return new int[]{255, 204, 0};
    }

    // ========== 기타 색상 ==========

    public static int[] aqua() {
        return new int[]{51, 204, 204};
    }

    public static int[] rose() {
        return new int[]{255, 228, 225};
    }

    public static int[] darkGreen() {
        return new int[]{0, 100, 0};
    }

    public static int[] darkTeal() {
        return new int[]{0, 128, 128};
    }

    public static int[] orange() {
        return new int[]{255, 165, 0};
    }

    public static int[] coral() {
        return new int[]{255, 127, 80};
    }

    public static int[] brown() {
        return new int[]{165, 42, 42};
    }

    public static int[] indigo() {
        return new int[]{75, 0, 130};
    }

    public static int[] lime() {
        return new int[]{0, 255, 0};
    }

    public static int[] maroon() {
        return new int[]{128, 0, 0};
    }

    public static int[] oliveGreen() {
        return new int[]{128, 128, 0};
    }

    public static int[] seaGreen() {
        return new int[]{46, 139, 87};
    }

    public static int[] tan() {
        return new int[]{210, 180, 140};
    }


    /**
     * @deprecated 이 메서드는 엑셀의 자동 색상 처리에 대한 유스케이스 부족으로 인해
     * 차기 메이저 버전 3.0.0에서 삭제될 예정입니다.
     * 대신 필요에 따라 직접 RGB 값을 지정하거나 black() 등을 사용하십시오.
     */
    @Deprecated
    public static int[] automatic() {
        return new int[]{128, 128, 128};
    }
}
