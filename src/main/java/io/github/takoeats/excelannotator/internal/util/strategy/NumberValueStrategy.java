package io.github.takoeats.excelannotator.internal.util.strategy;

import org.apache.poi.ss.usermodel.Cell;

public final class NumberValueStrategy implements CellValueStrategy {

    @Override
    public boolean supports(Object value) {
        return value instanceof Number;
    }

    @Override
    public void apply(Cell cell, Object value) {
        cell.setCellValue(((Number) value).doubleValue());
    }
}
