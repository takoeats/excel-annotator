package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.internal.ExcelMetadataFactory;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.testdto.MultiSheetColumnDTO;
import io.github.takoeats.excelannotator.testdto.PersonDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ExcelMetadataFactoryTest {

    @Test
    void extractExcelMetadata_basicFields() {
        ExcelMetadata<PersonDTO> meta = ExcelMetadataFactory.extractExcelMetadata(PersonDTO.class);

        assertNotNull(meta);
        assertEquals("Persons", meta.getSheetName());
        assertTrue(meta.hasHeader());

        assertEquals(Arrays.asList("Name", "Age", "Salary"), meta.getHeaders());
        assertEquals(Arrays.asList(120, 80, 120), meta.getColumnWidths());
        assertEquals(3, meta.getExtractors().size());

        // Verify extractors read values via getters
        PersonDTO p = new PersonDTO("Alice", 30, new BigDecimal("1234.50"));
        List<Function<PersonDTO, Object>> ex = meta.getExtractors();
        assertEquals("Alice", ex.get(0).apply(p));
        assertEquals(30, ex.get(1).apply(p));
        assertEquals(new BigDecimal("1234.50"), ex.get(2).apply(p));
    }

    @Test
    void extractMultiSheetMetadata_groupsByColumnSheet() {
        Map<String, ExcelMetadata<MultiSheetColumnDTO>> map =
                ExcelMetadataFactory.extractMultiSheetMetadata(MultiSheetColumnDTO.class);

        assertEquals(2, map.size());
        assertTrue(map.containsKey("SheetA"));
        assertTrue(map.containsKey("SheetB"));

        ExcelMetadata<MultiSheetColumnDTO> aMeta = map.get("SheetA");
        ExcelMetadata<MultiSheetColumnDTO> bMeta = map.get("SheetB");

        assertEquals(Collections.singletonList("ColA"), aMeta.getHeaders());
        assertEquals(Collections.singletonList("ColB"), bMeta.getHeaders());

        // Default hasHeader propagated from class annotation
        assertTrue(aMeta.hasHeader());
        assertTrue(bMeta.hasHeader());
    }
}