package io.github.takoeats.excelannotator.masking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("MaskingUtil 단위 테스트")
public class MaskingUtilTest {

    @Test
    @DisplayName("repeat - 0 이하 count는 빈 문자열 반환")
    void repeat_withZeroOrNegativeCount_returnsEmpty() {
        assertEquals("", MaskingUtil.repeat('*', 0));
        assertEquals("", MaskingUtil.repeat('*', -1));
        assertEquals("", MaskingUtil.repeat('*', -10));
    }

    @Test
    @DisplayName("repeat - 양수 count는 문자 반복")
    void repeat_withPositiveCount_repeatsChar() {
        assertEquals("*", MaskingUtil.repeat('*', 1));
        assertEquals("***", MaskingUtil.repeat('*', 3));
        assertEquals("##########", MaskingUtil.repeat('#', 10));
    }

    @Test
    @DisplayName("maskLeft - null 값은 그대로 반환")
    void maskLeft_withNull_returnsNull() {
        assertNull(MaskingUtil.maskLeft(null, 4));
        assertNull(MaskingUtil.maskLeft(null, 4, '#'));
    }

    @Test
    @DisplayName("maskLeft - empty 값은 그대로 반환")
    void maskLeft_withEmpty_returnsEmpty() {
        assertEquals("", MaskingUtil.maskLeft("", 4));
        assertEquals("", MaskingUtil.maskLeft("", 4, '#'));
    }

    @Test
    @DisplayName("maskLeft - 길이가 visibleRight 이하면 그대로 반환")
    void maskLeft_withLengthLessThanOrEqualToVisibleRight_returnsOriginal() {
        assertEquals("1234", MaskingUtil.maskLeft("1234", 4));
        assertEquals("1234", MaskingUtil.maskLeft("1234", 5));
        assertEquals("AB", MaskingUtil.maskLeft("AB", 2));
        assertEquals("AB", MaskingUtil.maskLeft("AB", 3));
    }

    @Test
    @DisplayName("maskLeft - 길이가 visibleRight보다 크면 왼쪽 마스킹")
    void maskLeft_withLengthGreaterThanVisibleRight_masksLeft() {
        assertEquals("****2345", MaskingUtil.maskLeft("ABC12345", 4));
        assertEquals("**************5678", MaskingUtil.maskLeft("123456789012345678", 4));
        assertEquals("*****", MaskingUtil.maskLeft("ABCDE", 0));
    }

    @Test
    @DisplayName("maskLeft - 커스텀 마스킹 문자 사용")
    void maskLeft_withCustomMaskChar_usesCustomChar() {
        assertEquals("####2345", MaskingUtil.maskLeft("ABC12345", 4, '#'));
        assertEquals("____2345", MaskingUtil.maskLeft("ABC12345", 4, '_'));
    }

    @Test
    @DisplayName("maskRight - null 값은 그대로 반환")
    void maskRight_withNull_returnsNull() {
        assertNull(MaskingUtil.maskRight(null, 4));
        assertNull(MaskingUtil.maskRight(null, 4, '#'));
    }

    @Test
    @DisplayName("maskRight - empty 값은 그대로 반환")
    void maskRight_withEmpty_returnsEmpty() {
        assertEquals("", MaskingUtil.maskRight("", 4));
        assertEquals("", MaskingUtil.maskRight("", 4, '#'));
    }

    @Test
    @DisplayName("maskRight - 길이가 visibleLeft 이하면 그대로 반환")
    void maskRight_withLengthLessThanOrEqualToVisibleLeft_returnsOriginal() {
        assertEquals("1234", MaskingUtil.maskRight("1234", 4));
        assertEquals("1234", MaskingUtil.maskRight("1234", 5));
        assertEquals("AB", MaskingUtil.maskRight("AB", 2));
        assertEquals("AB", MaskingUtil.maskRight("AB", 3));
    }

    @Test
    @DisplayName("maskRight - 길이가 visibleLeft보다 크면 오른쪽 마스킹")
    void maskRight_withLengthGreaterThanVisibleLeft_masksRight() {
        assertEquals("ABC1****", MaskingUtil.maskRight("ABC12345", 4));
        assertEquals("1234**************", MaskingUtil.maskRight("123456789012345678", 4));
        assertEquals("*****", MaskingUtil.maskRight("ABCDE", 0));
    }

    @Test
    @DisplayName("maskRight - 커스텀 마스킹 문자 사용")
    void maskRight_withCustomMaskChar_usesCustomChar() {
        assertEquals("ABC1####", MaskingUtil.maskRight("ABC12345", 4, '#'));
        assertEquals("ABC1____", MaskingUtil.maskRight("ABC12345", 4, '_'));
    }

    @Test
    @DisplayName("maskMiddle - null 값은 그대로 반환")
    void maskMiddle_withNull_returnsNull() {
        assertNull(MaskingUtil.maskMiddle(null, 2, 2));
        assertNull(MaskingUtil.maskMiddle(null, 2, 2, '#'));
    }

    @Test
    @DisplayName("maskMiddle - empty 값은 그대로 반환")
    void maskMiddle_withEmpty_returnsEmpty() {
        assertEquals("", MaskingUtil.maskMiddle("", 2, 2));
        assertEquals("", MaskingUtil.maskMiddle("", 2, 2, '#'));
    }

