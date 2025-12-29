package io.github.takoeats.excelannotator.internal.util.strategy;

import org.apache.poi.ss.usermodel.Cell;

import java.time.LocalDate;

public final class LocalDateValueStrategy implements CellValueStrategy {

    @Override
    public boolean supports(Object value) {
        return value instanceof LocalDate;
    }

    @Override
    public void apply(Cell cell, Object value) {
        cell.setCellValue((LocalDate) value);
    }
}
