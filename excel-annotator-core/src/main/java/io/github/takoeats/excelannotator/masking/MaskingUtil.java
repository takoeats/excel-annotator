package io.github.takoeats.excelannotator.masking;

/**
 * 마스킹 유틸리티 클래스
 * <p>공통 마스킹 패턴을 제공합니다.</p>
 */
public final class MaskingUtil {

    private static final char DEFAULT_MASK_CHAR = '*';

    private MaskingUtil() {
    }

    /**
     * 문자 반복 (Java 1.8 호환)
     *
     * @param ch    반복할 문자
     * @param count 반복 횟수
     * @return 반복된 문자열
     */
    public static String repeat(char ch, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * 왼쪽 마스킹 (오른쪽 n자리 보존)
     * <p>예시: maskLeft("ABC12345", 4) → "****2345"</p>
     *
     * @param value        원본 문자열
     * @param visibleRight 오른쪽에서 보존할 문자 수
     * @return 마스킹된 문자열
     */
    public static String maskLeft(String value, int visibleRight) {
        return maskLeft(value, visibleRight, DEFAULT_MASK_CHAR);
    }

    /**
     * 왼쪽 마스킹 (오른쪽 n자리 보존, 마스킹 문자 지정)
     *
     * @param value        원본 문자열
     * @param visibleRight 오른쪽에서 보존할 문자 수
     * @param maskChar     마스킹 문자
     * @return 마스킹된 문자열
     */
    public static String maskLeft(String value, int visibleRight, char maskChar) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        if (value.length() <= visibleRight) {
            return value;
        }

        int maskLength = value.length() - visibleRight;
        return repeat(maskChar, maskLength) +
                value.substring(value.length() - visibleRight);
    }

    /**
     * 오른쪽 마스킹 (왼쪽 n자리 보존)
     * <p>예시: maskRight("ABC12345", 4) → "ABC1****"</p>
     *
     * @param value       원본 문자열
     * @param visibleLeft 왼쪽에서 보존할 문자 수
     * @return 마스킹된 문자열
     */
    public static String maskRight(String value, int visibleLeft) {
        return maskRight(value, visibleLeft, DEFAULT_MASK_CHAR);
    }

    /**
     * 오른쪽 마스킹 (왼쪽 n자리 보존, 마스킹 문자 지정)
     *
     * @param value       원본 문자열
     * @param visibleLeft 왼쪽에서 보존할 문자 수
     * @param maskChar    마스킹 문자
     * @return 마스킹된 문자열
     */
    public static String maskRight(String value, int visibleLeft, char maskChar) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        if (value.length() <= visibleLeft) {
            return value;
        }

        int maskLength = value.length() - visibleLeft;
        return value.substring(0, visibleLeft) +
                repeat(maskChar, maskLength);
    }

    /**
     * 중간 마스킹 (양쪽 n자리씩 보존)
     * <p>예시: maskMiddle("ABC12345", 2, 2) → "AB****45"</p>
     *
     * @param value        원본 문자열
     * @param visibleLeft  왼쪽에서 보존할 문자 수
     * @param visibleRight 오른쪽에서 보존할 문자 수
     * @return 마스킹된 문자열
     */
    public static String maskMiddle(String value, int visibleLeft, int visibleRight) {
        return maskMiddle(value, visibleLeft, visibleRight, DEFAULT_MASK_CHAR);
    }

    /**
     * 중간 마스킹 (양쪽 n자리씩 보존, 마스킹 문자 지정)
     *
     * @param value        원본 문자열
     * @param visibleLeft  왼쪽에서 보존할 문자 수
     * @param visibleRight 오른쪽에서 보존할 문자 수
     * @param maskChar     마스킹 문자
     * @return 마스킹된 문자열
     */
    public static String maskMiddle(String value, int visibleLeft, int visibleRight, char maskChar) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        if (value.length() <= visibleLeft + visibleRight) {
            return value;
        }

        int maskLength = value.length() - visibleLeft - visibleRight;
        return value.substring(0, visibleLeft) +
                repeat(maskChar, maskLength) +
                value.substring(value.length() - visibleRight);
    }

    /**
     * 전체 마스킹
     * <p>예시: maskAll("ABC12345") → "********"</p>
     *
     * @param value 원본 문자열
     * @return 마스킹된 문자열
     */
    public static String maskAll(String value) {
        return maskAll(value, DEFAULT_MASK_CHAR);
    }

    /**
     * 전체 마스킹 (마스킹 문자 지정)
     *
     * @param value    원본 문자열
     * @param maskChar 마스킹 문자
     * @return 마스킹된 문자열
     */
    public static String maskAll(String value, char maskChar) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return repeat(maskChar, value.length());
    }
}
