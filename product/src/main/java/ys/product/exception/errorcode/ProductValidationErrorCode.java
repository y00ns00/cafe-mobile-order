package ys.product.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ys.product.common.ErrorCode;

/**
 * Product Validation 에러 코드 (입력값 검증 실패)
 *
 * 에러 코드 범위: PD0300~PD0899
 * - 각 Value Object별로 구체적인 상황에 맞는 에러 코드 정의
 *
 * HTTP 상태 코드는 GlobalExceptionHandler에서 매핑
 */
@Getter
@RequiredArgsConstructor
public enum ProductValidationErrorCode implements ErrorCode {

    // 상품명 검증 (PD0300~PD0399)
    PRODUCT_NAME_REQUIRED("PD0301", "상품명은 필수입니다."),
    PRODUCT_NAME_TOO_SHORT("PD0302", "상품명은 최소 1자 이상이어야 합니다."),
    PRODUCT_NAME_TOO_LONG("PD0303", "상품명은 최대 100자 이하이어야 합니다."),
    PRODUCT_NAME_INVALID("PD0304", "상품명이 유효하지 않습니다."),

    // 가격 검증 (PD0400~PD0499)
    PRICE_REQUIRED("PD0401", "가격은 필수입니다."),
    PRICE_NEGATIVE("PD0402", "가격은 음수일 수 없습니다."),
    PRICE_ZERO("PD0403", "가격은 0원일 수 없습니다."),
    PRICE_TOO_HIGH("PD0404", "가격이 너무 높습니다."),
    PRICE_INVALID("PD0405", "가격이 유효하지 않습니다."),

    // 이미지 URL 검증 (PD0500~PD0599)
    IMAGE_URL_REQUIRED("PD0501", "이미지 URL은 필수입니다."),
    IMAGE_URL_INVALID_FORMAT("PD0502", "이미지 URL 형식이 올바르지 않습니다."),
    IMAGE_URL_TOO_LONG("PD0503", "이미지 URL이 너무 깁니다."),

    // 상품 상태 검증 (PD0600~PD0699)
    PRODUCT_STATUS_REQUIRED("PD0601", "상품 상태는 필수입니다."),
    PRODUCT_STATUS_INVALID("PD0602", "상품 상태가 유효하지 않습니다. (AVAILABLE, SOLD_OUT, HIDDEN, DISCONTINUED 중 하나)"),

    // 설명 검증 (PD0700~PD0799)
    DESCRIPTION_TOO_LONG("PD0701", "상품 설명은 최대 1000자 이하이어야 합니다.");

    private final String code;
    private final String message;
}
