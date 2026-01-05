package io.github.takoeats.excelannotator.internal.builder;

import java.util.List;
import java.util.stream.Stream;

/**
 * Base interface for all file format builders
 * <p>Provides common file generation methods returning the final filename.</p>
 */
public interface BaseBuilder {

    /**
     * Sets the filename for the file
     *
     * @param fileName the desired filename (will be sanitized and may have timestamp added)
     * @return this builder for method chaining
     */
    BaseBuilder fileName(String fileName);

    /**
     * Writes file from a List of data
     *
     * @param data the data list to export
     * @param <T>  the DTO type annotated with @ExcelSheet and @ExcelColumn
     * @return the final processed filename
     */
    <T> String write(List<T> data);

    /**
     * Writes file from a Stream of data (memory-efficient for large datasets)
     *
     * @param dataStream the data stream to export
     * @param <T>        the DTO type annotated with @ExcelSheet and @ExcelColumn
     * @return the final processed filename
     */
    <T> String write(Stream<T> dataStream);
}
