package io.github.takoeats.excelannotator.internal;

import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExcelMetadataCache {

    private static final Map<Class<?>, ExcelMetadata<?>> METADATA_CACHE =
            new ConcurrentHashMap<>();

    private ExcelMetadataCache() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    @SuppressWarnings("unchecked")
    public static <T> ExcelMetadata<T> getMetadata(Class<T> clazz) {
        return (ExcelMetadata<T>) METADATA_CACHE.computeIfAbsent(
                clazz,
                ExcelMetadataFactory::extractExcelMetadata
        );
    }

    public static void clearCache() {
        METADATA_CACHE.clear();
    }

    public static int getCacheSize() {
        return METADATA_CACHE.size();
    }
}
