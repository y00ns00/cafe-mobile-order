package ys.cafe.common.exception;

import lombok.Getter;

/**
 * 공통 예외 클래스
 * 모든 도메인에서 사용 가능한 범용 예외
 */
@Getter
public class CommonException extends RuntimeException {

    private final CommonErrorCode errorCode;
    private final String message;

    public CommonException(CommonErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.message = customMessage;
    }

    public CommonException(CommonErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
