package ys.cafe.member.exception;

import ys.cafe.member.exception.errorcode.MemberValidationErrorCode;

/**
 * Member 입력값 검증 실패 예외
 *
 * 사용 범위:
 * - 입력 검증 실패 (형식, 필수값, 길이 등)
 * - Value Object 생성 시 검증 실패
 * - 예: 전화번호 형식 오류, 생년월일 형식 오류 등
 *
 * ErrorCode: MemberValidationErrorCode (MB0300~MB0899)
 */
public class MemberValidationException extends MemberException {

    public MemberValidationException(MemberValidationErrorCode errorCode) {
        super(errorCode);
    }

    public MemberValidationException(MemberValidationErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public MemberValidationException(MemberValidationErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}