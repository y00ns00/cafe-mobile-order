package ys.cafe.product.common;

import ys.cafe.common.exception.ErrorCode;

/**
 * 공통 에러 코드 인터페이스
 */
public enum CommonErrorCode implements ErrorCode {

    ALREADY_EXISTS("CO0001"),
    NOT_FOUND("CO0002"),
    BAD_REQUEST("CO0003"),
    ;


    CommonErrorCode(String code) {
        this.code = code;
    }
    private final String code;


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return "";
    }
}
