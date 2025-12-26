package io.github.takoeats.excelannotator.style.rule;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.style.rule.node.ExpressionNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionParserTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "value < 100",
            "value >= 0",
            "value == 50",
            "value != 0",
            "value between 10 and 100",
            "value between -100 and -10",
            "value equals '완료'",
            "value equals_ignore_case 'COMPLETE'",
            "value contains '진행'",
            "value starts_with '주문'",
            "value ends_with '완료'",
            "value is_negative",
            "value is_positive",
            "value is_zero",
            "value is_null",
            "value is_not_null",
            "value is_empty",
            "value is_not_empty",
            "value > 0 && value < 100",
            "value < 0 || value > 100",
            "value < 0 ^ value > 100",
            "!(value == 0)",
            "value > 0 && value < 50 || value > 100",
            "(value > 0 && value < 50) || (value > 100 && value < 200)",
            "!(value >= 0 && value <= 100)",
            "!(value equals '완료') && !(value contains '진행')",
            "value contains '진행 중'",
            "value < 100.5",
            "value > -50.25",
            "VALUE < 100",
            "  value   <   100  ",
            "value < 0 || value > 100 && value < 200 ^ value == 150",
            "((value > 0 && value < 50) || (value > 100 && value < 150)) && value != 75",
            "value equals '완료' || value equals '승인'",
            "value > 0 && value equals '양수'",
            "!!(value > 0)"
    })
    void parseToTree_validExpressions_returnsNonNullNode(String expression) {
        ExpressionNode node = ExpressionParser.parseToTree(expression);
        assertNotNull(node);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "value <> 100",
            "value matches 'pattern'",
            "value is_invalid",
            "value equals 완료",
            "(value > 0 && value < 100",
            "value between 10"
    })
    void parseToTree_invalidExpressions_throwsException(String expression) {
        assertThrows(ExcelExporterException.class, () -> ExpressionParser.parseToTree(expression));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void parseToTree_emptyOrWhitespace_throwsException(String expression) {
        assertThrows(ExcelExporterException.class, () -> ExpressionParser.parseToTree(expression));
    }

    @Test
    void parseToTree_nullExpression_throwsException() {
        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> ExpressionParser.parseToTree(null)
        );
        assertEquals(ErrorCode.EXPRESSION_PARSE_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("null이거나 빈 문자열일 수 없습니다"));
    }

    @Test
    void parseToTree_unknownOperator_throwsException() {
        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> ExpressionParser.parseToTree("value <> 100")
        );
        assertEquals(ErrorCode.EXPRESSION_PARSE_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("알 수 없는 연산자") ||
                   exception.getMessage().contains("잘못된 조건 형식"));
    }

    @Test
    void parseToTree_unknownStringOperation_throwsException() {
        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> ExpressionParser.parseToTree("value matches 'pattern'")
        );
        assertEquals(ErrorCode.EXPRESSION_PARSE_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("알 수 없는 문자열 연산") ||
                   exception.getMessage().contains("잘못된 조건 형식"));
    }

    @Test
    void parseToTree_unknownSpecialCondition_throwsException() {
        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> ExpressionParser.parseToTree("value is_invalid")
        );
        assertEquals(ErrorCode.EXPRESSION_PARSE_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("알 수 없는 특수 조건") ||
                   exception.getMessage().contains("잘못된 조건 형식"));
    }

    @Test
    void parseToTree_invalidConditionFormat_throwsException() {
        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> ExpressionParser.parseToTree("invalid expression")
        );
        assertEquals(ErrorCode.EXPRESSION_PARSE_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("잘못된 조건 형식"));
    }

    @Test
    void parseToTree_missingClosingParenthesis_throwsException() {
        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> ExpressionParser.parseToTree("(value > 0 && value < 100")
        );
        assertEquals(ErrorCode.EXPRESSION_PARSE_FAILED, exception.getErrorCode());
    }

    @Test
    void parseToTree_incompleteBetween_throwsException() {
        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> ExpressionParser.parseToTree("value between 10")
        );
        assertEquals(ErrorCode.EXPRESSION_PARSE_FAILED, exception.getErrorCode());
    }

    @Test
    void parseToTree_stringWithoutQuotes_throwsException() {
        ExcelExporterException exception = assertThrows(
            ExcelExporterException.class,
            () -> ExpressionParser.parseToTree("value equals 완료")
        );
        assertEquals(ErrorCode.EXPRESSION_PARSE_FAILED, exception.getErrorCode());
    }

    @Test
    void parseToTree_doubleNegationWorks() {
        ExpressionNode node = ExpressionParser.parseToTree("!!(value > 0)");
        assertNotNull(node);
    }

    @Test
    void parseToTree_complexNestedExpression() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "((value > 0 && value < 50) || (value > 100 && value < 150)) && value != 75"
        );
        assertNotNull(node);
    }

    @Test
    void parseToTree_multipleLogicalOperators() {
        ExpressionNode node = ExpressionParser.parseToTree(
            "value < 0 || value > 100 && value < 200 ^ value == 150"
        );
        assertNotNull(node);
    }

    @Test
    void parseToTree_stringWithSpaces() {
        ExpressionNode node = ExpressionParser.parseToTree("value contains '진행 중'");
        assertNotNull(node);
    }

    @Test
    void parseToTree_caseInsensitiveKeywords() {
        ExpressionNode node1 = ExpressionParser.parseToTree("VALUE < 100");
        ExpressionNode node2 = ExpressionParser.parseToTree("value < 100");
        assertNotNull(node1);
        assertNotNull(node2);
    }

    @Test
    void parseToTree_extraWhitespace() {
        ExpressionNode node = ExpressionParser.parseToTree("  value   <   100  ");
        assertNotNull(node);
    }
}
