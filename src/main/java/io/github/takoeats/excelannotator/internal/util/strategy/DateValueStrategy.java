package io.github.takoeats.excelannotator.internal.util.strategy;

import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;

public final class DateValueStrategy implements CellValueStrategy {

    @Override
    public boolean supports(Object value) {
        return value instanceof Date;
    }

    @Override
    public void apply(Cell cell, Object value) {
        cell.setCellValue((Date) value);
    }
}
