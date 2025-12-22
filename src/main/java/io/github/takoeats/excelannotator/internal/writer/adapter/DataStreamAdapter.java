package io.github.takoeats.excelannotator.internal.writer.adapter;

import java.util.Iterator;

public final class DataStreamAdapter {

    public <T> Iterator<T> prependToIterator(T firstItem, Iterator<T> iterator) {
        return new Iterator<T>() {
            private boolean firstReturned = false;

            @Override
            public boolean hasNext() {
                return !firstReturned || iterator.hasNext();
            }

            @Override
            public T next() {
                if (!firstReturned) {
                    firstReturned = true;
                    return firstItem;
                }
                return iterator.next();
            }
        };
    }
}
