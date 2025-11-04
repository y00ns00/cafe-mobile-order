package ys.cafe.member.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ys.cafe.member.common.ErrorCode;

/**
 * Member Domain 에러 코드 (비즈니스 규칙 위반)
 *
 * 에러 코드 범위: MB0001~MB0299
 * - MB0001~MB0099: 회원 중복 관련
 * - MB0100~MB0199: 회원 조회 관련
 * - MB0200~MB0299: 회원 상태 관련
 *
 * HTTP 상태 코드는 GlobalExceptionHandler에서 매핑
 */
@Getter
@RequiredArgsConstructor
public enum MemberDomainErrorCode implements ErrorCode {

    // ===== 회원 상태 (MB0200~MB0299) =====
    INVALID_MEMBER_STATE("MB0200", "잘못된 회원 상태입니다."),
    NOT_WITHDRAW_REQUESTED_STATE("MB0201", "탈퇴 요청 상태가 아닙니다."),
    WITHDRAW_REVOKE_PERIOD_EXPIRED("MB0202", "탈퇴 철회 가능한 기간이 지났습니다."),
    ALREADY_WITHDRAW_REQUESTED("MB0203", "이미 탈퇴가 요청된 회원입니다.");

    private final String code;
    private final String message;
}
