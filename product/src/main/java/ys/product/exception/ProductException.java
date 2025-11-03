package ys.product.exception;

import lombok.Getter;
import ys.product.common.ErrorCode;


/**
 * Product 도메인 관련 예외의 최상위 클래스
 * 모든 Product 예외는 이 클래스를 상속받아 에러 코드와 메시지를 관리
 */
@Getter
public abstract class ProductException extends RuntimeException {

    private final ErrorCode errorCode;

    protected ProductException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected ProductException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    protected ProductException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
