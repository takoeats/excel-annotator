package io.github.takoeats.excelannotator.exception;

import lombok.Getter;

/**
 * Excel 처리 중 발생하는 커스텀 예외 클래스
 * <p>ErrorCode를 통해 오류 유형을 구분하고, 상세한 오류 정보를 제공합니다.</p>
 *
 * <h3>사용 예시</h3>
 * <pre>{@code
 * // 기본 오류 코드만 사용
 * throw new ExcelExporterException(ErrorCode.EMPTY_DATA);
 *
 * // 상세 정보 추가
 * throw new ExcelExporterException(ErrorCode.INVALID_RGB_VALUE, "Red: 300");
 *
 * // 원인 예외 포함
 * try {
 *     // ... Excel 작업
 * } catch (IOException e) {
 *     throw new ExcelExporterException(ErrorCode.WORKBOOK_CREATION_FAILED, e);
 * }
 *
 * // 상세 정보 + 원인 예외
 * throw new ExcelExporterException(
 *     ErrorCode.WORKBOOK_CREATION_FAILED,
 *     "시트 생성 중 오류",
 *     e
 * );
 * }</pre>
 */
@Getter
public class ExcelExporterException extends RuntimeException {

    /**
     * 오류 코드
     */
    private final ErrorCode errorCode;

    /**
     * 추가 상세 정보
     */
    private final String detail;

    /**
     * 오류 코드만 사용하는 생성자
     *
     * @param errorCode 오류 코드
     */
    public ExcelExporterException(ErrorCode errorCode) {
        super(errorCode.getFormattedMessage());
        this.errorCode = errorCode;
        this.detail = null;
    }

    /**
     * 오류 코드와 상세 정보를 포함하는 생성자
     *
     * @param errorCode 오류 코드
     * @param detail    추가 상세 정보
     */
    public ExcelExporterException(ErrorCode errorCode, String detail) {
        super(errorCode.getFormattedMessage(detail));
        this.errorCode = errorCode;
        this.detail = detail;
    }

    /**
     * 오류 코드와 원인 예외를 포함하는 생성자
     *
     * @param errorCode 오류 코드
     * @param cause     원인 예외
     */
    public ExcelExporterException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getFormattedMessage(), cause);
        this.errorCode = errorCode;
        this.detail = null;
    }

    /**
     * 오류 코드, 상세 정보, 원인 예외를 모두 포함하는 생성자
     *
     * @param errorCode 오류 코드
     * @param detail    추가 상세 정보
     * @param cause     원인 예외
     */
    public ExcelExporterException(ErrorCode errorCode, String detail, Throwable cause) {
        super(errorCode.getFormattedMessage(detail), cause);
        this.errorCode = errorCode;
        this.detail = detail;
    }

    /**
     * 오류 코드의 코드 값 반환
     *
     * @return 오류 코드 문자열 (예: "E001")
     */
    public String getCode() {
        return errorCode.getCode();
    }

    /**
     * 간단한 오류 정보 문자열 반환 (로깅용)
     *
     * @return "코드: 메시지" 형식의 문자열
     */
    public String getSimpleMessage() {
        return errorCode.getFormattedMessage();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExcelExporterException: ");
        sb.append(getMessage());

        if (getCause() != null) {
            sb.append(" | Caused by: ");
            sb.append(getCause().getClass().getSimpleName());
            sb.append(": ");
            sb.append(getCause().getMessage());
        }

        return sb.toString();
    }
}
