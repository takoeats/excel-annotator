package io.github.takoeats.excelannotator.util;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.internal.ExcelMetadataFactory;
import io.github.takoeats.excelannotator.internal.metadata.ExcelMetadata;
import io.github.takoeats.excelannotator.internal.util.ColumnWidthCalculator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ColumnWidthCalculator 테스트")
class ColumnWidthCalculatorTest {

    private Workbook workbook;
    private Sheet sheet;

    @BeforeEach
    void setUp() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet();
    }

    @AfterEach
    void tearDown() throws Exception {
        workbook.close();
    }

    @ExcelSheet("Test")
    @Getter
    @AllArgsConstructor
    public static class TestDTO {

        @ExcelColumn(header = "Name", order = 1, width = 150)
        private String name;

        @ExcelColumn(header = "Age", order = 2, width = 100)
        private int age;

        @ExcelColumn(header = "Description", order = 3)
        private String description;
    }

    @Test
    @DisplayName("estimateCellWidth - 영문 문자의 너비 계산")
    void shouldCalculateEnglishCharacterWidth() {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("ABC");

        int width = ColumnWidthCalculator.estimateCellWidth(cell);

        int expectedLength = 3;
        int expectedWidth = (expectedLength * 256) + (256 * 2);
        assertEquals(expectedWidth, width);
    }

    @Test
    @DisplayName("estimateCellWidth - 한글 문자의 너비 계산 (2배)")
    void shouldCalculateKoreanCharacterWidth() {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("가나다");

        int width = ColumnWidthCalculator.estimateCellWidth(cell);

        int expectedLength = 6;
        int expectedWidth = (expectedLength * 256) + (256 * 2);
        assertEquals(expectedWidth, width);
    }

    @Test
    @DisplayName("estimateCellWidth - 한글과 영문 혼합 너비 계산")
    void shouldCalculateMixedCharacterWidth() {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("ABC가나");

        int width = ColumnWidthCalculator.estimateCellWidth(cell);

        int expectedLength = 7;
        int expectedWidth = (expectedLength * 256) + (256 * 2);
        assertEquals(expectedWidth, width);
    }

    @Test
    @DisplayName("estimateCellWidth - 숫자의 너비 계산")
    void shouldCalculateNumericWidth() {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(12345);

        int width = ColumnWidthCalculator.estimateCellWidth(cell);
        assertTrue(width > 0);
    }

    @Test
    @DisplayName("estimateCellWidth - 빈 문자열의 너비 계산")
    void shouldCalculateEmptyStringWidth() {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("");

        int width = ColumnWidthCalculator.estimateCellWidth(cell);
        int expectedWidth = (0 * 256) + (256 * 2);
        assertEquals(expectedWidth, width);
    }

    @Test
    @DisplayName("applyAutoSizeToColumn - 단일 컬럼 자동 크기 조정")
    void shouldAutoSizeSingleColumn() {
        Row row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("Short");

        Row row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("Very Long Text Here");

        ColumnWidthCalculator.applyAutoSizeToColumn(sheet, 0);

        int columnWidth = sheet.getColumnWidth(0);
        assertTrue(columnWidth > 256);
    }

    @Test
    @DisplayName("applyAutoSizeToColumn - 자동 크기 조정 후 최소 너비 보장")
    void shouldEnsureMinimumWidthAfterAutoSize() {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("A");

        ColumnWidthCalculator.applyAutoSizeToColumn(sheet, 0);

        int columnWidth = sheet.getColumnWidth(0);
        assertTrue(columnWidth >= 256);
    }

    @Test
    @DisplayName("applyFixedColumnWidths - 고정 너비 적용")
    void shouldApplyFixedWidths() {
        ExcelMetadata<TestDTO> metadata =
                ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);

        ColumnWidthCalculator.applyFixedColumnWidths(sheet, metadata);

        assertEquals(150 * 32, sheet.getColumnWidth(0));
        assertEquals(100 * 32, sheet.getColumnWidth(1));
    }

    @Test
    @DisplayName("applyFixedColumnWidths - autoWidth 컬럼은 고정 너비 적용 안 함")
    void shouldNotApplyWidthForAutoWidthColumns() {
        ExcelMetadata<TestDTO> metadata =
                ExcelMetadataFactory.extractExcelMetadata(TestDTO.class);

        ColumnWidthCalculator.applyFixedColumnWidths(sheet, metadata);

        assertTrue(sheet.getColumnWidth(0) == 150 * 32);
        assertTrue(sheet.getColumnWidth(1) == 100 * 32);
        assertTrue(sheet.getColumnWidth(2) >= 0);
    }

    @Test
    @DisplayName("applySampledAutoSize - 10,000행 샘플링하여 너비 계산")
    void shouldSampleFirst10000Rows() {
        for (int i = 0; i < 15000; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("SampleData" + i);
        }

        ColumnWidthCalculator.applySampledAutoSize(sheet, 0);

        assertTrue(sheet.getColumnWidth(0) > 0);
    }

    @Test
    @DisplayName("applySampledAutoSize - 최소 너비 보장")
    void shouldEnsureMinimumWidth() {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("");

        ColumnWidthCalculator.applySampledAutoSize(sheet, 0);

        assertTrue(sheet.getColumnWidth(0) >= 256);
    }

    @Test
    @DisplayName("applySampledAutoSize - 한글 데이터 샘플링")
    void shouldSampleKoreanData() {
        for (int i = 0; i < 15000; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("고객명" + i);
        }

        ColumnWidthCalculator.applySampledAutoSize(sheet, 0);

        assertTrue(sheet.getColumnWidth(0) > 256);
    }

    @Test
    @DisplayName("getCalibrationFactor - 보정 계수는 32")
    void shouldReturnCalibrationFactor() {
        assertEquals(32, ColumnWidthCalculator.getCalibrationFactor());
    }

    @ExcelSheet("Integration")
    @Getter
    @AllArgsConstructor
    public static class IntegrationDTO {

        @ExcelColumn(header = "영문이름", order = 1)
        private String englishName;

        @ExcelColumn(header = "한글이름", order = 2)
        private String koreanName;

        @ExcelColumn(header = "고정너비", order = 3, width = 200)
        private String fixedWidth;
    }

    @Test
    @DisplayName("고정너비 + autoWidth 혼합 적용")
    void shouldApplyMixedWidths() {
        ExcelMetadata<IntegrationDTO> metadata =
                ExcelMetadataFactory.extractExcelMetadata(IntegrationDTO.class);

        Row row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("John");
        row1.createCell(1).setCellValue("홍길동");
        row1.createCell(2).setCellValue("Fixed");

        Row row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("Jane Doe");
        row2.createCell(1).setCellValue("김철수입니다");
        row2.createCell(2).setCellValue("Test");

        ColumnWidthCalculator.applyFixedColumnWidths(sheet, metadata);
        ColumnWidthCalculator.applyAutoWidthColumns(sheet, metadata);

        assertEquals(200 * 32, sheet.getColumnWidth(2));
        assertTrue(sheet.getColumnWidth(0) > 0);
        assertTrue(sheet.getColumnWidth(1) > 0);
    }

    @ExcelSheet("SmallDataset")
    @Getter
    @AllArgsConstructor
    public static class SmallDatasetDTO {

        @ExcelColumn(header = "Column1", order = 1)
        private String column1;

        @ExcelColumn(header = "Column2", order = 2)
        private String column2;
    }

    @Test
    @DisplayName("applyAutoWidthColumns - 10,000행 이하일 때 전체 스캔 방식 사용")
    void applyAutoWidthColumns_withSmallDataset_shouldUseFullScan() {
        ExcelMetadata<SmallDatasetDTO> metadata =
                ExcelMetadataFactory.extractExcelMetadata(SmallDatasetDTO.class);

        for (int i = 0; i < 5000; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("Data" + i);
            row.createCell(1).setCellValue("Value" + i);
        }

        ColumnWidthCalculator.applyAutoWidthColumns(sheet, metadata);

        assertTrue(sheet.getColumnWidth(0) > 0);
        assertTrue(sheet.getColumnWidth(1) > 0);
    }

    @Test
    @DisplayName("applyAutoWidthColumns - 10,000행 초과일 때 샘플링 방식 사용")
    void applyAutoWidthColumns_withLargeDataset_shouldUseSampling() {
        ExcelMetadata<SmallDatasetDTO> metadata =
                ExcelMetadataFactory.extractExcelMetadata(SmallDatasetDTO.class);

        for (int i = 0; i < 15000; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("Data" + i);
            row.createCell(1).setCellValue("Value" + i);
        }

        ColumnWidthCalculator.applyAutoWidthColumns(sheet, metadata);

        assertTrue(sheet.getColumnWidth(0) > 0);
        assertTrue(sheet.getColumnWidth(1) > 0);
    }

    @ExcelSheet("MixedWidth")
    @Getter
    @AllArgsConstructor
    public static class MixedWidthDTO {

        @ExcelColumn(header = "AutoWidth", order = 1)
        private String autoWidth;

        @ExcelColumn(header = "FixedWidth", order = 2, width = 150)
        private String fixedWidth;
    }

    @Test
    @DisplayName("applyAutoWidthColumns - 고정 너비 컬럼은 건드리지 않고 autoWidth만 적용")
    void applyAutoWidthColumns_shouldOnlyApplyToAutoWidthColumns() {
        ExcelMetadata<MixedWidthDTO> metadata =
                ExcelMetadataFactory.extractExcelMetadata(MixedWidthDTO.class);

        for (int i = 0; i < 100; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("AutoData" + i);
            row.createCell(1).setCellValue("FixedData" + i);
        }

        ColumnWidthCalculator.applyFixedColumnWidths(sheet, metadata);
        int fixedWidthBefore = sheet.getColumnWidth(1);

        ColumnWidthCalculator.applyAutoWidthColumns(sheet, metadata);

        assertTrue(sheet.getColumnWidth(0) > 0);
        assertEquals(fixedWidthBefore, sheet.getColumnWidth(1));
    }
}
