package io.github.takoeats.excelannotator.internal.util.strategy;

import org.apache.poi.ss.usermodel.Cell;

public final class DefaultValueStrategy implements CellValueStrategy {

    @Override
    public boolean supports(Object value) {
        return true;
    }

    @Override
    public void apply(Cell cell, Object value) {
        cell.setCellValue(value.toString());
    }
}
