package io.github.takoeats.excelannotator.masking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Masking 단위 테스트")
public class MaskingTest {

    @Test
    @DisplayName("NONE - null 값은 그대로 반환한다")
    void none_withNull_returnsNull() {
        assertNull(Masking.NONE.mask(null));
    }

    @Test
    @DisplayName("NONE - empty 값은 그대로 반환한다")
    void none_withEmpty_returnsEmpty() {
        assertEquals("", Masking.NONE.mask(""));
    }

    @Test
    @DisplayName("NONE - 일반 값은 그대로 반환한다")
    void none_withValue_returnsValue() {
        assertEquals("test", Masking.NONE.mask("test"));
    }

    @Test
    @DisplayName("PHONE - null 값은 그대로 반환한다")
    void phone_withNull_returnsNull() {
        assertNull(Masking.PHONE.mask(null));
    }

    @Test
    @DisplayName("PHONE - empty 값은 그대로 반환한다")
    void phone_withEmpty_returnsEmpty() {
        assertEquals("", Masking.PHONE.mask(""));
    }

    @Test
    @DisplayName("PHONE - 11자리 번호를 마스킹한다")
    void phone_with11Digits_masksCorrectly() {
        assertEquals("010-****-5678", Masking.PHONE.mask("010-1234-5678"));
        assertEquals("010-****-5678", Masking.PHONE.mask("01012345678"));
    }

    @Test
    @DisplayName("PHONE - 10자리 번호를 마스킹한다")
    void phone_with10Digits_masksCorrectly() {
        assertEquals("02-****-5678", Masking.PHONE.mask("02-1234-5678"));
        assertEquals("02-****-5678", Masking.PHONE.mask("0212345678"));
    }

    @Test
    @DisplayName("PHONE - 9자리 번호를 마스킹한다")
    void phone_with9Digits_masksCorrectly() {
        assertEquals("02-***-5678", Masking.PHONE.mask("02-123-5678"));
        assertEquals("02-***-5678", Masking.PHONE.mask("021235678"));
    }

    @Test
    @DisplayName("PHONE - 비정상 길이 번호는 maskMiddle로 처리한다 (길이가 충분한 경우)")
    void phone_withInvalidLength_usesMaskMiddle() {
        String result = Masking.PHONE.mask("12345678");
        assertNotNull(result);
        assertTrue(result.contains("*"));
    }

    @Test
    @DisplayName("PHONE - 비정상 길이 번호는 너무 짧으면 그대로 반환한다")
    void phone_withVeryShortLength_returnsOriginal() {
        String result = Masking.PHONE.mask("12345");
        assertEquals("12345", result);
    }

    @Test
    @DisplayName("EMAIL - null 값은 그대로 반환한다")
    void email_withNull_returnsNull() {
        assertNull(Masking.EMAIL.mask(null));
    }

    @Test
    @DisplayName("EMAIL - empty 값은 그대로 반환한다")
    void email_withEmpty_returnsEmpty() {
        assertEquals("", Masking.EMAIL.mask(""));
    }

    @Test
    @DisplayName("EMAIL - @ 없는 이메일은 그대로 반환한다")
    void email_withoutAt_returnsOriginal() {
        assertEquals("invalid", Masking.EMAIL.mask("invalid"));
    }

    @Test
    @DisplayName("EMAIL - @ 첫 글자인 경우 그대로 반환한다")
    void email_withAtAtStart_returnsOriginal() {
        assertEquals("@example.com", Masking.EMAIL.mask("@example.com"));
    }

    @Test
    @DisplayName("EMAIL - local이 1글자인 경우 *** 추가")
    void email_withSingleCharLocal_addsStars() {
        assertEquals("a***@test.com", Masking.EMAIL.mask("a@test.com"));
    }

