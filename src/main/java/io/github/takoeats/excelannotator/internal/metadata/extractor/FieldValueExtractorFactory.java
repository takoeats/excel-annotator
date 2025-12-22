package io.github.takoeats.excelannotator.internal.metadata.extractor;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

@Slf4j
public final class FieldValueExtractorFactory {

    private FieldValueExtractorFactory() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static <T> Function<T, Object> createExtractor(ColumnInfo columnInfo) {
        return obj -> {
            try {
                if (obj == null) {
                    log.debug("Object parameter is null for field: {}", columnInfo.getField().getName());
                    return null;
                }

                String fieldName = columnInfo.getField().getName();
                String getterName = buildGetterName(fieldName);
                Method getter = obj.getClass().getMethod(getterName);

                if (!validateGetterMethod(getter, fieldName)) {
                    return null;
                }

                return extractFieldValue(getter, obj);
            } catch (NoSuchMethodException e) {
                throw new ExcelExporterException(
                        ErrorCode.FIELD_ACCESS_FAILED,
                        "Getter 메서드를 찾을 수 없음: " + columnInfo.getField().getName(),
                        e
                );
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new ExcelExporterException(
                        ErrorCode.FIELD_ACCESS_FAILED,
                        "필드 값 추출 실패: " + columnInfo.getField().getName(),
                        e
                );
            }
        };
    }

    private static String buildGetterName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new ExcelExporterException(ErrorCode.INVALID_FIELD_NAME);
        }
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static boolean validateGetterMethod(Method getter, String fieldName) {
        if (getter.getParameterCount() != 0) {
            log.debug("Invalid getter method (has parameters): get{}",
                    fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
            return false;
        }

        if (getter.getReturnType() == void.class || getter.getReturnType() == Void.class) {
            log.warn("Getter method has void return type for field: {}", fieldName);
            return false;
        }

        return true;
    }

    private static Object extractFieldValue(Method getter, Object obj)
            throws InvocationTargetException, IllegalAccessException {
        Object value = getter.invoke(obj);
        if (value == null) {
            return null;
        }

        if (FieldTypeClassifier.isDateType(value.getClass())
                || value instanceof Number
                || value instanceof Boolean) {
            return value;
        }

        return value.toString();
    }
}
