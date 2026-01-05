package io.github.takoeats.excelannotator.internal.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 파일명 보안 검증 및 정제 유틸리티
 * <p>
 * <b>방어하는 공격 유형:</b>
 * <ul>
 *   <li>Path Traversal: ../, ..\, ., / 등</li>
 *   <li>HTTP Header Injection: CRLF, 제어문자</li>
 *   <li>Command Injection: ; | & $ 등</li>
 *   <li>OS Reserved Names: Windows (CON, PRN 등), Unix (null, stdin 등)</li>
 *   <li>URL Encoding Attack: %2e%2e%2f 등</li>
 * </ul>
 * <p>
 * <b>처리 원칙:</b> 화이트리스트 기반 - 안전한 문자만 명시적으로 허용
 * </p>
 */
public final class FilenameSecurityValidator {

    private static final String DEFAULT_SAFE_FILENAME = "download.xlsx";
    private static final int MAX_FILENAME_LENGTH = 200;

    private static final Pattern SAFE_FILENAME_PATTERN =
            Pattern.compile(
                    "^[a-zA-Z0-9\\-_.()\\[\\] " +
                            "가-힣" +
                            "À-ÖØ-öø-ÿ" +
                            "ぁ-ゔァ-ヴー" +
                            "一-龥" +
                            "]+$"
            );

    private static final Set<String> WINDOWS_RESERVED_NAMES = new HashSet<>(Arrays.asList(
            "CON", "PRN", "AUX", "NUL", "CLOCK$",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    ));

    private static final Set<String> UNIX_SPECIAL_FILENAMES = new HashSet<>(Arrays.asList(
            "NULL", "ZERO", "RANDOM", "URANDOM", "FULL", "STDIN", "STDOUT", "STDERR",
            "TTY", "CONSOLE", "KMSG", "MEM", "KMEM", "PORT"
    ));

    private FilenameSecurityValidator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * 파일명 보안 검증 및 정제
     * <p>위험한 패턴 발견 시 기본 파일명 반환, 그 외에는 안전하게 정제하여 반환</p>
     *
     * @param filename 검증할 파일명
     * @return 안전하게 정제된 파일명 또는 기본 파일명
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return DEFAULT_SAFE_FILENAME;
        }

        String trimmed = filename.trim();

        if (containsDangerousPattern(trimmed)) {
            return DEFAULT_SAFE_FILENAME;
        }

        if (isReservedOrSpecialName(trimmed)) {
            return DEFAULT_SAFE_FILENAME;
        }

        String sanitized = removeUnsafeCharacters(trimmed);

        String normalized = normalizeWhitespace(sanitized);

        if (normalized.length() > MAX_FILENAME_LENGTH) {
            normalized = normalized.substring(0, MAX_FILENAME_LENGTH);
        }

        normalized = normalized.trim();

        if (normalized.isEmpty() || !isSafeFilename(normalized) || !isMeaningfulFilename(normalized)) {
            return DEFAULT_SAFE_FILENAME;
        }

        return normalized;
    }

    /**
     * 위험한 패턴 검사 (Path Traversal, 제어문자, URL 인코딩)
     * <p>
     * 발견 시 즉시 거부하는 패턴들:
     * <ul>
     *   <li>경로 순회: .., /, \, :</li>
     *   <li>숨김 파일: .(으)로 시작</li>
     *   <li>파이프: |</li>
     *   <li>제어문자: 0x00-0x1F, 0x7F</li>
     *   <li>URL 인코딩: %2e, %2f, %5c, %00</li>
     * </ul>
     * </p>
     *
     * @param filename 검사할 파일명
     * @return 위험한 패턴 포함 여부
     */
    static boolean containsDangerousPattern(String filename) {
        if (filename.contains("..") ||
                filename.contains("/") ||
                filename.contains("\\") ||
                filename.startsWith(".") ||
                filename.contains(":") ||
                filename.contains("|")) {
            return true;
        }

        if (filename.matches(".*[\\x00-\\x1F\\x7F].*")) {
            return true;
        }

        String lowerFilename = filename.toLowerCase();
        return lowerFilename.contains("%2e") ||
                lowerFilename.contains("%2f") ||
                lowerFilename.contains("%5c") ||
                lowerFilename.contains("%00");
    }

    /**
     * OS 예약어 및 특수 파일명 검사
     * <p>
     * <b>Windows 예약어:</b> CON, PRN, AUX, NUL, COM1-9, LPT1-9, CLOCK$<br>
     * <b>Unix 특수 파일:</b> null, zero, random, stdin, stdout, stderr 등
     * </p>
     *
     * @param filename 검사할 파일명
     * @return 예약어 또는 특수 파일명 여부
     */
    static boolean isReservedOrSpecialName(String filename) {
        String nameWithoutExt = filename;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            nameWithoutExt = filename.substring(0, dotIndex);
        }

        String upperName = nameWithoutExt.trim().toUpperCase();

        return WINDOWS_RESERVED_NAMES.contains(upperName) ||
                UNIX_SPECIAL_FILENAMES.contains(upperName);
    }

    /**
     * 안전하지 않은 문자 제거
     * <p>
     * <b>허용 문자:</b> a-z, A-Z, 0-9, -, _, ., (, ), [, ], 공백<br>
     * <b>처리:</b> 허용되지 않는 모든 문자를 언더스코어(_)로 치환
     * </p>
     *
     * @param filename 정제할 파일명
     * @return 안전한 문자만 포함된 파일명
     */
    static String removeUnsafeCharacters(String filename) {
        return filename.replaceAll(
                "[^a-zA-Z0-9\\-_.()\\[\\] " +
                        "가-힣" +
                        "À-ÖØ-öø-ÿ" +
                        "ぁ-ゔァ-ヴー" +
                        "一-龥" +
                        "]",
                "_"
        );
    }

    /**
     * 공백 및 언더스코어 정규화
     * <p>연속된 공백이나 언더스코어를 단일 언더스코어로 변경</p>
     *
     * @param filename 정규화할 파일명
     * @return 정규화된 파일명
     */
    static String normalizeWhitespace(String filename) {
        return filename.replaceAll("[\\s_]+", "_");
    }

    /**
     * 최종 안전성 검증
     * <p>화이트리스트 패턴과 완전히 일치하는지 확인</p>
     *
     * @param filename 검증할 파일명
     * @return 안전한 파일명 여부
     */
    static boolean isSafeFilename(String filename) {
        return SAFE_FILENAME_PATTERN.matcher(filename).matches();
    }

    /**
     * 의미 있는 파일명 검증
     * <p>최소 길이 및 영문/숫자 포함 여부 확인</p>
     *
     * @param filename 검증할 파일명
     * @return 의미 있는 파일명 여부
     */
    static boolean isMeaningfulFilename(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        String baseName = (dotIndex > 0)
                ? filename.substring(0, dotIndex)
                : filename;

        if (baseName.isEmpty()) {
            return false;
        }

        return baseName.matches(
                ".*[a-zA-Z0-9" +
                        "가-힣" +
                        "ぁ-ゔァ-ヴー" +
                        "一-龥" +
                        "].*"
        );
    }


    /**
     * 기본 안전 파일명 반환
     *
     * @return 기본 파일명
     */
    public static String getDefaultSafeFilename() {
        return DEFAULT_SAFE_FILENAME;
    }

    /**
     * 최대 파일명 길이 반환
     *
     * @return 최대 길이
     */
    public static int getMaxFilenameLength() {
        return MAX_FILENAME_LENGTH;
    }
}
