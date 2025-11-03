package ys.cafe.member.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "에러 발생 시간", example = "2025-11-01T12:34:56.789")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;

    @Schema(description = "에러 코드", example = "INVALID_INPUT")
    private String code;

    @Schema(description = "에러 메시지", example = "생년월일은 필수입니다.")
    private String message;

    @Schema(description = "요청 경로", example = "/api/members/signup")
    private String path;

    @Schema(description = "필드별 검증 에러 목록")
    private List<FieldError> fieldErrors;

    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            code,
            message,
            path,
            null
        );
    }

    public static ErrorResponse of(int status, String code, String message, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            code,
            message,
            path,
            fieldErrors
        );
    }

    @Getter
    @AllArgsConstructor
    public static class FieldError {

        @Schema(description = "필드명", example = "birthDate")
        private String field;

        @Schema(description = "거부된 값", example = "2099-01-01")
        private Object rejectedValue;

        @Schema(description = "에러 메시지", example = "생년월일은 현재 날짜보다 이후일 수 없습니다.")
        private String message;
    }
}