package io.github.takoeats.excelannotator.internal.util.strategy;

import org.apache.poi.ss.usermodel.Cell;

import java.util.Calendar;

public final class CalendarValueStrategy implements CellValueStrategy {

    @Override
    public boolean supports(Object value) {
        return value instanceof Calendar;
    }

    @Override
    public void apply(Cell cell, Object value) {
        cell.setCellValue((Calendar) value);
    }
}