    @Test
    @DisplayName("EMAIL - 일반 이메일은 첫 글자만 보존")
    void email_withNormalEmail_masksCorrectly() {
        assertEquals("u***@example.com", Masking.EMAIL.mask("user@example.com"));
        assertEquals("t***@test.co.kr", Masking.EMAIL.mask("test@test.co.kr"));
    }

    @Test
    @DisplayName("SSN - null 값은 그대로 반환한다")
    void ssn_withNull_returnsNull() {
        assertNull(Masking.SSN.mask(null));
    }

    @Test
    @DisplayName("SSN - empty 값은 그대로 반환한다")
    void ssn_withEmpty_returnsEmpty() {
        assertEquals("", Masking.SSN.mask(""));
    }

    @Test
    @DisplayName("SSN - 13자리 dash 있는 경우")
    void ssn_with13DigitsAndDash_masksCorrectly() {
        assertEquals("123456-*******", Masking.SSN.mask("123456-1234567"));
    }

    @Test
    @DisplayName("SSN - 13자리 dash 없는 경우")
    void ssn_with13DigitsNoDash_masksCorrectly() {
        assertEquals("123456*******", Masking.SSN.mask("1234561234567"));
    }

    @Test
    @DisplayName("SSN - dash가 있지만 13자리가 아닌 경우")
    void ssn_withDashButNot13Digits_masksAfterDash() {
        String result = Masking.SSN.mask("12345-67890");
        assertTrue(result.startsWith("12345-"));
        assertTrue(result.contains("*"));
    }

    @Test
    @DisplayName("SSN - dash 없고 13자리도 아닌 경우 maskRight 사용")
    void ssn_withoutDashAndNot13Digits_usesMaskRight() {
        String result = Masking.SSN.mask("12345678");
        assertNotNull(result);
        assertTrue(result.contains("*"));
    }

    @Test
    @DisplayName("NAME - null 값은 그대로 반환한다")
    void name_withNull_returnsNull() {
        assertNull(Masking.NAME.mask(null));
    }

    @Test
    @DisplayName("NAME - empty 값은 그대로 반환한다")
    void name_withEmpty_returnsEmpty() {
        assertEquals("", Masking.NAME.mask(""));
    }

    @Test
    @DisplayName("NAME - 1글자 이름은 그대로 반환한다")
    void name_withSingleChar_returnsOriginal() {
        assertEquals("김", Masking.NAME.mask("김"));
    }

    @Test
    @DisplayName("NAME - 2글자 이름은 마지막 글자 마스킹")
    void name_with2Chars_masksLastChar() {
        assertEquals("이*", Masking.NAME.mask("이영"));
    }

    @Test
    @DisplayName("NAME - 3글자 이상 이름은 중간 마스킹")
    void name_with3OrMoreChars_masksMiddle() {
        assertEquals("홍*동", Masking.NAME.mask("홍길동"));
        assertEquals("김*수", Masking.NAME.mask("김철수"));
        assertEquals("이**희", Masking.NAME.mask("이영철희"));
    }

    @Test
    @DisplayName("CREDIT_CARD - null 값은 그대로 반환한다")
    void creditCard_withNull_returnsNull() {
        assertNull(Masking.CREDIT_CARD.mask(null));
    }

    @Test
    @DisplayName("CREDIT_CARD - empty 값은 그대로 반환한다")
    void creditCard_withEmpty_returnsEmpty() {
        assertEquals("", Masking.CREDIT_CARD.mask(""));
    }

    @Test
    @DisplayName("CREDIT_CARD - 16자리 dash 있는 경우")
    void creditCard_with16DigitsAndDash_masksCorrectly() {
        assertEquals("****-****-****-3456", Masking.CREDIT_CARD.mask("1234-5678-9012-3456"));
    }

    @Test
    @DisplayName("CREDIT_CARD - 16자리 dash 없는 경우")
    void creditCard_with16DigitsNoDash_masksCorrectly() {
        assertEquals("************3456", Masking.CREDIT_CARD.mask("1234567890123456"));
    }

