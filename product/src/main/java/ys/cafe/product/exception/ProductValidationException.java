package ys.cafe.product.exception;

import ys.cafe.product.exception.errorcode.ProductValidationErrorCode;

/**
 * Product 입력값 검증 실패 예외
 *
 * 사용 범위:
 * - 입력 검증 실패 (형식, 필수값, 길이 등)
 * - Value Object 생성 시 검증 실패
 * - 예: 상품명 형식 오류, 가격 범위 오류 등
 *
 * ErrorCode: ProductValidationErrorCode (PD0300~PD0899)
 */
public class ProductValidationException extends ProductException {

    public ProductValidationException(ProductValidationErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public ProductValidationException(ProductValidationErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
