package ys.cafe.order.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ys.cafe.common.exception.ErrorCode;

/**
 * Order Validation 에러 코드 (입력값 검증 실패)
 *
 * 에러 코드 범위: OR0300~OR0899
 * - 주문 및 주문 항목 생성 시 발생하는 검증 오류
 *
 * HTTP 상태 코드는 GlobalExceptionHandler에서 매핑
 */
@Getter
@RequiredArgsConstructor
public enum OrderValidationErrorCode implements ErrorCode {

    // 주문 검증 (OR0300~OR0399)
    ORDER_MEMBER_ID_REQUIRED("OR0301", "회원 ID는 필수입니다."),
    ORDER_LINES_REQUIRED("OR0302", "주문 항목은 필수입니다."),
    ORDER_LINES_EMPTY("OR0303", "주문 항목이 비어있습니다."),
    ORDER_NOT_FOUND("OR0304", "주문을 찾을 수 없습니다."),
    ORDER_STATUS_INVALID("OR0305", "주문 상태가 유효하지 않습니다."),
    ORDER_CANNOT_CANCEL("OR0306", "취소할 수 없는 주문 상태입니다."),
    ORDER_ALREADY_CANCELED("OR0307", "이미 취소된 주문입니다."),

    // 주문 항목 검증 (OR0400~OR0499)
    ORDER_LINE_PRODUCT_ID_REQUIRED("OR0401", "상품 ID는 필수입니다."),
    ORDER_LINE_PRODUCT_NAME_REQUIRED("OR0402", "상품명은 필수입니다."),
    ORDER_LINE_PRODUCT_NOT_FOUND("OR0403", "상품을 찾을 수 없습니다."),
    ORDER_LINE_PRICE_REQUIRED("OR0405", "가격은 필수입니다.");

    private final String code;
    private final String message;
}
