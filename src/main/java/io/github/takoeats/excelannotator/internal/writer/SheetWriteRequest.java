package io.github.takoeats.excelannotator.internal.writer;

import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import lombok.Getter;

import java.util.Iterator;

@Getter
public final class SheetWriteRequest<T> {

    private final Iterator<T> dataIterator;
    private final ExcelMetadata<T> metadata;

    private SheetWriteRequest(Iterator<T> dataIterator, ExcelMetadata<T> metadata) {
        this.dataIterator = dataIterator;
        this.metadata = metadata;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {

        private Iterator<T> dataIterator;
        private ExcelMetadata<T> metadata;

        public Builder<T> dataIterator(Iterator<T> iterator) {
            this.dataIterator = iterator;
            return this;
        }

        public Builder<T> metadata(ExcelMetadata<T> metadata) {
            this.metadata = metadata;
            return this;
        }

        public SheetWriteRequest<T> build() {
            if (dataIterator == null || metadata == null) {
                throw new IllegalStateException("Iterator and metadata are required");
            }
            return new SheetWriteRequest<>(dataIterator, metadata);
        }
    }
}
