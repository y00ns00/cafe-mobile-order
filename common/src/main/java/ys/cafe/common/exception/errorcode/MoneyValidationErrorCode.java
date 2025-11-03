package ys.cafe.common.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ys.cafe.common.exception.ErrorCode;

/**
 * 금액(Money) Validation 에러 코드
 *
 * 에러 코드 범위: MN0001~MN0999
 * - 금액 생성 및 연산 시 발생하는 검증 오류
 *
 * HTTP 상태 코드는 GlobalExceptionHandler에서 매핑
 */
@Getter
@RequiredArgsConstructor
public enum MoneyValidationErrorCode implements ErrorCode {

    // 금액 검증 (MN0001~MN0099)
    MONEY_REQUIRED("MN0001", "금액은 필수입니다."),
    MONEY_NEGATIVE("MN0002", "금액은 음수일 수 없습니다."),
    MONEY_ZERO("MN0003", "금액은 0원일 수 없습니다."),
    MONEY_TOO_HIGH("MN0004", "금액이 너무 높습니다."),
    MONEY_INVALID("MN0005", "금액이 유효하지 않습니다.");

    private final String code;
    private final String message;
}