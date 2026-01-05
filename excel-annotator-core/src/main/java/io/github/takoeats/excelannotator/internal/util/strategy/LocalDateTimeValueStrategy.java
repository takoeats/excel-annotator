package io.github.takoeats.excelannotator.internal.util.strategy;

import org.apache.poi.ss.usermodel.Cell;

import java.time.LocalDateTime;

public final class LocalDateTimeValueStrategy implements CellValueStrategy {

    @Override
    public boolean supports(Object value) {
        return value instanceof LocalDateTime;
    }

    @Override
    public void apply(Cell cell, Object value) {
        cell.setCellValue((LocalDateTime) value);
    }
}