    @Test
    @DisplayName("CREDIT_CARD - 16자리가 아닌 경우 maskRight 사용 (왼쪽 4자리 보존)")
    void creditCard_withNot16Digits_usesMaskRight() {
        String result = Masking.CREDIT_CARD.mask("123456789012");
        assertNotNull(result);
        assertTrue(result.startsWith("1234"));
        assertTrue(result.contains("*"));
    }

    @Test
    @DisplayName("ACCOUNT_NUMBER - null 값은 그대로 반환한다")
    void accountNumber_withNull_returnsNull() {
        assertNull(Masking.ACCOUNT_NUMBER.mask(null));
    }

    @Test
    @DisplayName("ACCOUNT_NUMBER - empty 값은 그대로 반환한다")
    void accountNumber_withEmpty_returnsEmpty() {
        assertEquals("", Masking.ACCOUNT_NUMBER.mask(""));
    }

    @Test
    @DisplayName("ACCOUNT_NUMBER - 3부분 dash 형식 마스킹")
    void accountNumber_with3Parts_masksMiddle() {
        assertEquals("110-***-***789", Masking.ACCOUNT_NUMBER.mask("110-123-456789"));
    }

    @Test
    @DisplayName("ACCOUNT_NUMBER - dash 없는 경우 maskMiddle 사용")
    void accountNumber_withoutDash_usesMaskMiddle() {
        String result = Masking.ACCOUNT_NUMBER.mask("1234567890");
        assertNotNull(result);
        assertTrue(result.contains("*"));
    }

    @Test
    @DisplayName("ADDRESS - null 값은 그대로 반환한다")
    void address_withNull_returnsNull() {
        assertNull(Masking.ADDRESS.mask(null));
    }

    @Test
    @DisplayName("ADDRESS - empty 값은 그대로 반환한다")
    void address_withEmpty_returnsEmpty() {
        assertEquals("", Masking.ADDRESS.mask(""));
    }

    @Test
    @DisplayName("ADDRESS - 3단어 이상인 경우")
    void address_with3OrMoreParts_masks3rdPart() {
        assertEquals("서울시 강남구 ***", Masking.ADDRESS.mask("서울시 강남구 테헤란로 123"));
    }

    @Test
    @DisplayName("ADDRESS - 2단어인 경우")
    void address_with2Parts_masks2ndPart() {
        assertEquals("서울시 ***", Masking.ADDRESS.mask("서울시 강남구"));
    }

    @Test
    @DisplayName("ADDRESS - 1단어인 경우 maskRight 사용")
    void address_with1Part_usesMaskRight() {
        String result = Masking.ADDRESS.mask("서울시");
        assertEquals("***", result);
    }

    @Test
    @DisplayName("ZIP_CODE - null 값은 그대로 반환한다")
    void zipCode_withNull_returnsNull() {
        assertNull(Masking.ZIP_CODE.mask(null));
    }

    @Test
    @DisplayName("ZIP_CODE - empty 값은 그대로 반환한다")
    void zipCode_withEmpty_returnsEmpty() {
        assertEquals("", Masking.ZIP_CODE.mask(""));
    }

    @Test
    @DisplayName("ZIP_CODE - 5자리 우편번호")
    void zipCode_with5Digits_masksLast2() {
        assertEquals("123**", Masking.ZIP_CODE.mask("12345"));
    }

    @Test
    @DisplayName("ZIP_CODE - 6자리 우편번호")
    void zipCode_with6Digits_masksLast3() {
        assertEquals("123***", Masking.ZIP_CODE.mask("123456"));
    }

    @Test
    @DisplayName("ZIP_CODE - 3자리 이하는 그대로 반환")
    void zipCode_with3OrLessChars_returnsOriginal() {
        assertEquals("123", Masking.ZIP_CODE.mask("123"));
        assertEquals("12", Masking.ZIP_CODE.mask("12"));
    }