    @Test
    @DisplayName("maskMiddle - 길이가 visibleLeft + visibleRight 이하면 그대로 반환")
    void maskMiddle_withLengthLessThanOrEqualToVisibleSum_returnsOriginal() {
        assertEquals("1234", MaskingUtil.maskMiddle("1234", 2, 2));
        assertEquals("1234", MaskingUtil.maskMiddle("1234", 2, 3));
        assertEquals("AB", MaskingUtil.maskMiddle("AB", 1, 1));
        assertEquals("ABC", MaskingUtil.maskMiddle("ABC", 2, 2));
    }

    @Test
    @DisplayName("maskMiddle - 길이가 visibleLeft + visibleRight보다 크면 중간 마스킹")
    void maskMiddle_withLengthGreaterThanVisibleSum_masksMiddle() {
        assertEquals("AB****GH", MaskingUtil.maskMiddle("ABCDEFGH", 2, 2));
        assertEquals("123************************890", MaskingUtil.maskMiddle("123456789012345678901234567890", 3, 3));
        assertEquals("A***E", MaskingUtil.maskMiddle("ABCDE", 1, 1));
    }

    @Test
    @DisplayName("maskMiddle - visibleLeft 0인 경우")
    void maskMiddle_withZeroVisibleLeft_masksFromStart() {
        assertEquals("******GH", MaskingUtil.maskMiddle("ABCDEFGH", 0, 2));
    }

    @Test
    @DisplayName("maskMiddle - visibleRight 0인 경우")
    void maskMiddle_withZeroVisibleRight_masksToEnd() {
        assertEquals("AB******", MaskingUtil.maskMiddle("ABCDEFGH", 2, 0));
    }

    @Test
    @DisplayName("maskMiddle - 둘 다 0인 경우")
    void maskMiddle_withBothZero_masksAll() {
        assertEquals("********", MaskingUtil.maskMiddle("ABCDEFGH", 0, 0));
    }

    @Test
    @DisplayName("maskMiddle - 커스텀 마스킹 문자 사용")
    void maskMiddle_withCustomMaskChar_usesCustomChar() {
        assertEquals("AB####GH", MaskingUtil.maskMiddle("ABCDEFGH", 2, 2, '#'));
        assertEquals("AB____GH", MaskingUtil.maskMiddle("ABCDEFGH", 2, 2, '_'));
    }

    @Test
    @DisplayName("maskAll - null 값은 그대로 반환")
    void maskAll_withNull_returnsNull() {
        assertNull(MaskingUtil.maskAll(null));
        assertNull(MaskingUtil.maskAll(null, '#'));
    }

    @Test
    @DisplayName("maskAll - empty 값은 그대로 반환")
    void maskAll_withEmpty_returnsEmpty() {
        assertEquals("", MaskingUtil.maskAll(""));
        assertEquals("", MaskingUtil.maskAll("", '#'));
    }

    @Test
    @DisplayName("maskAll - 전체 마스킹")
    void maskAll_masksEntireString() {
        assertEquals("********", MaskingUtil.maskAll("ABC12345"));
        assertEquals("***", MaskingUtil.maskAll("abc"));
        assertEquals("*", MaskingUtil.maskAll("1"));
    }

    @Test
    @DisplayName("maskAll - 커스텀 마스킹 문자 사용")
    void maskAll_withCustomMaskChar_usesCustomChar() {
        assertEquals("########", MaskingUtil.maskAll("ABC12345", '#'));
        assertEquals("________", MaskingUtil.maskAll("ABC12345", '_'));
        assertEquals("XXXXXXXX", MaskingUtil.maskAll("ABC12345", 'X'));
    }

    @Test
    @DisplayName("통합 테스트 - 다양한 시나리오")
    void integrationTest_variousScenarios() {
        assertEquals("****5678", MaskingUtil.maskLeft("12345678", 4));
        assertEquals("1234****", MaskingUtil.maskRight("12345678", 4));
        assertEquals("12**78", MaskingUtil.maskMiddle("123478", 2, 2));
        assertEquals("********", MaskingUtil.maskAll("12345678"));

        assertEquals("####5678", MaskingUtil.maskLeft("12345678", 4, '#'));
        assertEquals("1234####", MaskingUtil.maskRight("12345678", 4, '#'));
        assertEquals("12##78", MaskingUtil.maskMiddle("123478", 2, 2, '#'));
        assertEquals("########", MaskingUtil.maskAll("12345678", '#'));
    }

    @Test
    @DisplayName("경계값 테스트 - 1글자 문자열")
    void boundaryTest_singleChar() {
        assertEquals("A", MaskingUtil.maskLeft("A", 1));
        assertEquals("A", MaskingUtil.maskRight("A", 1));
        assertEquals("A", MaskingUtil.maskMiddle("A", 0, 1));
        assertEquals("*", MaskingUtil.maskAll("A"));
    }

    @Test
    @DisplayName("경계값 테스트 - visible이 길이보다 큰 경우")
    void boundaryTest_visibleGreaterThanLength() {
        assertEquals("ABC", MaskingUtil.maskLeft("ABC", 10));
        assertEquals("ABC", MaskingUtil.maskRight("ABC", 10));
        assertEquals("ABC", MaskingUtil.maskMiddle("ABC", 5, 5));
    }
}
