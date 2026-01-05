package io.github.takoeats.excelannotator.internal.builder;

/**
 * CSV-specific builder interface
 * <p>Currently identical to BaseBuilder but allows for CSV-specific extensions in the future.</p>
 */
public interface CsvBuilder extends BaseBuilder {

    @Override
    CsvBuilder fileName(String fileName);
}
