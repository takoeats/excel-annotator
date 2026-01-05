package io.github.takoeats.excelannotator.internal.util.strategy;

import org.apache.poi.ss.usermodel.Cell;

public final class StringValueStrategy implements CellValueStrategy {

    @Override
    public boolean supports(Object value) {
        return value instanceof String;
    }

    @Override
    public void apply(Cell cell, Object value) {
        String strValue = (String) value;
        if (strValue.isEmpty()) {
            cell.setBlank();
        } else {
            cell.setCellValue(strValue);
        }
    }
}
