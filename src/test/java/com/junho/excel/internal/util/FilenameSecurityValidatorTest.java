package com.junho.excel.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("FilenameSecurityValidator 단위 테스트")
class FilenameSecurityValidatorTest {

  @Test
  @DisplayName("유효한 파일명은 그대로 반환")
  void sanitizeFilename_validName_returnsAsIs() {
    assertEquals("valid_file.xlsx", FilenameSecurityValidator.sanitizeFilename("valid_file.xlsx"));
    assertEquals("Report_2024.xlsx",
        FilenameSecurityValidator.sanitizeFilename("Report_2024.xlsx"));
    assertEquals("data-export.xlsx",
        FilenameSecurityValidator.sanitizeFilename("data-export.xlsx"));
  }

  @Test
  @DisplayName("null 또는 빈 문자열은 기본 파일명 반환")
  void sanitizeFilename_nullOrEmpty_returnsDefault() {
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename(null));
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename(""));
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename("   "));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "../../../etc/passwd",
      "..\\..\\windows\\system32",
      "test/../config.xml",
      "./file.xlsx",
      "file/path.xlsx",
      "file\\path.xlsx"
  })
  @DisplayName("Path Traversal 패턴은 기본 파일명 반환")
  void sanitizeFilename_pathTraversal_returnsDefault(String maliciousName) {
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename(maliciousName));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "CON.xlsx", "PRN.txt", "AUX.xlsx", "NUL.xlsx",
      "COM1.xlsx", "COM9.xlsx", "LPT1.xlsx", "LPT9.xlsx",
      "con.xlsx", "prn.txt"
  })
  @DisplayName("Windows 예약어는 기본 파일명 반환")
  void sanitizeFilename_windowsReserved_returnsDefault(String reservedName) {
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename(reservedName));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NULL.xlsx", "ZERO.xlsx", "RANDOM.xlsx", "STDIN.xlsx",
      "STDOUT.xlsx", "STDERR.xlsx", "null.xlsx", "stdin.xlsx"
  })
  @DisplayName("Unix 특수 파일명은 기본 파일명 반환")
  void sanitizeFilename_unixSpecial_returnsDefault(String specialName) {
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename(specialName));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "%2e%2e%2f",
      "%2E%2E%2F",
      "file%2fpath",
      "test%5cfile",
      "file%00.xlsx"
  })
  @DisplayName("URL 인코딩 공격은 기본 파일명 반환")
  void sanitizeFilename_urlEncoded_returnsDefault(String encodedName) {
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename(encodedName));
  }

  @Test
  @DisplayName("제어문자는 기본 파일명 반환")
  void sanitizeFilename_controlChars_returnsDefault() {
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename("file\u0000.xlsx"));
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename("file\u001F.xlsx"));
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename("file\u007F.xlsx"));
  }

  @Test
  @DisplayName("안전하지 않은 문자는 언더스코어로 치환")
  void sanitizeFilename_unsafeChars_replacedWithUnderscore() {
    assertEquals("file_name.xlsx", FilenameSecurityValidator.sanitizeFilename("file;name.xlsx"));
    assertEquals("file_name.xlsx", FilenameSecurityValidator.sanitizeFilename("file=name.xlsx"));
    assertEquals("file_name.xlsx", FilenameSecurityValidator.sanitizeFilename("file\"name.xlsx"));
    assertEquals("test_file.xlsx", FilenameSecurityValidator.sanitizeFilename("test@file.xlsx"));
  }

  @Test
  @DisplayName("힌디어 등 비ASCII 문자는 언더스코어로 치환")
  void sanitizeFilename_nonAscii_replacedWithUnderscore() {
    String result = FilenameSecurityValidator.sanitizeFilename("हिंदीफ़ाइलabc.xlsx");
    assertTrue(result.contains("_abc"));
    assertFalse(result.contains("हिंदी"));
  }

  @Test
  @DisplayName("연속된 공백과 언더스코어는 단일 언더스코어로 정규화")
  void sanitizeFilename_consecutiveSpaces_normalized() {
    assertEquals("test_file.xlsx", FilenameSecurityValidator.sanitizeFilename("test   file.xlsx"));
    assertEquals("test_file.xlsx", FilenameSecurityValidator.sanitizeFilename("test___file.xlsx"));
    assertEquals("test_file.xlsx",
        FilenameSecurityValidator.sanitizeFilename("test _ _ file.xlsx"));
  }

  @Test
  @DisplayName("200자 초과는 200자로 절단")
  void sanitizeFilename_tooLong_truncated() {
    String longName =
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
            + ".xlsx";
    String result = FilenameSecurityValidator.sanitizeFilename(longName);
    assertEquals(200, result.length());
  }

  @Test
  @DisplayName("정제 후 빈 문자열이 되면 기본 파일명 반환")
  void sanitizeFilename_becomesEmpty_returnsDefault() {
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename(";;;==="));
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename("Русское имя файла"));
  }

  @Test
  @DisplayName("containsDangerousPattern - 위험한 패턴 감지")
  void containsDangerousPattern_detectsThreats() {
    assertTrue(FilenameSecurityValidator.containsDangerousPattern("../file"));
    assertTrue(FilenameSecurityValidator.containsDangerousPattern("file/path"));
    assertTrue(FilenameSecurityValidator.containsDangerousPattern("file\\path"));
    assertTrue(FilenameSecurityValidator.containsDangerousPattern(".hidden"));
    assertTrue(FilenameSecurityValidator.containsDangerousPattern("file:name"));
    assertTrue(FilenameSecurityValidator.containsDangerousPattern("file|name"));
    assertTrue(FilenameSecurityValidator.containsDangerousPattern("file\u0000"));
    assertTrue(FilenameSecurityValidator.containsDangerousPattern("%2e%2e%2f"));

    assertFalse(FilenameSecurityValidator.containsDangerousPattern("safe_file.xlsx"));
  }

  @Test
  @DisplayName("isReservedOrSpecialName - Windows 예약어 감지")
  void isReservedOrSpecialName_detectsWindowsReserved() {
    assertTrue(FilenameSecurityValidator.isReservedOrSpecialName("CON.xlsx"));
    assertTrue(FilenameSecurityValidator.isReservedOrSpecialName("PRN"));
    assertTrue(FilenameSecurityValidator.isReservedOrSpecialName("COM1.txt"));
    assertTrue(FilenameSecurityValidator.isReservedOrSpecialName("con.xlsx"));

    assertFalse(FilenameSecurityValidator.isReservedOrSpecialName("CONFIG.xlsx"));
  }

  @Test
  @DisplayName("isReservedOrSpecialName - Unix 특수 파일명 감지")
  void isReservedOrSpecialName_detectsUnixSpecial() {
    assertTrue(FilenameSecurityValidator.isReservedOrSpecialName("NULL.xlsx"));
    assertTrue(FilenameSecurityValidator.isReservedOrSpecialName("STDIN"));
    assertTrue(FilenameSecurityValidator.isReservedOrSpecialName("stdout.txt"));

    assertFalse(FilenameSecurityValidator.isReservedOrSpecialName("nullish.xlsx"));
  }

  @Test
  @DisplayName("removeUnsafeCharacters - 안전하지 않은 문자 제거")
  void removeUnsafeCharacters_removesUnsafe() {
    assertEquals("test_file.xlsx",
        FilenameSecurityValidator.removeUnsafeCharacters("test@file.xlsx"));
    assertEquals("file_name_test.xlsx",
        FilenameSecurityValidator.removeUnsafeCharacters("file#name$test.xlsx"));
    assertEquals("safe-file_name.xlsx",
        FilenameSecurityValidator.removeUnsafeCharacters("safe-file_name.xlsx"));
  }

  @Test
  @DisplayName("normalizeWhitespace - 공백 정규화")
  void normalizeWhitespace_normalizes() {
    assertEquals("test_file", FilenameSecurityValidator.normalizeWhitespace("test   file"));
    assertEquals("test_file", FilenameSecurityValidator.normalizeWhitespace("test___file"));
    assertEquals("test_file", FilenameSecurityValidator.normalizeWhitespace("test _ _ file"));
  }

  @Test
  @DisplayName("isSafeFilename - 안전한 파일명 검증")
  void isSafeFilename_validates() {
    assertTrue(FilenameSecurityValidator.isSafeFilename("safe-file_name.xlsx"));
    assertTrue(FilenameSecurityValidator.isSafeFilename("Report_2024-01-15.xlsx"));
    assertTrue(FilenameSecurityValidator.isSafeFilename("data[1].xlsx"));
    assertTrue(FilenameSecurityValidator.isSafeFilename("한글파일명.xlsx"));
    assertTrue(FilenameSecurityValidator.isSafeFilename("日本語ファイル名.xlsx"));
    assertTrue(FilenameSecurityValidator.isSafeFilename("中文文件名.xlsx"));

    assertFalse(FilenameSecurityValidator.isSafeFilename("file@test.xlsx"));
    assertFalse(FilenameSecurityValidator.isSafeFilename("file#name.xlsx"));
    assertFalse(FilenameSecurityValidator.isSafeFilename("../file.xlsx"));
    assertFalse(FilenameSecurityValidator.isSafeFilename("한글@파일명.xlsx"));
    assertFalse(FilenameSecurityValidator.isSafeFilename("日本語#ファイル名.xlsx"));
    assertFalse(FilenameSecurityValidator.isSafeFilename("中文$文件名.xlsx"));
  }


  @Test
  @DisplayName("isMeaningfulFilename - 의미 있는 파일명 검증")
  void isMeaningfulFilename_validates() {
    assertTrue(FilenameSecurityValidator.isMeaningfulFilename("file.xlsx"));
    assertTrue(FilenameSecurityValidator.isMeaningfulFilename("a1"));
    assertTrue(FilenameSecurityValidator.isMeaningfulFilename("1a"));

    assertFalse(FilenameSecurityValidator.isMeaningfulFilename("_"));
    assertFalse(FilenameSecurityValidator.isMeaningfulFilename(""));
    assertFalse(FilenameSecurityValidator.isMeaningfulFilename("---"));
  }

  @Test
  @DisplayName("getDefaultSafeFilename - 기본 파일명 반환")
  void getDefaultSafeFilename_returnsDefault() {
    assertEquals("download.xlsx", FilenameSecurityValidator.getDefaultSafeFilename());
  }

  @Test
  @DisplayName("getMaxFilenameLength - 최대 길이 반환")
  void getMaxFilenameLength_returnsMax() {
    assertEquals(200, FilenameSecurityValidator.getMaxFilenameLength());
  }

  @Test
  @DisplayName("유틸리티 클래스 - 인스턴스화 불가")
  void utilityClass_cannotBeInstantiated() {
    try {
      java.lang.reflect.Constructor<FilenameSecurityValidator> constructor =
          FilenameSecurityValidator.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
      org.junit.jupiter.api.Assertions.fail("Should have thrown AssertionError");
    } catch (java.lang.reflect.InvocationTargetException e) {
      assertTrue(e.getCause() instanceof AssertionError);
      assertTrue(e
          .getCause()
          .getMessage()
          .contains("Utility class cannot be instantiated"));
    } catch (Exception e) {
      org.junit.jupiter.api.Assertions.fail("Unexpected exception: " + e
          .getClass()
          .getName());
    }
  }

  @Test
  @DisplayName("복합 공격 시나리오 - 모두 차단")
  void sanitizeFilename_combinedAttacks_allBlocked() {
    assertEquals("download.xlsx",
        FilenameSecurityValidator.sanitizeFilename("../evil\u0000;rm=test"));
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename("%2e%2e/etc/passwd"));
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename("CON"));
    assertEquals("download.xlsx", FilenameSecurityValidator.sanitizeFilename("NULL"));
  }

  @Test
  @DisplayName("실제 사용 케이스 - 정상 파일명 처리")
  void sanitizeFilename_realWorldCases_handledCorrectly() {
    assertEquals("customer_report_2024.xlsx",
        FilenameSecurityValidator.sanitizeFilename("customer_report_2024.xlsx"));

    assertEquals("sales_2024-01-15.xlsx",
        FilenameSecurityValidator.sanitizeFilename("sales_2024-01-15.xlsx"));

    assertEquals("Report_Q1_Final.xlsx",
        FilenameSecurityValidator.sanitizeFilename("Report_Q1_Final.xlsx"));

    assertEquals("data_export_v1.0.xlsx",
        FilenameSecurityValidator.sanitizeFilename("data_export_v1.0.xlsx"));
  }
}
