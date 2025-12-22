package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.testdto.QueryParams;
import io.github.takoeats.excelannotator.testdto.UserEntity;
import io.github.takoeats.excelannotator.testdto.UserExcelDTO;
import io.github.takoeats.excelannotator.util.ExcelAssertions;
import io.github.takoeats.excelannotator.util.ExcelTestHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ExcelDataProviderPatternTest {

    @Test
    void queryParamsToServiceToExcel_fullFlow() throws Exception {
        QueryParams params = QueryParams.builder()
            .status("ACTIVE")
            .page(1)
            .size(20)
            .build();

        List<UserEntity> entities = mockUserService(params);

        List<UserExcelDTO> excelData = entities.stream()
            .map(UserExcelDTO::fromEntity)
            .collect(Collectors.toList());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "users.xlsx", excelData);

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        ExcelAssertions.assertExcelFileValid(baos.toByteArray());

        Sheet sheet = wb.getSheetAt(0);
        ExcelAssertions.assertSheetName(wb, 0, "User List");
        ExcelAssertions.assertRowCount(sheet, 21);

        Row dataRow = sheet.getRow(1);
        assertNotNull(dataRow.getCell(0));
        assertNotNull(dataRow.getCell(1));
        assertNotNull(dataRow.getCell(2));
    }

    @Test
    void pagination_differentPages_correctData() throws Exception {
        QueryParams page1Params = QueryParams.builder()
            .page(1)
            .size(10)
            .build();

        QueryParams page2Params = QueryParams.builder()
            .page(2)
            .size(10)
            .build();

        List<UserEntity> page1Entities = mockUserService(page1Params);
        List<UserEntity> page2Entities = mockUserService(page2Params);

        List<UserExcelDTO> page1Data = page1Entities.stream()
            .map(UserExcelDTO::fromEntity)
            .collect(Collectors.toList());

        List<UserExcelDTO> page2Data = page2Entities.stream()
            .map(UserExcelDTO::fromEntity)
            .collect(Collectors.toList());

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos1, "page1.xlsx", page1Data);

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos2, "page2.xlsx", page2Data);

        Workbook wb1 = ExcelTestHelper.workbookFromBytes(baos1.toByteArray());
        Workbook wb2 = ExcelTestHelper.workbookFromBytes(baos2.toByteArray());

        ExcelAssertions.assertRowCount(wb1.getSheetAt(0), 11);
        ExcelAssertions.assertRowCount(wb2.getSheetAt(0), 11);

        Row wb1Row1 = wb1.getSheetAt(0).getRow(1);
        Row wb2Row1 = wb2.getSheetAt(0).getRow(1);

        String id1 = wb1Row1.getCell(0).toString();
        String id2 = wb2Row1.getCell(0).toString();

        assertNotEquals(id1, id2);
    }

    @Test
    void entityToDtoConversion_preservesAllFields() throws Exception {
        UserEntity entity = UserEntity.builder()
            .id(123L)
            .username("testuser")
            .email("test@example.com")
            .status("ACTIVE")
            .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
            .lastLoginAt(LocalDateTime.of(2025, 1, 27, 15, 30))
            .build();

        UserExcelDTO dto = UserExcelDTO.fromEntity(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getUsername(), dto.getUsername());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getLastLoginAt(), dto.getLastLoginAt());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "single_user.xlsx",
            java.util.Collections.singletonList(dto));

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);
        Row dataRow = sheet.getRow(1);

        ExcelTestHelper.assertCellValue(dataRow.getCell(1), "testuser");
        ExcelTestHelper.assertCellValue(dataRow.getCell(2), "test@example.com");
        ExcelTestHelper.assertCellValue(dataRow.getCell(3), "ACTIVE");
    }

    @Test
    void converterChain_multipleTransformations() throws Exception {
        List<UserEntity> entities = createMockEntities(50);

        Function<UserEntity, UserExcelDTO> basicConverter = UserExcelDTO::fromEntity;

        Function<UserExcelDTO, UserExcelDTO> statusNormalizer = dto ->
            UserExcelDTO.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .status(dto.getStatus().toUpperCase())
                .createdAt(dto.getCreatedAt())
                .lastLoginAt(dto.getLastLoginAt())
                .build();

        List<UserExcelDTO> processedData = entities.stream()
            .map(basicConverter)
            .map(statusNormalizer)
            .collect(Collectors.toList());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "transformed.xlsx", processedData);

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        ExcelAssertions.assertRowCount(wb.getSheetAt(0), 51);

        Row dataRow = wb.getSheetAt(0).getRow(1);
        String status = dataRow.getCell(3).getStringCellValue();
        assertTrue(status.equals("ACTIVE") || status.equals("INACTIVE"));
    }

    @Test
    void nullQueryParams_usesDefaults() throws Exception {
        QueryParams nullParams = QueryParams.builder().build();

        List<UserEntity> entities = mockUserService(nullParams);

        List<UserExcelDTO> excelData = entities.stream()
            .map(UserExcelDTO::fromEntity)
            .collect(Collectors.toList());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "default_params.xlsx", excelData);

        Workbook wb = ExcelTestHelper.workbookFromBytes(baos.toByteArray());
        Sheet sheet = wb.getSheetAt(0);

        assertTrue(sheet.getLastRowNum() > 0);
        assertNotNull(sheet.getRow(1));
    }

    private List<UserEntity> mockUserService(QueryParams params) {
        int page = params.getPage() != null ? params.getPage() : 1;
        int size = params.getSize() != null ? params.getSize() : 20;
        String status = params.getStatus() != null ? params.getStatus() : "ACTIVE";

        int startId = (page - 1) * size + 1;
        int endId = page * size;

        List<UserEntity> result = new ArrayList<>();
        for (int i = startId; i <= endId; i++) {
            result.add(UserEntity.builder()
                .id((long) i)
                .username("user" + i)
                .email("user" + i + "@example.com")
                .status(status)
                .createdAt(LocalDateTime.now().minusDays(i))
                .lastLoginAt(LocalDateTime.now().minusHours(i))
                .build());
        }

        return result;
    }

    private List<UserEntity> createMockEntities(int count) {
        List<UserEntity> result = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            result.add(UserEntity.builder()
                .id((long) i)
                .username("user" + i)
                .email("user" + i + "@example.com")
                .status(i % 2 == 0 ? "active" : "inactive")
                .createdAt(LocalDateTime.now().minusDays(i))
                .lastLoginAt(LocalDateTime.now().minusHours(i))
                .build());
        }
        return result;
    }
}
