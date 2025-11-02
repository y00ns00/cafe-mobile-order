package ys.member.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ys.member.exception.ErrorResponse;
import ys.member.exception.MemberValidationException;
import ys.member.exception.MemberDomainException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 전역 예외 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorCodeHttpStatusMapper httpStatusMapper;

    public GlobalExceptionHandler(ErrorCodeHttpStatusMapper httpStatusMapper) {
        this.httpStatusMapper = httpStatusMapper;
    }

    /**
     * Bean Validation 실패 (@Valid, @Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_INPUT",
                "입력값 검증에 실패했습니다.",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Bean Validation 실패 (BindException)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex,
            HttpServletRequest request
    ) {
        log.warn("Binding failed: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_INPUT",
                "입력값 검증에 실패했습니다.",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * IllegalArgumentException - 잘못된 인자
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * IllegalStateException - 잘못된 상태
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        log.warn("IllegalStateException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_STATE",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Member Validation 예외 (입력값 검증 실패)
     * Validation 실패는 클라이언트 입력 오류이므로 WARN 레벨로 기록
     */
    @ExceptionHandler(MemberValidationException.class)
    public ResponseEntity<ErrorResponse> handleMemberValidationException(
            MemberValidationException ex,
            HttpServletRequest request
    ) {
        log.warn("[Validation] {} (code: {})", ex.getMessage(), ex.getErrorCode().getCode());

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
     * Member Domain 예외 (비즈니스 규칙 위반)
     * Domain 예외는 비즈니스 로직 오류이므로 ERROR 레벨로 기록
     */
    @ExceptionHandler(MemberDomainException.class)
    public ResponseEntity<ErrorResponse> handleMemberDomainException(
            MemberDomainException ex,
            HttpServletRequest request
    ) {
        log.error("[Domain] {} (code: {})", ex.getMessage(), ex.getErrorCode().getCode());

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
     * 공통 예외
     */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleMemberDomainException(
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