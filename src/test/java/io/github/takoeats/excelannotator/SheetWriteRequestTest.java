package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.internal.ExcelMetadataFactory;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.internal.writer.SheetWriteRequest;
import io.github.takoeats.excelannotator.testdto.PersonDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SheetWriteRequestTest {

  @Test
  void build_withValidParameters_createsRequest() {
    List<PersonDTO> data = Arrays.asList(
        new PersonDTO("Alice", 30, null),
        new PersonDTO("Bob", 41, null)
    );
    Iterator<PersonDTO> iterator = data.iterator();
    ExcelMetadata<PersonDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(PersonDTO.class);

    SheetWriteRequest<PersonDTO> request = SheetWriteRequest.<PersonDTO>builder()
        .dataIterator(iterator)
        .metadata(metadata)
        .build();

    assertNotNull(request);
    assertSame(iterator, request.getDataIterator());
    assertSame(metadata, request.getMetadata());
  }

  @Test
  void build_withNullIterator_throwsIllegalStateException() {
    ExcelMetadata<PersonDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(PersonDTO.class);

    IllegalStateException exception = assertThrows(
        IllegalStateException.class,
        () -> SheetWriteRequest.<PersonDTO>builder()
            .metadata(metadata)
            .build()
    );

    assertEquals("Iterator and metadata are required", exception.getMessage());
  }

  @Test
  void build_withNullMetadata_throwsIllegalStateException() {
    List<PersonDTO> data = Arrays.asList(
        new PersonDTO("Alice", 30, null)
    );
    Iterator<PersonDTO> iterator = data.iterator();

    IllegalStateException exception = assertThrows(
        IllegalStateException.class,
        () -> SheetWriteRequest.<PersonDTO>builder()
            .dataIterator(iterator)
            .build()
    );

    assertEquals("Iterator and metadata are required", exception.getMessage());
  }

  @Test
  void build_withBothNull_throwsIllegalStateException() {
    IllegalStateException exception = assertThrows(
        IllegalStateException.class,
        () -> SheetWriteRequest.<PersonDTO>builder()
            .build()
    );

    assertEquals("Iterator and metadata are required", exception.getMessage());
  }

  @Test
  void getDataIterator_returnsCorrectIterator() {
    List<PersonDTO> data = Arrays.asList(
        new PersonDTO("Alice", 30, null),
        new PersonDTO("Bob", 41, null)
    );
    Iterator<PersonDTO> iterator = data.iterator();
    ExcelMetadata<PersonDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(PersonDTO.class);

    SheetWriteRequest<PersonDTO> request = SheetWriteRequest.<PersonDTO>builder()
        .dataIterator(iterator)
        .metadata(metadata)
        .build();

    assertTrue(request.getDataIterator().hasNext());
    assertEquals("Alice", request.getDataIterator().next().getName());
  }

  @Test
  void getMetadata_returnsCorrectMetadata() {
    List<PersonDTO> data = Arrays.asList(
        new PersonDTO("Alice", 30, null)
    );
    Iterator<PersonDTO> iterator = data.iterator();
    ExcelMetadata<PersonDTO> metadata = ExcelMetadataFactory.extractExcelMetadata(PersonDTO.class);

    SheetWriteRequest<PersonDTO> request = SheetWriteRequest.<PersonDTO>builder()
        .dataIterator(iterator)
        .metadata(metadata)
        .build();

    assertEquals("Persons", request.getMetadata().getSheetName());
    assertTrue(request.getMetadata().hasHeader());
  }
}
