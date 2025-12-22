package io.github.takoeats.excelannotator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.takoeats.excelannotator.testdto.PersonDTO;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Filename Security 테스트")
class FilenameSecurityTest {

  private static Method encodeFileNameCommonsMethod;

  @BeforeAll
  static void setUp() throws Exception {
    encodeFileNameCommonsMethod = ExcelExporter.class.getDeclaredMethod(
        "encodeFileNameCommons",
        String.class
    );
    encodeFileNameCommonsMethod.setAccessible(true);
  }

  private String encodeFileName(String fileName) throws Exception {
    return (String) encodeFileNameCommonsMethod.invoke(null, fileName);
  }

  @Test
  @DisplayName("null 입력 시 기본 파일명 반환")
  void nullInput_returnsDefaultFileName() throws Exception {
    String result = encodeFileName(null);
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("빈 문자열 입력 시 기본 파일명 반환")
  void emptyString_returnsDefaultFileName() throws Exception {
    String result = encodeFileName("");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("공백만 있는 입력 시 기본 파일명 반환")
  void whitespaceOnly_returnsDefaultFileName() throws Exception {
    String result = encodeFileName("   ");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("Path Traversal 공격 - 상위 디렉토리 접근 차단")
  void pathTraversal_parentDirectory_blockedAndReturnsDefault() throws Exception {
    String result = encodeFileName("../../../etc/passwd");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("Path Traversal 공격 - Windows 경로 차단")
  void pathTraversal_windowsPath_blockedAndReturnsDefault() throws Exception {
    String result = encodeFileName("..\\..\\windows\\system32\\config");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("Path Traversal 공격 - 중간에 .. 포함")
  void pathTraversal_dotDotInMiddle_blockedAndReturnsDefault() throws Exception {
    String result = encodeFileName("test/../../../config.xml");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("Path Traversal 공격 - 슬래시 포함")
  void pathTraversal_forwardSlash_blockedAndReturnsDefault() throws Exception {
    String result = encodeFileName("test/file/path.xlsx");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("Path Traversal 공격 - 백슬래시 포함")
  void pathTraversal_backslash_blockedAndReturnsDefault() throws Exception {
    String result = encodeFileName("test\\file\\path.xlsx");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("HTTP Header Injection - CRLF로 인한 조기 차단")
  void httpHeaderInjection_crlfRemoved() throws Exception {
    String result = encodeFileName("test\r\nContent-Length: 0\r\n");
    assertFalse(result.contains("\r"));
    assertFalse(result.contains("\n"));
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("HTTP Header Injection - 악성 헤더 삽입 시도")
  void httpHeaderInjection_maliciousHeader_sanitized() throws Exception {
    String result = encodeFileName("fileX-Custom:_maliciousX-Evil:_bad");
    assertFalse(result.contains("\r"));
    assertFalse(result.contains("\n"));
  }

  @Test
  @DisplayName("HTTP 메타 문자 - 세미콜론 제거 후 슬래시로 인해 차단")
  void httpMetachar_semicolon_removed() throws Exception {
    String result = encodeFileName("file;rm -rf /");
    assertFalse(result.contains(";"));
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("HTTP 메타 문자 - 등호 제거")
  void httpMetachar_equals_removed() throws Exception {
    String result = encodeFileName("test=value");
    assertFalse(result.contains("="));
    assertEquals("test_value", result);
  }

  @Test
  @DisplayName("HTTP 메타 문자 - 큰따옴표 제거")
  void httpMetachar_doubleQuote_removed() throws Exception {
    String result = encodeFileName("path\"with\"quotes");
    assertFalse(result.contains("\""));
    assertEquals("path_with_quotes", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "file;rm -rf /",
      "test=value",
      "path\"with\"quotes",
      "test\rcarriage",
      "test\nnewline",
      "multi;=\"\r\ncombined"
  })
  @DisplayName("HTTP 메타 문자 - 모든 조합 제거")
  void httpMetachars_allCombinations_completelyRemoved(String input) throws Exception {
    String result = encodeFileName(input);
    assertFalse(result.matches(".*[;=\"\\r\\n].*"),
        "Result should not contain HTTP metacharacters: " + result);
  }

  @Test
  @DisplayName("제어 문자 - NULL 바이트 제거")
  void controlCharacter_nullByte_removed() throws Exception {
    String result = encodeFileName("test\u0000.xlsx");
    assertFalse(result.contains("\u0000"));
  }

  @Test
  @DisplayName("제어 문자 - 0x00~0x1F 범위 제거")
  void controlCharacters_range00to1F_removed() throws Exception {
    String input = "test\u0001\u0002\u001F.xlsx";
    String result = encodeFileName(input);
    assertFalse(result.matches(".*[\\x00-\\x1F].*"));
  }

  @Test
  @DisplayName("제어 문자 - DEL(0x7F) 제거")
  void controlCharacter_del_removed() throws Exception {
    String result = encodeFileName("file\u007Ftest.xlsx");
    assertFalse(result.contains("\u007F"));
  }

  @Test
  @DisplayName("제어 문자 - 복합 제어 문자 제거")
  void controlCharacters_multiple_allRemoved() throws Exception {
    String input = "test\u0000\u0001\u001F\u007F.xlsx";
    String result = encodeFileName(input);
    assertFalse(result.matches(".*[\\x00-\\x1F\\x7F].*"));
  }

  @Test
  @DisplayName("길이 제한 - 200자 이하 유지")
  void lengthLimit_under200_unchanged() throws Exception {
    String input = StringUtils.repeat('A', 100) + ".xlsx";
    String result = encodeFileName(input);
    assertEquals(input, result);
  }

  @Test
  @DisplayName("길이 제한 - 정확히 200자는 유지")
  void lengthLimit_exactly200_unchanged() throws Exception {
    String input = StringUtils.repeat('A', 200);
    String result = encodeFileName(input);
    assertEquals(200, result.length());
    assertEquals(input, result);
  }

  @Test
  @DisplayName("길이 제한 - 201자 이상은 200자로 절단")
  void lengthLimit_over200_truncatedTo200() throws Exception {
    String input = StringUtils.repeat('A', 201) + ".xlsx";
    String result = encodeFileName(input);
    assertTrue(result.length() <= 200);
    assertEquals(200, result.length());
  }

  @Test
  @DisplayName("길이 제한 - 매우 긴 파일명 절단")
  void lengthLimit_veryLongName_truncated() throws Exception {
    String input = StringUtils.repeat('A', 500) + ".xlsx";
    String result = encodeFileName(input);
    assertEquals(200, result.length());
  }

  @Test
  @DisplayName("ASCII 외 문자 - 이모지를 언더스코어로 치환")
  void nonAscii_emoji_replacedWithUnderscore() throws Exception {
    String result = encodeFileName("test😀file.xlsx");
    assertFalse(result.contains("😀"));
    assertTrue(result.contains("_"));
  }

  @Test
  @DisplayName("연속 공백/언더스코어 - 단일 언더스코어로 정규화")
  void consecutiveSpacesAndUnderscores_normalized() throws Exception {
    String result = encodeFileName("test   ___  file.xlsx");
    assertEquals("test_file.xlsx", result);
  }

  @Test
  @DisplayName("연속 공백/언더스코어 - 탭은 제어문자로 조기 차단")
  void consecutiveWhitespace_withTabs_normalized() throws Exception {
    String result = encodeFileName("test\t\t   file.xlsx");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("연속 공백/언더스코어 - 여러 언더스코어")
  void consecutiveUnderscores_normalized() throws Exception {
    String result = encodeFileName("test______file.xlsx");
    assertEquals("test_file.xlsx", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "valid_file_name.xlsx",
      "Report_2024-01-15.xlsx",
      "data[1].xlsx",
      "한글파일명.xlsx",
      "日本語ファイル名.xlsx",
      "中文文件名.xlsx"
  })
  @DisplayName("유효한 파일명 - 그대로 유지")
  void validFilename_unchanged(String validName) throws Exception {
    String result = encodeFileName(validName);
    assertEquals(validName, result);
  }

  @Test
  @DisplayName("유효한 파일명 - 영문, 숫자, 특수문자")
  void validFilename_alphanumericAndSpecialChars_unchanged() throws Exception {
    String validName = "Report_2024-01-15_v1.0.xlsx";
    String result = encodeFileName(validName);
    assertEquals(validName, result);
  }

  @Test
  @DisplayName("복합 공격 - Path Traversal + HTTP Injection")
  void combinedAttack_pathTraversalAndHttpInjection_blocked() throws Exception {
    String result = encodeFileName("../test\r\nX-Header: evil");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("복합 공격 - 제어문자 + 메타문자")
  void combinedAttack_controlAndMetachars_sanitized() throws Exception {
    String input = "test\u0000;=\"\r\n.xlsx";
    String result = encodeFileName(input);
    assertFalse(result.matches(".*[\\x00-\\x1F;=\"\\r\\n].*"));
  }

  @Test
  @DisplayName("복합 공격 - 모든 보안 위협 조합")
  void combinedAttack_allThreats_handledCorrectly() throws Exception {
    String input = "../evil\u0000file;rm=test\"\r\n한글.xlsx";
    String result = encodeFileName(input);
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("엣지 케이스 - 제어문자만으로 구성")
  void edgeCase_onlyControlCharacters_returnsDefault() throws Exception {
    String input = "\u0000\u0001\u0002\r\n";
    String result = encodeFileName(input);
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("엣지 케이스 - HTTP 메타문자만으로 구성")
  void edgeCase_onlyHttpMetachars_returnsDefault() throws Exception {
    String input = ";=\"\r\n";
    String result = encodeFileName(input);
    assertEquals("download.xlsx", result);
  }



  @Test
  @DisplayName("엣지 케이스 - 허용 ASCII 외 문자만으로 구성")
  void edgeCase_onlyNonAscii_replacedWithUnderscores() throws Exception {
    //힌디어 파일명
    String input = "हिंदी_फ़ाइल.xlsx";
    String result = encodeFileName(input);
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("실제 사용 사례 - 정상 파일명")
  void realUseCase_normalFilename() throws Exception {
    String result = encodeFileName("customer_report_2024.xlsx");
    assertEquals("customer_report_2024.xlsx", result);
  }

  @Test
  @DisplayName("실제 사용 사례 - 날짜 포함 파일명")
  void realUseCase_filenameWithDate() throws Exception {
    String result = encodeFileName("sales_2024-01-15.xlsx");
    assertEquals("sales_2024-01-15.xlsx", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "%2e%2e%2f",
      "%2E%2E%2F",
      "file%2fpath.xlsx",
      "test%5cfile.xlsx",
      "file%00.xlsx"
  })
  @DisplayName("URL 인코딩 공격 - 기본 파일명 반환")
  void urlEncodingAttack_returnsDefault(String encodedFilename) throws Exception {
    String result = encodeFileName(encodedFilename);
    assertEquals("download.xlsx", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "CON.xlsx",
      "PRN.txt",
      "AUX.xlsx",
      "NUL.xlsx",
      "COM1.xlsx",
      "COM9.xlsx",
      "LPT1.xlsx",
      "LPT9.xlsx",
      "con.xlsx",
      "prn.txt"
  })
  @DisplayName("Windows 예약어 - 기본 파일명 반환")
  void windowsReservedNames_returnsDefault(String reservedName) throws Exception {
    String result = encodeFileName(reservedName);
    assertEquals("download.xlsx", result);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "NULL.xlsx",
      "ZERO.xlsx",
      "RANDOM.xlsx",
      "STDIN.xlsx",
      "STDOUT.xlsx",
      "STDERR.xlsx",
      "null.xlsx",
      "stdin.xlsx"
  })
  @DisplayName("Unix 특수 파일명 - 기본 파일명 반환")
  void unixSpecialFilenames_returnsDefault(String specialName) throws Exception {
    String result = encodeFileName(specialName);
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("숨김 파일(.으로 시작) - 기본 파일명 반환")
  void hiddenFile_returnsDefault() throws Exception {
    String result = encodeFileName(".hidden_file.xlsx");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("파이프 문자 포함 - 기본 파일명 반환")
  void pipeCharacter_returnsDefault() throws Exception {
    String result = encodeFileName("file|name.xlsx");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("콜론 문자 포함 - 기본 파일명 반환")
  void colonCharacter_returnsDefault() throws Exception {
    String result = encodeFileName("file:name.xlsx");
    assertEquals("download.xlsx", result);
  }

  @Test
  @DisplayName("통합 테스트 - ExcelExporter API를 통한 보안 검증")
  void integration_excelExporterApi_filenameSanitized() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String maliciousName = "../../../etc/passwd";

    String returnedName = ExcelExporter.excelFromList(
        baos,
        maliciousName,
        Collections.singletonList(new PersonDTO("Test", 30, new BigDecimal("1000")))
    );

    assertNotNull(returnedName);
    assertTrue(returnedName.startsWith("download_") || returnedName.equals("download.xlsx"));
    assertTrue(returnedName.endsWith(".xlsx"));
  }

  @Test
  @DisplayName("통합 테스트 - HTTP Injection 시도 차단")
  void integration_httpInjectionAttempt_blocked() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String maliciousName = "testX-Custom:_evil";

    String returnedName = ExcelExporter.excelFromList(
        baos,
        maliciousName,
        Collections.singletonList(new PersonDTO("Test", 30, new BigDecimal("1000")))
    );

    assertNotNull(returnedName);
    assertFalse(returnedName.contains("\r"));
    assertFalse(returnedName.contains("\n"));
  }

  @Test
  @DisplayName("통합 테스트 - 길이 제한 적용")
  void integration_lengthLimit_enforced() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String longName = StringUtils.repeat('A', 300) + ".xlsx";

    String returnedName = ExcelExporter.excelFromList(
        baos,
        longName,
        Collections.singletonList(new PersonDTO("Test", 30, new BigDecimal("1000")))
    );

    assertNotNull(returnedName);
    assertTrue(returnedName.length() <= 230);
  }
}
