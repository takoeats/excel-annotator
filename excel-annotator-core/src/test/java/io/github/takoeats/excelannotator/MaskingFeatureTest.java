package io.github.takoeats.excelannotator;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.masking.Masking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaskingFeatureTest {

    @Getter
    @AllArgsConstructor
    @ExcelSheet("개인정보")
    public static class PersonalInfoDTO {
        @ExcelColumn(header = "이름", order = 1, masking = Masking.NAME)
        private String name;

        @ExcelColumn(header = "전화번호", order = 2, masking = Masking.PHONE)
        private String phone;

        @ExcelColumn(header = "이메일", order = 3, masking = Masking.EMAIL)
        private String email;

        @ExcelColumn(header = "주민번호", order = 4, masking = Masking.SSN)
        private String ssn;
    }

    @Getter
    @AllArgsConstructor
    @ExcelSheet("금융정보")
    public static class FinancialInfoDTO {
        @ExcelColumn(header = "계좌번호", order = 1, masking = Masking.ACCOUNT_NUMBER)
        private String accountNumber;

        @ExcelColumn(header = "신용카드", order = 2, masking = Masking.CREDIT_CARD)
        private String creditCard;

        @ExcelColumn(header = "금액", order = 3, masking = Masking.NONE)
        private String amount;
    }

    @Getter
    @AllArgsConstructor
    @ExcelSheet("기타정보")
    public static class OtherInfoDTO {
        @ExcelColumn(header = "주소", order = 1, masking = Masking.ADDRESS)
        private String address;

        @ExcelColumn(header = "우편번호", order = 2, masking = Masking.ZIP_CODE)
        private String zipCode;

        @ExcelColumn(header = "IP주소", order = 3, masking = Masking.IP_ADDRESS)
        private String ipAddress;

        @ExcelColumn(header = "여권번호", order = 4, masking = Masking.PASSPORT)
        private String passport;
    }

    @Test
    void masking_personalInfo_shouldMaskSensitiveData() throws Exception {
        List<PersonalInfoDTO> data = Arrays.asList(
                new PersonalInfoDTO("홍길동", "010-1234-5678", "hong@example.com", "123456-1234567"),
                new PersonalInfoDTO("김철수", "010-9876-5432", "kim@test.com", "987654-7654321"),
                new PersonalInfoDTO("이영희", "02-1234-5678", "lee@company.com", "111111-2222222")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        assertEquals("홍*동", row1.getCell(0).getStringCellValue());
        assertEquals("010-****-5678", row1.getCell(1).getStringCellValue());
        assertEquals("h***@example.com", row1.getCell(2).getStringCellValue());
        assertEquals("123456-*******", row1.getCell(3).getStringCellValue());

        Row row2 = sheet.getRow(2);
        assertEquals("김*수", row2.getCell(0).getStringCellValue());
        assertEquals("010-****-5432", row2.getCell(1).getStringCellValue());
        assertEquals("k***@test.com", row2.getCell(2).getStringCellValue());
        assertEquals("987654-*******", row2.getCell(3).getStringCellValue());

        Row row3 = sheet.getRow(3);
        assertEquals("이*희", row3.getCell(0).getStringCellValue());
        assertEquals("02-****-5678", row3.getCell(1).getStringCellValue());
        assertEquals("l***@company.com", row3.getCell(2).getStringCellValue());
        assertEquals("111111-*******", row3.getCell(3).getStringCellValue());

        workbook.close();
    }

    @Test
    void masking_financialInfo_shouldMaskAccountAndCard() throws Exception {
        List<FinancialInfoDTO> data = Arrays.asList(
                new FinancialInfoDTO("110-123-456789", "1234-5678-9012-3456", "1,000,000"),
                new FinancialInfoDTO("220-456-789012", "9876-5432-1098-7654", "2,500,000")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        assertEquals("110-***-***789", row1.getCell(0).getStringCellValue());
        assertEquals("****-****-****-3456", row1.getCell(1).getStringCellValue());
        assertEquals("1,000,000", row1.getCell(2).getStringCellValue());

        Row row2 = sheet.getRow(2);
        assertEquals("220-***-***012", row2.getCell(0).getStringCellValue());
        assertEquals("****-****-****-7654", row2.getCell(1).getStringCellValue());
        assertEquals("2,500,000", row2.getCell(2).getStringCellValue());

        workbook.close();
    }

    @Test
    void masking_otherInfo_shouldMaskVariousTypes() throws Exception {
        List<OtherInfoDTO> data = Arrays.asList(
                new OtherInfoDTO("서울시 강남구 테헤란로 123", "12345", "192.168.1.100", "M12345678"),
                new OtherInfoDTO("경기도 성남시 분당구 정자동", "06789", "10.0.0.1", "AB1234567")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        assertEquals("서울시 강남구 ***", row1.getCell(0).getStringCellValue());
        assertEquals("123**", row1.getCell(1).getStringCellValue());
        assertEquals("192.168.*.*", row1.getCell(2).getStringCellValue());
        assertEquals("M12***678", row1.getCell(3).getStringCellValue());

        Row row2 = sheet.getRow(2);
        assertEquals("경기도 성남시 ***", row2.getCell(0).getStringCellValue());
        assertEquals("067**", row2.getCell(1).getStringCellValue());
        assertEquals("10.0.*.*", row2.getCell(2).getStringCellValue());
        assertEquals("AB1***567", row2.getCell(3).getStringCellValue());

        workbook.close();
    }

    @Getter
    @AllArgsConstructor
    @ExcelSheet("부분마스킹")
    public static class PartialMaskingDTO {
        @ExcelColumn(header = "왼쪽마스킹", order = 1, masking = Masking.PARTIAL_LEFT)
        private String leftMasked;

        @ExcelColumn(header = "오른쪽마스킹", order = 2, masking = Masking.PARTIAL_RIGHT)
        private String rightMasked;

        @ExcelColumn(header = "중간마스킹", order = 3, masking = Masking.MIDDLE)
        private String middleMasked;
    }

    @Test
    void masking_partialMasking_shouldMaskCorrectly() throws Exception {
        List<PartialMaskingDTO> data = Arrays.asList(
                new PartialMaskingDTO("ABC12345", "1234567890", "ABCDEFGH"),
                new PartialMaskingDTO("XYZ98765", "9876543210", "12345678")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        assertEquals("****2345", row1.getCell(0).getStringCellValue());
        assertEquals("1234******", row1.getCell(1).getStringCellValue());
        assertEquals("AB****GH", row1.getCell(2).getStringCellValue());

        Row row2 = sheet.getRow(2);
        assertEquals("****8765", row2.getCell(0).getStringCellValue());
        assertEquals("9876******", row2.getCell(1).getStringCellValue());
        assertEquals("12****78", row2.getCell(2).getStringCellValue());

        workbook.close();
    }

    @Getter
    @AllArgsConstructor
    @ExcelSheet("마스킹없음")
    public static class NoMaskingDTO {
        @ExcelColumn(header = "일반텍스트", order = 1)
        private String normalText;

        @ExcelColumn(header = "숫자", order = 2)
        private Integer number;

        @ExcelColumn(header = "마스킹명시안함", order = 3, masking = Masking.NONE)
        private String explicitNone;
    }

    @Test
    void masking_none_shouldNotMask() throws Exception {
        List<NoMaskingDTO> data = Arrays.asList(
                new NoMaskingDTO("테스트데이터", 12345, "원본유지"),
                new NoMaskingDTO("Hello World", 99999, "Keep Original")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        assertEquals("테스트데이터", row1.getCell(0).getStringCellValue());
        assertEquals(12345, (int) row1.getCell(1).getNumericCellValue());
        assertEquals("원본유지", row1.getCell(2).getStringCellValue());

        Row row2 = sheet.getRow(2);
        assertEquals("Hello World", row2.getCell(0).getStringCellValue());
        assertEquals(99999, (int) row2.getCell(1).getNumericCellValue());
        assertEquals("Keep Original", row2.getCell(2).getStringCellValue());

        workbook.close();
    }

    @Getter
    @AllArgsConstructor
    @ExcelSheet("차량/신분증")
    public static class VehicleAndIdDTO {
        @ExcelColumn(header = "차량번호", order = 1, masking = Masking.LICENSE_PLATE)
        private String licensePlate;

        @ExcelColumn(header = "신분증번호", order = 2, masking = Masking.ID_CARD)
        private String idCard;
    }

    @Test
    void masking_vehicleAndId_shouldMaskCorrectly() throws Exception {
        List<VehicleAndIdDTO> data = Arrays.asList(
                new VehicleAndIdDTO("12가3456", "11-12-345678-90"),
                new VehicleAndIdDTO("서울12가3456", "123456-1234567")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        assertEquals("12가**56", row1.getCell(0).getStringCellValue());
        assertNotNull(row1.getCell(1).getStringCellValue());

        Row row2 = sheet.getRow(2);
        assertEquals("서울12가**56", row2.getCell(0).getStringCellValue());
        assertNotNull(row2.getCell(1).getStringCellValue());

        workbook.close();
    }

    @Getter
    @AllArgsConstructor
    @ExcelSheet("널값테스트")
    public static class NullTestDTO {
        @ExcelColumn(header = "이름", order = 1, masking = Masking.NAME)
        private String name;

        @ExcelColumn(header = "전화번호", order = 2, masking = Masking.PHONE)
        private String phone;
    }

    @Test
    void masking_nullValues_shouldHandleGracefully() throws Exception {
        List<NullTestDTO> data = Arrays.asList(
                new NullTestDTO("홍길동", null),
                new NullTestDTO(null, "010-1234-5678"),
                new NullTestDTO(null, null)
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ExcelExporter.excelFromList(baos, "test.xlsx", data);

        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(baos.toByteArray()));
        Sheet sheet = workbook.getSheetAt(0);

        Row row1 = sheet.getRow(1);
        assertEquals("홍*동", row1.getCell(0).getStringCellValue());
        assertEquals("", row1.getCell(1).getStringCellValue());

        Row row2 = sheet.getRow(2);
        assertEquals("", row2.getCell(0).getStringCellValue());
        assertEquals("010-****-5678", row2.getCell(1).getStringCellValue());

        Row row3 = sheet.getRow(3);
        assertEquals("", row3.getCell(0).getStringCellValue());
        assertEquals("", row3.getCell(1).getStringCellValue());

        workbook.close();
    }
}
