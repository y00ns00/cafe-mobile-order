package ys.cafe.member.common;

import lombok.Getter;


/**
 * Member 도메인 관련 예외의 최상위 클래스
 * 모든 Member 예외는 이 클래스를 상속받아 에러 코드와 메시지를 관리
 */
@Getter
public class CommonException extends RuntimeException {

    private final CommonErrorCode errorCode;

    private final String message;


    public CommonException(CommonErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.message = customMessage;
    }
}