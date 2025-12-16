package com.junho.excel.style.rule;

import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.style.rule.node.ExpressionNode;
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
}
