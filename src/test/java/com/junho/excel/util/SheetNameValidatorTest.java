package com.junho.excel.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.util.SheetNameValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SheetNameValidator 테스트")
class SheetNameValidatorTest {

  @Test
  @DisplayName("유효한 시트 이름은 그대로 반환")
  void validSheetNameShouldReturnAsIs() {
    String validName = "ValidSheet";
    assertEquals(validName, SheetNameValidator.validateAndSanitize(validName));
  }

  @Test
  @DisplayName("앞뒤 공백은 제거")
  void shouldTrimWhitespace() {
    assertEquals("Sheet1", SheetNameValidator.validateAndSanitize("  Sheet1  "));
  }

  @Test
  @DisplayName("null 입력 시 예외 발생")
  void nullInputShouldThrowException() {
    assertThrows(ExcelExporterException.class,
        () -> SheetNameValidator.validateAndSanitize(null));
  }

  @Test
  @DisplayName("빈 문자열 입력 시 예외 발생")
  void emptyStringInputShouldThrowException() {
    assertThrows(ExcelExporterException.class,
        () -> SheetNameValidator.validateAndSanitize(""));
  }

  @Test
  @DisplayName("공백만 있는 문자열 입력 시 예외 발생")
  void whitespaceOnlyInputShouldThrowException() {
    assertThrows(ExcelExporterException.class,
        () -> SheetNameValidator.validateAndSanitize("   "));
  }

  @Test
  @DisplayName("콜론(:)은 제거")
  void colonShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet:Name"));
  }

  @Test
  @DisplayName("백슬래시(\\)는 제거")
  void backslashShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet\\Name"));
  }

  @Test
  @DisplayName("슬래시(/)는 제거")
  void slashShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet/Name"));
  }

  @Test
  @DisplayName("부등호(<>)는 제거")
  void angleBracketsShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet<>Name"));
  }

  @Test
  @DisplayName("따옴표(\")는 제거")
  void doubleQuoteShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet\"Name"));
  }

  @Test
  @DisplayName("물음표(?)는 제거")
  void questionMarkShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet?Name"));
  }

  @Test
  @DisplayName("별표(*)는 제거")
  void asteriskShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet*Name"));
  }

  @Test
  @DisplayName("파이프(|)는 제거")
  void pipeShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet|Name"));
  }

  @Test
  @DisplayName("대괄호([])는 제거")
  void bracketsShouldBeRemoved() {
    assertEquals("Sheet1", SheetNameValidator.validateAndSanitize("Sheet[1]"));
  }

  @Test
  @DisplayName("연속된 점(..)은 제거")
  void consecutiveDotsShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet..Name"));
  }

  @Test
  @DisplayName("제어 문자는 제거")
  void controlCharactersShouldBeRemoved() {
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize("Sheet\u0001Name"));
  }

  @Test
  @DisplayName("모든 금지 문자 제거")
  void allInvalidCharactersShouldBeRemoved() {
    String input = "Sheet:Name\\Test/A<B>C\"D?E*F|G[H]";
    assertEquals("SheetNameTestABCDEFGH", SheetNameValidator.validateAndSanitize(input));
  }

  @Test
  @DisplayName("31자 초과 시 자르기")
  void shouldTruncateTo31Characters() {
    String longName = "ThisIsAVeryLongSheetNameThatExceedsTheMaximumAllowedLength";
    String result = SheetNameValidator.validateAndSanitize(longName);

    assertEquals(31, result.length());
    assertEquals("ThisIsAVeryLongSheetNameThatExc", result);
  }

  @Test
  @DisplayName("30자 이하는 그대로 유지")
  void lessThan31CharactersShouldRemainUnchanged() {
    String name = "ShortSheetName";
    String result = SheetNameValidator.validateAndSanitize(name);
    assertEquals(name, result);
  }

  @Test
  @DisplayName("한글 시트 이름 허용")
  void koreanSheetNameShouldBeAllowed() {
    assertEquals("고객정보", SheetNameValidator.validateAndSanitize("고객정보"));
  }

  @Test
  @DisplayName("유효하지 않은 문자만 포함 시 예외 발생")
  void onlyInvalidCharactersShouldThrowException() {
    assertThrows(ExcelExporterException.class,
        () -> SheetNameValidator.validateAndSanitize(":::***"));
  }

  @Test
  @DisplayName("1자 시트 이름 허용")
  void singleCharacterShouldBeAllowed() {
    assertEquals("A", SheetNameValidator.validateAndSanitize("A"));
  }

  @Test
  @DisplayName("30자 시트 이름 허용")
  void thirtyCharactersShouldBeAllowed() {
    String name = "ThisIsThirtyCharactersLongNow!";
    assertEquals(30, name.length());
    assertEquals(name, SheetNameValidator.validateAndSanitize(name));
  }

  @Test
  @DisplayName("금지 문자 제거 + 길이 자르기")
  void sanitizeAndTruncate() {
    String input = "VeryLongSheet:Name\\With/Invalid<>Characters\"And?More*Text|Here[Test]";
    String result = SheetNameValidator.validateAndSanitize(input);

    assertEquals(31, result.length());
  }

  @Test
  @DisplayName("공백 제거 + 금지 문자 제거")
  void trimAndSanitize() {
    String input = "  Sheet:Name  ";
    assertEquals("SheetName", SheetNameValidator.validateAndSanitize(input));
  }
}
