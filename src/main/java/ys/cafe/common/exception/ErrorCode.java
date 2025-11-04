package ys.cafe.common.exception;

/**
 * 도메인 에러 코드 공통 인터페이스
 * 모든 도메인의 에러 코드가 구현해야 하는 공통 계약
 *
 * HTTP 상태 코드는 Web 계층(GlobalExceptionHandler)에서 매핑하여 관리
 */
public interface ErrorCode {

    /**
     * 에러 코드 (예: PD0001, MB0001)
     */
    String getCode();

    /**
     * 기본 에러 메시지
     */
    String getMessage();
}