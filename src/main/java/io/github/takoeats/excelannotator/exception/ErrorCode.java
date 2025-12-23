package io.github.takoeats.excelannotator.exception;

import lombok.Getter;

/**
 * Excel 처리 관련 오류 코드 정의
 * <p>각 오류 상황에 대한 코드와 메시지를 포함합니다.</p>
 */
@Getter
public enum ErrorCode {

    EMPTY_DATA("E001", "엑셀로 내보낼 데이터가 없습니다."),

    WORKBOOK_CREATION_FAILED("E002", "Excel 워크북 생성에 실패했습니다."),

    INVALID_FIELD_NAME("E003", "필드명은 null이거나 빈 문자열일 수 없습니다."),

    INVALID_RGB_VALUE("E004", "RGB 값은 0-255 범위 내의 정수여야 합니다."),

    INVALID_RGB_ARRAY("E005", "RGB 배열은 [red, green, blue] 형식이어야 합니다."),

    STYLE_INSTANTIATION_FAILED("E006", "스타일 클래스 인스턴스 생성에 실패했습니다."),

    CONDITION_INSTANTIATION_FAILED("E007", "조건 클래스 인스턴스 생성에 실패했습니다."),

    EXPRESSION_PARSE_FAILED("E008", "표현식 파싱에 실패했습니다."),

    METADATA_EXTRACTION_FAILED("E009", "메타데이터 추출에 실패했습니다."),

    FIELD_ACCESS_FAILED("E010", "필드 접근에 실패했습니다."),

    IO_ERROR("E011", "입출력 오류가 발생했습니다."),

    DUPLICATE_SHEET_NAME("E012", "중복된 시트 이름이 존재합니다."),

    INVALID_SHEET_NAME("E013", "유효하지 않은 시트 이름입니다."),

    ORDER_CONFLICT("E014", "동일한 시트 이름을 가진 DTO들의 order 값이 충돌합니다. LinkedHashMap 사용이 필요합니다."),

    STREAM_ALREADY_CONSUMED("E015", "Stream이 이미 소비되었거나 닫혔습니다. Stream은 한 번만 사용할 수 있습니다."),

    EXCEED_MAX_ROWS("E016", "데이터가 100만 건을 초과합니다. Stream API를 사용하세요."),

    DUPLICATE_SHEET_ORDER("E019", "동일한 시트 order 값이 존재합니다. 각 시트의 order는 고유해야 합니다."),

    ROW_WRITE_ERROR("E020", "엑셀 행 작성 중 오류가 발생했습니다."),

    NO_EXCEL_COLUMNS("E021", "@ExcelColumn 어노테이션이 적용된 필드가 없습니다."),

    @Deprecated
    EMPTY_SHEET_DATA("E012-deprecated", "멀티 시트 데이터가 비어있습니다. EMPTY_DATA 사용 권장"),

    @Deprecated
    EMPTY_SHEET_LIST("E015-deprecated", "시트에 포함된 데이터 리스트가 비어있습니다. EMPTY_DATA 사용 권장"),

    @Deprecated
    EMPTY_STREAM("E017-deprecated", "Stream이 비어있습니다. EMPTY_DATA 사용 권장"),

    @Deprecated
    MISSING_EXCEL_SHEET_ANNOTATION("E018-deprecated", "DTO 클래스에 @ExcelSheet 어노테이션이 없습니다. METADATA_EXTRACTION_FAILED 사용 권장");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 오류 코드와 메시지를 포함한 전체 메시지 반환
     *
     * @return "[코드] 메시지" 형식의 문자열
     */
    public String getFormattedMessage() {
        return String.format("[%s] %s", code, message);
    }

    /**
     * 추가 상세 정보를 포함한 메시지 반환
     *
     * @param detail 추가 상세 정보
     * @return "[코드] 메시지 - 상세정보" 형식의 문자열
     */
    public String getFormattedMessage(String detail) {
        return String.format("[%s] %s - %s", code, message, detail);
    }
}
