package ys.cafe.payment.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ys.cafe.common.exception.CommonException;
import ys.cafe.payment.exception.ErrorResponse;

/**
 * Payment 전역 예외 핸들러
 */
@Slf4j
@RestControllerAdvice(basePackages = "ys.cafe.payment")
@RequiredArgsConstructor
public class PaymentGlobalExceptionHandler {

    private final PaymentErrorCodeHttpStatusMapper httpStatusMapper;

    /**
     * 공통 예외
     */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(
            CommonException ex,
            HttpServletRequest request
    ) {
        log.error("[Common] {} (code: {})", ex.getMessage(), ex.getErrorCode().getCode());

        HttpStatus httpStatus = httpStatusMapper.resolve(ex.getErrorCode());

        ErrorResponse errorResponse = ErrorResponse.of(
                httpStatus.value(),
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    /**
     * 기타 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
