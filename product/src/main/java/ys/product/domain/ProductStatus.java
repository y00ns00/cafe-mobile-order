package ys.product.domain;

import ys.product.exception.ProductValidationException;
import ys.product.exception.errorcode.ProductValidationErrorCode;

import java.util.Arrays;

public enum ProductStatus {
    // 판매 가능
    AVAILABLE,
    // 판매 중지
    SOLD_OUT,
    // 관리 상태
    HIDDEN,             // 숨김 (등록했지만 미공개)
    DISCONTINUED,       // 단종 (더 이상 판매 안함)
    ;

    public static ProductStatus fromString(String status) {
        if (status == null) {
            throw new ProductValidationException(ProductValidationErrorCode.PRODUCT_STATUS_REQUIRED);
        }

        return Arrays.stream(ProductStatus.values())
                .filter(productStatus -> productStatus.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new ProductValidationException(
                        ProductValidationErrorCode.PRODUCT_STATUS_INVALID
                ));
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return Arrays.stream(ProductStatus.values())
                .anyMatch(productStatus -> productStatus.name().equalsIgnoreCase(status));
    }

}