    @Test
    @DisplayName("ZIP_CODE - 비정상 길이는 앞 3자리 보존")
    void zipCode_withOtherLength_keeps3Chars() {
        assertEquals("123*", Masking.ZIP_CODE.mask("1234"));
        assertEquals("123****", Masking.ZIP_CODE.mask("1234567"));
    }

    @Test
    @DisplayName("IP_ADDRESS - null 값은 그대로 반환한다")
    void ipAddress_withNull_returnsNull() {
        assertNull(Masking.IP_ADDRESS.mask(null));
    }

    @Test
    @DisplayName("IP_ADDRESS - empty 값은 그대로 반환한다")
    void ipAddress_withEmpty_returnsEmpty() {
        assertEquals("", Masking.IP_ADDRESS.mask(""));
    }

    @Test
    @DisplayName("IP_ADDRESS - IPv4 주소 마스킹")
    void ipAddress_withIPv4_masksLast2Octets() {
        assertEquals("192.168.*.*", Masking.IP_ADDRESS.mask("192.168.1.100"));
    }

    @Test
    @DisplayName("IP_ADDRESS - IPv4 비정상 형식은 그대로 반환")
    void ipAddress_withInvalidIPv4_returnsOriginal() {
        assertEquals("192.168.1", Masking.IP_ADDRESS.mask("192.168.1"));
    }

    @Test
    @DisplayName("IP_ADDRESS - IPv6 주소 마스킹")
    void ipAddress_withIPv6_masksAfter2ndPart() {
        assertEquals("2001:0db8:****:****:****:****:****:****",
                Masking.IP_ADDRESS.mask("2001:0db8:85a3::8a2e:0370:7334"));
    }

    @Test
    @DisplayName("IP_ADDRESS - IPv6 비정상 형식은 그대로 반환")
    void ipAddress_withInvalidIPv6_returnsOriginal() {
        assertEquals("2001", Masking.IP_ADDRESS.mask("2001"));
    }

    @Test
    @DisplayName("IP_ADDRESS - . 또는 : 없는 경우 그대로 반환")
    void ipAddress_withoutDelimiters_returnsOriginal() {
        assertEquals("invalid", Masking.IP_ADDRESS.mask("invalid"));
    }

    @Test
    @DisplayName("ID_CARD - null 값은 그대로 반환한다")
    void idCard_withNull_returnsNull() {
        assertNull(Masking.ID_CARD.mask(null));
    }

    @Test
    @DisplayName("ID_CARD - empty 값은 그대로 반환한다")
    void idCard_withEmpty_returnsEmpty() {
        assertEquals("", Masking.ID_CARD.mask(""));
    }

    @Test
    @DisplayName("ID_CARD - 10자리 이상은 1/3 지점부터 마스킹")
    void idCard_with10OrMoreDigits_masksFromOneThird() {
        String result = Masking.ID_CARD.mask("11-12-345678-90");
        assertNotNull(result);
        assertTrue(result.contains("*"));
    }

    @Test
    @DisplayName("ID_CARD - 10자리 미만은 오른쪽 6자리 마스킹")
    void idCard_withLessThan10Digits_masksRight6() {
        String result = Masking.ID_CARD.mask("123456789");
        assertNotNull(result);
        assertTrue(result.contains("*"));
    }

    @Test
    @DisplayName("PASSPORT - null 값은 그대로 반환한다")
    void passport_withNull_returnsNull() {
        assertNull(Masking.PASSPORT.mask(null));
    }

    @Test
    @DisplayName("PASSPORT - empty 값은 그대로 반환한다")
    void passport_withEmpty_returnsEmpty() {
        assertEquals("", Masking.PASSPORT.mask(""));
    }

