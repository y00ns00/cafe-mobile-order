package ys.cafe.common.exception;

import ys.cafe.common.exception.errorcode.MoneyValidationErrorCode;

/**
 * 금액(Money) 입력값 검증 실패 예외
 *
 * 사용 범위:
 * - 금액 입력 검증 실패 (형식, 필수값, 범위 등)
 * - Won Value Object 생성 시 검증 실패
 * - 예: 가격 null, 음수, 연산 오류 등
 *
 * ErrorCode: MoneyValidationErrorCode (MN0001~MN0999)
 */
public class MoneyValidationException extends RuntimeException {

    private final ErrorCode errorCode;

    public MoneyValidationException(MoneyValidationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public MoneyValidationException(MoneyValidationErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}