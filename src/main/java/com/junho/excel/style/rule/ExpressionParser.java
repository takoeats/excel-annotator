package com.junho.excel.style.rule;

import com.junho.excel.style.rule.node.BinaryOpNode;
import com.junho.excel.style.rule.node.ExpressionNode;
import com.junho.excel.style.rule.node.LeafNode;
import com.junho.excel.style.rule.node.UnaryOpNode;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 조건 표현식 파서 (재귀 하강 파서)
 * <p>논리 연산자를 포함한 복잡한 표현식을 파싱하여 트리 구조로 변환합니다.</p>
 *
 * <h3>지원하는 표현식</h3>
 * <ul>
 *     <li>숫자 비교: {@code value < 100}, {@code value >= 0}, {@code value between 10 and 100}</li>
 *     <li>문자열 비교: {@code value equals '완료'}, {@code value contains '진행'}</li>
 *     <li>특수 조건: {@code value is_negative}, {@code value is_null}, {@code value is_empty}</li>
 *     <li>논리 연산자: {@code &&} (AND), {@code ||} (OR), {@code ^} (XOR), {@code !} (NOT)</li>
 *     <li>괄호: {@code (value > 0 && value < 100) || value == 0}</li>
 * </ul>
 */
@Slf4j
public class ExpressionParser {

    // 숫자 비교: value < 100
    private static final Pattern NUMBER_COMPARE_PATTERN =
            Pattern.compile("^value\\s*([<>]=?|[!=]=)\\s*(-?\\d+\\.?\\d*)$");

    // between: value between 10 and 100
    private static final Pattern BETWEEN_PATTERN =
            Pattern.compile("^value\\s+between\\s+(-?\\d+\\.?\\d*)\\s+and\\s+(-?\\d+\\.?\\d*)$");

    // 문자열 비교: value equals 'text'
    private static final Pattern STRING_COMPARE_PATTERN =
            Pattern.compile("^value\\s+(equals|equals_ignore_case|contains|starts_with|ends_with)\\s+'([^']*)'$");

    // 특수 조건: value is_negative
    private static final Pattern SPECIAL_PATTERN =
            Pattern.compile("^value\\s+(is_negative|is_positive|is_zero|is_null|is_not_null|is_empty|is_not_empty)$");

    private ExpressionParser() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * 표현식 문자열을 파싱하여 트리 구조로 변환합니다.
     *
     * @param expression 표현식 문자열
     * @return 파싱된 표현식 트리
     * @throws com.junho.excel.exception.ExcelExporterException 파싱 실패 시
     */
    public static ExpressionNode parseToTree(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new com.junho.excel.exception.ExcelExporterException(
                    com.junho.excel.exception.ErrorCode.EXPRESSION_PARSE_FAILED,
                    "표현식은 null이거나 빈 문자열일 수 없습니다"
            );
        }

