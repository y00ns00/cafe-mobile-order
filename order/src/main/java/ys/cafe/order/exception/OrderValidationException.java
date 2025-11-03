package ys.cafe.order.exception;

import ys.cafe.order.exception.errorcode.OrderValidationErrorCode;

/**
 * Order 입력값 검증 실패 예외
 *
 * 사용 범위:
 * - 입력 검증 실패 (형식, 필수값, 범위 등)
 * - 주문 및 주문 항목 생성 시 검증 실패
 *
 * ErrorCode: OrderValidationErrorCode (OR0300~OR0899)
 */
public class OrderValidationException extends RuntimeException {

    private final OrderValidationErrorCode errorCode;

    public OrderValidationException(OrderValidationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public OrderValidationException(OrderValidationErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public OrderValidationErrorCode getErrorCode() {
        return errorCode;
    }
}
