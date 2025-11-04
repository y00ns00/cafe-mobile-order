package ys.cafe.product.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ys.cafe.common.exception.ErrorCode;

/**
 * Product Domain 에러 코드 (비즈니스 규칙 위반)
 *
 * 에러 코드 범위: PD0001~PD0299
 * - PD0001~PD0099: 상품 조회 관련
 * - PD0100~PD0199: 상품 중복 관련
 * - PD0200~PD0299: 상품 상태 관련
 *
 * HTTP 상태 코드는 GlobalExceptionHandler에서 매핑
 */
@Getter
@RequiredArgsConstructor
public enum ProductDomainErrorCode implements ErrorCode {

    // ===== 상품 조회 (PD0001~PD0099) =====
    PRODUCT_NOT_FOUND("PD0001", "상품을 찾을 수 없습니다."),

    // ===== 상품 중복 (PD0100~PD0199) =====
    DUPLICATE_PRODUCT("PD0100", "이미 존재하는 상품입니다."),

    // ===== 상품 상태 (PD0200~PD0299) =====
    INVALID_PRODUCT_STATUS("PD0200", "잘못된 상품 상태입니다."),
    PRODUCT_NOT_AVAILABLE("PD0201", "판매 가능한 상품이 아닙니다."),
    PRODUCT_ALREADY_SOLD_OUT("PD0202", "이미 품절된 상품입니다."),
    PRODUCT_ALREADY_DISCONTINUED("PD0203", "이미 단종된 상품입니다."),
    PRODUCT_ALREADY_HIDDEN("PD0204", "이미 숨김 처리된 상품입니다.");

    private final String code;
    private final String message;
}
