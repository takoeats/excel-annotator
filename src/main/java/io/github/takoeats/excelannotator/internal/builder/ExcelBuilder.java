package io.github.takoeats.excelannotator.internal.builder;

import io.github.takoeats.excelannotator.ExcelExporter;

import java.util.Map;
import java.util.function.Function;

/**
 * Excel-specific builder interface
 * <p>Extends base functionality with multi-sheet and data provider patterns.</p>
 */
public interface ExcelBuilder extends BaseBuilder {

    @Override
    ExcelBuilder fileName(String fileName);

    /**
     * Writes multi-sheet Excel from a Map
     * <p>Map values MUST be either {@code List<?>} or {@code Stream<?>}.</p>
     *
     * @param sheetData map of sheet identifier to data (List or Stream)
     * @return the final processed filename
     * @throws io.github.takoeats.excelannotator.exception.ExcelExporterException if map values are not List or Stream
     */
    String write(Map<String, ?> sheetData);

    /**
     * Writes Excel with data provider and converter pattern
     *
     * @param queryParams  query parameters for data retrieval
     * @param dataProvider function to retrieve data based on query params
     * @param converter    function to convert retrieved data to Excel DTO
     * @param <Q>          query parameter type
     * @param <S>          retrieved data type
     * @param <E>          Excel DTO type
     * @return the final processed filename
     */
    <Q, S, E> String write(Q queryParams,
                           ExcelExporter.ExcelDataProvider<Q, S> dataProvider,
                           Function<S, E> converter);
}
