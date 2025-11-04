package ys.cafe.member.exception;

import ys.cafe.member.exception.errorcode.MemberDomainErrorCode;

/**
 * Member 비즈니스 규칙 위반 예외
 *
 * 사용 범위:
 * - 비즈니스 로직 실행 중 규칙 위반
 * - 도메인 상태 제약 위반
 * - 예: 중복 회원, 회원 없음, 잘못된 회원 상태 등
 *
 * ErrorCode: MemberDomainErrorCode (MB0001~MB0299)
 */
public class MemberDomainException extends MemberException {

    public MemberDomainException(MemberDomainErrorCode errorCode) {
        super(errorCode);
    }

    public MemberDomainException(MemberDomainErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}