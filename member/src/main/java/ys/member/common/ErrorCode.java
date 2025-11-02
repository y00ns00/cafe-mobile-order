package ys.member.common;

/**
 * Member 도메인 에러 코드 인터페이스
 * Domain과 Validation 에러 코드의 공통 계약
 *
 * HTTP 상태 코드는 Web 계층(GlobalExceptionHandler)에서 매핑하여 관리
 */
public interface ErrorCode {

    /**
     * 에러 코드 (예: MB0001)
     */
    String getCode();

    /**
     * 기본 에러 메시지
     */
    String getMessage();
}
