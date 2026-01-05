package io.github.takoeats.excelannotator.internal;

import lombok.Getter;

import java.util.Iterator;

@Getter
public final class SheetDataEntry {

    private final Iterator<?> data;
    private final Class<?> clazz;

    public SheetDataEntry(Iterator<?> data, Class<?> clazz) {
        this.data = data;
        this.clazz = clazz;
    }

}