        try {
            String normalized = expression.trim();
            Tokenizer tokenizer = new Tokenizer(normalized);
            return parseOrExpression(tokenizer);
        } catch (com.junho.excel.exception.ExcelExporterException e) {
            throw e;
        } catch (Exception e) {
            throw new com.junho.excel.exception.ExcelExporterException(
                    com.junho.excel.exception.ErrorCode.EXPRESSION_PARSE_FAILED,
                    "표현식 파싱 실패: " + expression,
                    e
            );
        }
    }

    /**
     * OR 표현식 파싱 (가장 낮은 우선순위)
     * <p>형식: {@code andExpr ( '||' andExpr )*}</p>
     */
    private static ExpressionNode parseOrExpression(Tokenizer tokenizer) {
        ExpressionNode left = parseXorExpression(tokenizer);

        while (tokenizer.peek() != null && Objects
                .requireNonNull(tokenizer.peek())
                .equals("||")) {
            tokenizer.consume("||");
            ExpressionNode right = parseXorExpression(tokenizer);
            left = new BinaryOpNode(ExpressionNode.LogicalOperator.OR, left, right);
        }

        return left;
    }

    /**
     * XOR 표현식 파싱
     * <p>형식: {@code andExpr ( '^' andExpr )*}</p>
     */
    private static ExpressionNode parseXorExpression(Tokenizer tokenizer) {
        ExpressionNode left = parseAndExpression(tokenizer);

        while (tokenizer.peek() != null && Objects
                .requireNonNull(tokenizer.peek())
                .equals("^")) {
            tokenizer.consume("^");
            ExpressionNode right = parseAndExpression(tokenizer);
            left = new BinaryOpNode(ExpressionNode.LogicalOperator.XOR, left, right);
        }

        return left;
    }

    /**
     * AND 표현식 파싱
     * <p>형식: {@code unaryExpr ( '&&' unaryExpr )*}</p>
     */
    private static ExpressionNode parseAndExpression(Tokenizer tokenizer) {
        ExpressionNode left = parseUnaryExpression(tokenizer);

        while (tokenizer.peek() != null && Objects
                .requireNonNull(tokenizer.peek())
                .equals("&&")) {
            tokenizer.consume("&&");
            ExpressionNode right = parseUnaryExpression(tokenizer);
            left = new BinaryOpNode(ExpressionNode.LogicalOperator.AND, left, right);
        }

        return left;
    }

    /**
     * 단항 표현식 파싱 (NOT)
     * <p>형식: {@code '!' unaryExpr | primaryExpr}</p>
     */
    private static ExpressionNode parseUnaryExpression(Tokenizer tokenizer) {
        if (tokenizer.peek() != null && Objects
                .requireNonNull(tokenizer.peek())
                .equals("!")) {
            tokenizer.consume("!");
            ExpressionNode operand = parseUnaryExpression(tokenizer);
            return new UnaryOpNode(ExpressionNode.LogicalOperator.NOT, operand);
        }

        return parsePrimaryExpression(tokenizer);
    }

    /**
     * 기본 표현식 파싱 (괄호 또는 단일 조건)
     * <p>형식: {@code '(' orExpr ')' | condition}</p>
     */
    private static ExpressionNode parsePrimaryExpression(Tokenizer tokenizer) {
        if (tokenizer.peek() != null && Objects
                .requireNonNull(tokenizer.peek())
                .equals("(")) {
            tokenizer.consume("(");
            ExpressionNode expr = parseOrExpression(tokenizer);
            tokenizer.consume(")");
            return expr;
        }

        return parseCondition(tokenizer);
    }

    /**
     * 단일 조건 파싱
     */
    private static ExpressionNode parseCondition(Tokenizer tokenizer) {
        String condition = tokenizer.consumeCondition();
        ParsedExpression parsed = parseSingleCondition(condition);
        return new LeafNode(parsed);
    }

    /**
     * 단일 조건 문자열 파싱 (기존 로직)
     */
    private static ParsedExpression parseSingleCondition(String condition) {
        String normalized = condition.trim().toLowerCase();

        // 1. 숫자 비교
        Matcher numberMatcher = NUMBER_COMPARE_PATTERN.matcher(normalized);
        if (numberMatcher.matches()) {
            return parseNumberComparison(numberMatcher);
        }

        // 2. between
        Matcher betweenMatcher = BETWEEN_PATTERN.matcher(normalized);
        if (betweenMatcher.matches()) {
            return parseBetween(betweenMatcher);
        }

        // 3. 문자열 비교
        Matcher stringMatcher = STRING_COMPARE_PATTERN.matcher(normalized);
        if (stringMatcher.matches()) {
            return parseStringComparison(stringMatcher);
        }

        // 4. 특수 조건
        Matcher specialMatcher = SPECIAL_PATTERN.matcher(normalized);
        if (specialMatcher.matches()) {
            return parseSpecial(specialMatcher);
        }

        throw new com.junho.excel.exception.ExcelExporterException(
                com.junho.excel.exception.ErrorCode.EXPRESSION_PARSE_FAILED,
                "잘못된 조건 형식: " + condition
        );
    }

    private static ParsedExpression parseNumberComparison(Matcher matcher) {
        String operator = matcher.group(1);
        double value = Double.parseDouble(matcher.group(2));

        ExpressionType type = mapOperatorToType(operator);
        return ParsedExpression.builder()
                .type(type)
                .numberValue(value)
                .build();
    }

    private static ParsedExpression parseBetween(Matcher matcher) {
        double min = Double.parseDouble(matcher.group(1));
        double max = Double.parseDouble(matcher.group(2));

        return ParsedExpression.builder()
                .type(ExpressionType.BETWEEN)
                .numberValue(min)
                .numberValue2(max)
                .build();
    }

    private static ParsedExpression parseStringComparison(Matcher matcher) {
        String operation = matcher.group(1);
        String value = matcher.group(2);

        ExpressionType type;
        switch (operation) {
            case "equals":
                type = ExpressionType.STRING_EQUALS;
                break;
            case "equals_ignore_case":
                type = ExpressionType.STRING_EQUALS_IGNORE_CASE;
                break;
            case "contains":
                type = ExpressionType.STRING_CONTAINS;
                break;
            case "starts_with":
                type = ExpressionType.STRING_STARTS_WITH;
                break;
            case "ends_with":
                type = ExpressionType.STRING_ENDS_WITH;
                break;
            default:
                throw new com.junho.excel.exception.ExcelExporterException(
                        com.junho.excel.exception.ErrorCode.EXPRESSION_PARSE_FAILED,
                        "알 수 없는 문자열 연산: " + operation
                );
        }

        return ParsedExpression.builder()
                .type(type)
                .stringValue(value)
                .build();
    }

    private static ParsedExpression parseSpecial(Matcher matcher) {
        String condition = matcher.group(1);

        ExpressionType type;
        switch (condition) {
            case "is_negative":
                type = ExpressionType.IS_NEGATIVE;
                break;
            case "is_positive":
                type = ExpressionType.IS_POSITIVE;
                break;
            case "is_zero":
                type = ExpressionType.IS_ZERO;
                break;
            case "is_null":
                type = ExpressionType.IS_NULL;
                break;
            case "is_not_null":
                type = ExpressionType.IS_NOT_NULL;
                break;
            case "is_empty":
                type = ExpressionType.IS_EMPTY;
                break;
            case "is_not_empty":
                type = ExpressionType.IS_NOT_EMPTY;
                break;
            default:
                throw new com.junho.excel.exception.ExcelExporterException(
                        com.junho.excel.exception.ErrorCode.EXPRESSION_PARSE_FAILED,
                        "알 수 없는 특수 조건: " + condition
                );
        }

        return ParsedExpression.builder()
                .type(type)
                .build();
    }

    private static ExpressionType mapOperatorToType(String operator) {
        switch (operator) {
            case "<":
                return ExpressionType.LESS_THAN;
            case "<=":
                return ExpressionType.LESS_THAN_OR_EQUAL;
            case ">":
                return ExpressionType.GREATER_THAN;
            case ">=":
                return ExpressionType.GREATER_THAN_OR_EQUAL;
            case "==":
                return ExpressionType.EQUALS;
            case "!=":
                return ExpressionType.NOT_EQUALS;
            default:
                throw new com.junho.excel.exception.ExcelExporterException(
                        com.junho.excel.exception.ErrorCode.EXPRESSION_PARSE_FAILED,
                        "알 수 없는 연산자: " + operator
                );
        }
    }

    /**
     * 토크나이저 (표현식을 토큰 단위로 분리)
     */
    private static class Tokenizer {
        private final String input;
        private int position = 0;

        Tokenizer(String input) {
            this.input = input;
            skipWhitespace();
        }

        String peek() {
            if (position >= input.length()) {
                return null;
            }

            // 연산자 확인
            if (position + 1 < input.length()) {
                String twoChar = input.substring(position, position + 2);
                if (twoChar.equals("&&") || twoChar.equals("||")) {
                    return twoChar;
                }
            }

            char ch = input.charAt(position);
            if (ch == '!' || ch == '(' || ch == ')' || ch == '^') {
                return String.valueOf(ch);
            }

            return null;
        }

        void consume(String expected) {
            String actual = peek();
            if (actual == null || !actual.equals(expected)) {
                throw new com.junho.excel.exception.ExcelExporterException(
                        com.junho.excel.exception.ErrorCode.EXPRESSION_PARSE_FAILED,
                        "'" + expected + "'을(를) 기대했지만 '" + actual + "'을(를) 받았습니다"
                );
            }

            position += expected.length();
            skipWhitespace();
        }

        String consumeCondition() {
            int start = position;
            int parenCount = 0;

            while (position < input.length()) {
                char ch = input.charAt(position);

                // 괄호 카운트
                if (ch == '(') parenCount++;
                if (ch == ')') {
                    if (parenCount == 0) break;
                    parenCount--;
                }

                // 논리 연산자 확인 (괄호 밖에서만)
                if (parenCount == 0) {
                    if (position + 1 < input.length()) {
                        String twoChar = input.substring(position, position + 2);
                        if (twoChar.equals("&&") || twoChar.equals("||")) {
                            break;
                        }
                    }
                    if (ch == '^') break;
                }

                position++;
            }

            String condition = input.substring(start, position).trim();
            skipWhitespace();
            return condition;
        }

        private void skipWhitespace() {
            while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
                position++;
            }
        }
    }

    /**
     * 파싱된 단일 조건 표현식
     */
    @Getter
    @Builder
    public static class ParsedExpression {
        private final ExpressionType type;
        private final double numberValue;
        private final double numberValue2;  // for BETWEEN
        private final String stringValue;
    }

    /**
     * 표현식 타입
     */
    public enum ExpressionType {
        // 숫자 비교
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        EQUALS,
        NOT_EQUALS,
        BETWEEN,

        // 문자열 비교
        STRING_EQUALS,
        STRING_EQUALS_IGNORE_CASE,
        STRING_CONTAINS,
        STRING_STARTS_WITH,
        STRING_ENDS_WITH,

        // 특수 조건
        IS_NEGATIVE,
        IS_POSITIVE,
        IS_ZERO,
        IS_NULL,
        IS_NOT_NULL,
        IS_EMPTY,
        IS_NOT_EMPTY
    }
}
