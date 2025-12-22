package io.github.takoeats.excelannotator.style.rule;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 조건 평가 유틸리티 클래스
 * <p>StyleCondition 인스턴스를 캐싱하고 생성합니다.</p>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConditionEvaluator {

    private static final Map<Class<? extends StyleCondition>, StyleCondition> CONDITION_CACHE =
            new ConcurrentHashMap<>();

    /**
     * StyleCondition 인스턴스 생성 (캐싱됨)
     *
     * @param conditionClass 조건 클래스
     * @return StyleCondition 인스턴스
     */
    public static StyleCondition getConditionInstance(Class<? extends StyleCondition> conditionClass) {
        return CONDITION_CACHE.computeIfAbsent(conditionClass, k -> {
            try {
                Constructor<? extends StyleCondition> constructor = conditionClass.getDeclaredConstructor();
                return constructor.newInstance();
            } catch (Exception e) {
                throw new ExcelExporterException(
                        ErrorCode.CONDITION_INSTANTIATION_FAILED,
                        "조건 인스턴스 생성 실패: " + conditionClass.getName(),
                        e
                );
            }
        });
    }

    /**
     * 조건 평가
     *
     * @param conditionClass 조건 클래스
     * @param context        셀 컨텍스트
     * @return 조건 만족 여부
     */
    public static boolean evaluate(Class<? extends StyleCondition> conditionClass, CellContext context) {
        if (conditionClass == null || context == null) {
            return false;
        }

        StyleCondition condition = getConditionInstance(conditionClass);
        return condition.test(context);
    }

    /**
     * 숫자 비교 유틸리티
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NumberComparator {

        /**
         * 값이 임계값보다 큰지 확인
         */
        public static boolean greaterThan(Object value, double threshold) {
            Double numValue = toDouble(value);
            return numValue != null && numValue > threshold;
        }

        /**
         * 값이 임계값 이상인지 확인
         */
        public static boolean greaterThanOrEqual(Object value, double threshold) {
            Double numValue = toDouble(value);
            return numValue != null && numValue >= threshold;
        }

        /**
         * 값이 임계값보다 작은지 확인
         */
        public static boolean lessThan(Object value, double threshold) {
            Double numValue = toDouble(value);
            return numValue != null && numValue < threshold;
        }

        /**
         * 값이 임계값 이하인지 확인
         */
        public static boolean lessThanOrEqual(Object value, double threshold) {
            Double numValue = toDouble(value);
            return numValue != null && numValue <= threshold;
        }

        /**
         * 값이 임계값과 같은지 확인
         */
        public static boolean equals(Object value, double threshold) {
            Double numValue = toDouble(value);
            return numValue != null && Math.abs(numValue - threshold) < 0.0001;
        }

        /**
         * 값이 임계값과 다른지 확인
         */
        public static boolean notEquals(Object value, double threshold) {
            return !equals(value, threshold);
        }

        /**
         * 값이 범위 안에 있는지 확인
         */
        public static boolean between(Object value, double min, double max) {
            Double numValue = toDouble(value);
            return numValue != null && numValue >= min && numValue <= max;
        }

        /**
         * 음수인지 확인
         */
        public static boolean isNegative(Object value) {
            return lessThan(value, 0);
        }

        /**
         * 양수인지 확인
         */
        public static boolean isPositive(Object value) {
            return greaterThan(value, 0);
        }

        /**
         * 0인지 확인
         */
        public static boolean isZero(Object value) {
            return equals(value, 0);
        }

        private static Double toDouble(Object value) {
            if (value == null) {
                return null;
            }

            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            if (value instanceof String) {
                String str = ((String) value).trim();
                if (str.isEmpty()) {
                    return null;
                }
                try {
                    return Double.parseDouble(str.replace(",", ""));
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            return null;
        }
    }

    /**
     * 문자열 비교 유틸리티
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StringComparator {

        /**
         * 값이 특정 문자열과 같은지 확인 (대소문자 구분)
         */
        public static boolean equals(Object value, String target) {
            return value != null && value.toString().equals(target);
        }

        /**
         * 값이 특정 문자열과 같은지 확인 (대소문자 무시)
         */
        public static boolean equalsIgnoreCase(Object value, String target) {
            return value != null && value.toString().equalsIgnoreCase(target);
        }

        /**
         * 값이 특정 문자열을 포함하는지 확인
         */
        public static boolean contains(Object value, String substring) {
            return value != null && value.toString().contains(substring);
        }

        /**
         * 값이 특정 문자열로 시작하는지 확인
         */
        public static boolean startsWith(Object value, String prefix) {
            return value != null && value.toString().startsWith(prefix);
        }

        /**
         * 값이 특정 문자열로 끝나는지 확인
         */
        public static boolean endsWith(Object value, String suffix) {
            return value != null && value.toString().endsWith(suffix);
        }

        /**
         * 값이 비어있는지 확인 (null 또는 빈 문자열)
         */
        public static boolean isEmpty(Object value) {
            return value == null || value.toString().trim().isEmpty();
        }

        /**
         * 값이 비어있지 않은지 확인
         */
        public static boolean isNotEmpty(Object value) {
            return !isEmpty(value);
        }
    }
}
