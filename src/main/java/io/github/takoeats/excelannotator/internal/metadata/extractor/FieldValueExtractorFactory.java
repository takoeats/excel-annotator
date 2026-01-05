package io.github.takoeats.excelannotator.internal.metadata.extractor;

import io.github.takoeats.excelannotator.exception.ErrorCode;
import io.github.takoeats.excelannotator.exception.ExcelExporterException;
import io.github.takoeats.excelannotator.internal.metadata.ColumnInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldValueExtractorFactory {

    public static <T> Function<T, Object> createExtractor(ColumnInfo columnInfo) {
        MethodHandle methodHandle = extractAndCreateMethodHandle(columnInfo);
        String fieldName = columnInfo.getField().getName();

        if (methodHandle == null) {
            return obj -> null;
        }

        return obj -> invokeGetter(methodHandle, obj, fieldName);
    }

    private static MethodHandle extractAndCreateMethodHandle(ColumnInfo columnInfo) {
        String fieldName = columnInfo.getField().getName();
        String getterName = buildGetterName(fieldName);
        Class<?> declaringClass = columnInfo.getField().getDeclaringClass();

        Method getter;
        try {
            getter = declaringClass.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            throw new ExcelExporterException(
                    ErrorCode.FIELD_ACCESS_FAILED,
                    "Getter 메서드를 찾을 수 없음: " + fieldName,
                    e
            );
        }

        if (!validateGetterMethod(getter, fieldName)) {
            return null;
        }

        try {
            getter.setAccessible(true);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            return lookup.unreflect(getter);
        } catch (IllegalAccessException e) {
            throw new ExcelExporterException(
                    ErrorCode.FIELD_ACCESS_FAILED,
                    "Getter 메서드 접근 실패: " + fieldName,
                    e
            );
        }
    }

    private static Object invokeGetter(MethodHandle methodHandle, Object obj, String fieldName) {
        if (obj == null) {
            return null;
        }

        try {
            return extractFieldValue(methodHandle, obj);
        } catch (Throwable e) {
            throw new ExcelExporterException(
                    ErrorCode.FIELD_ACCESS_FAILED,
                    "필드 값 추출 실패: " + fieldName,
                    e
            );
        }
    }

    private static Object extractFieldValue(MethodHandle methodHandle, Object obj)
            throws Throwable {
        Object value = methodHandle.invoke(obj);
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

    private static String buildGetterName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new ExcelExporterException(ErrorCode.INVALID_FIELD_NAME);
        }
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static boolean validateGetterMethod(Method getter, String fieldName) {
        if (getter.getParameterCount() != 0) {
            return false;
        }

        if (getter.getReturnType() == void.class || getter.getReturnType() == Void.class) {
            return false;
        }

        return true;
    }
}
