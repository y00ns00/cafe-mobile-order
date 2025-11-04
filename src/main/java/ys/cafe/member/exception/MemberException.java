package ys.cafe.member.exception;

import lombok.Getter;
import ys.cafe.member.common.ErrorCode;


/**
 * Member 도메인 관련 예외의 최상위 클래스
 * 모든 Member 예외는 이 클래스를 상속받아 에러 코드와 메시지를 관리
 */
@Getter
public abstract class MemberException extends RuntimeException {

    private final ErrorCode errorCode;

    protected MemberException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected MemberException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    protected MemberException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}