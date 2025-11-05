package ys.cafe.common.exception;

/**
 * 공통 에러 코드
 * 모든 도메인에서 사용 가능한 범용 에러 코드
 */
public enum CommonErrorCode implements ErrorCode {

    ALREADY_EXISTS("CO0001", "이미 존재합니다"),
    NOT_FOUND("CO0002", "찾을 수 없습니다"),
    BAD_REQUEST("CO0003", "잘못된 요청입니다"),
    ;

    private final String code;
    private final String message;

    CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
