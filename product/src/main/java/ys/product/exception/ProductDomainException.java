package ys.product.exception;

import ys.product.exception.errorcode.ProductDomainErrorCode;

/**
 * Product 비즈니스 규칙 위반 예외
 *
 * 사용 범위:
 * - 비즈니스 로직 실행 중 규칙 위반
 * - 도메인 상태 제약 위반
 * - 예: 중복 상품, 상품 없음, 잘못된 상품 상태 등
 *
 * ErrorCode: ProductDomainErrorCode (PD0001~PD0299)
 */
public class ProductDomainException extends ProductException {

    public ProductDomainException(ProductDomainErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public ProductDomainException(ProductDomainErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