    @Test
    @DisplayName("PASSPORT - 6자리 이하는 앞 2자리 보존")
    void passport_with6OrLessChars_keeps2Chars() {
        String result = Masking.PASSPORT.mask("M12345");
        assertNotNull(result);
        assertTrue(result.startsWith("M1"));
        assertTrue(result.contains("*"));
    }

    @Test
    @DisplayName("PASSPORT - 7자리 이상은 앞뒤 3자리 보존")
    void passport_with7OrMoreChars_keeps3CharsEachSide() {
        assertEquals("M12***678", Masking.PASSPORT.mask("M12345678"));
        assertEquals("AB1***567", Masking.PASSPORT.mask("AB1234567"));
    }

    @Test
    @DisplayName("LICENSE_PLATE - null 값은 그대로 반환한다")
    void licensePlate_withNull_returnsNull() {
        assertNull(Masking.LICENSE_PLATE.mask(null));
    }

    @Test
    @DisplayName("LICENSE_PLATE - empty 값은 그대로 반환한다")
    void licensePlate_withEmpty_returnsEmpty() {
        assertEquals("", Masking.LICENSE_PLATE.mask(""));
    }

    @Test
    @DisplayName("LICENSE_PLATE - 4자리 이하는 그대로 반환")
    void licensePlate_with4OrLessChars_returnsOriginal() {
        assertEquals("1234", Masking.LICENSE_PLATE.mask("1234"));
        assertEquals("123", Masking.LICENSE_PLATE.mask("123"));
    }

    @Test
    @DisplayName("LICENSE_PLATE - 5자리 이상은 중간 마스킹")
    void licensePlate_with5OrMoreChars_masksMiddle() {
        assertEquals("12가**56", Masking.LICENSE_PLATE.mask("12가3456"));
        assertEquals("서울12가**56", Masking.LICENSE_PLATE.mask("서울12가3456"));
    }

    @Test
    @DisplayName("PARTIAL_LEFT - null 값은 그대로 반환한다")
    void partialLeft_withNull_returnsNull() {
        assertNull(Masking.PARTIAL_LEFT.mask(null));
    }

    @Test
    @DisplayName("PARTIAL_LEFT - empty 값은 그대로 반환한다")
    void partialLeft_withEmpty_returnsEmpty() {
        assertEquals("", Masking.PARTIAL_LEFT.mask(""));
    }

    @Test
    @DisplayName("PARTIAL_LEFT - 왼쪽 마스킹 오른쪽 4자리 보존")
    void partialLeft_masksLeft() {
        assertEquals("****2345", Masking.PARTIAL_LEFT.mask("ABC12345"));
    }

    @Test
    @DisplayName("PARTIAL_RIGHT - null 값은 그대로 반환한다")
    void partialRight_withNull_returnsNull() {
        assertNull(Masking.PARTIAL_RIGHT.mask(null));
    }

    @Test
    @DisplayName("PARTIAL_RIGHT - empty 값은 그대로 반환한다")
    void partialRight_withEmpty_returnsEmpty() {
        assertEquals("", Masking.PARTIAL_RIGHT.mask(""));
    }

    @Test
    @DisplayName("PARTIAL_RIGHT - 오른쪽 마스킹 왼쪽 4자리 보존")
    void partialRight_masksRight() {
        assertEquals("1234******", Masking.PARTIAL_RIGHT.mask("1234567890"));
    }

    @Test
    @DisplayName("MIDDLE - null 값은 그대로 반환한다")
    void middle_withNull_returnsNull() {
        assertNull(Masking.MIDDLE.mask(null));
    }

    @Test
    @DisplayName("MIDDLE - empty 값은 그대로 반환한다")
    void middle_withEmpty_returnsEmpty() {
        assertEquals("", Masking.MIDDLE.mask(""));
    }

    @Test
    @DisplayName("MIDDLE - 중간 마스킹 양쪽 2자리 보존")
    void middle_masksMiddle() {
        assertEquals("AB****GH", Masking.MIDDLE.mask("ABCDEFGH"));
    }
}
