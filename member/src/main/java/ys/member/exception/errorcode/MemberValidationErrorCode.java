package ys.member.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ys.member.common.ErrorCode;

/**
 * Member Validation 에러 코드 (입력값 검증 실패)
 *
 * 에러 코드 범위: MB0300~MB0899
 * - 각 Value Object별로 구체적인 상황에 맞는 에러 코드 정의
 *
 * HTTP 상태 코드는 GlobalExceptionHandler에서 매핑
 */
@Getter
@RequiredArgsConstructor
public enum MemberValidationErrorCode implements ErrorCode {

    // 비밀번호 검증 (MB0300~MB0399)
    PASSWORD_REQUIRED("MB0301", "비밀번호는 필수입니다."),
    PASSWORD_TOO_SHORT("MB0302", "비밀번호는 최소 8자 이상이어야 합니다."),
    PASSWORD_TOO_LONG("MB0303", "비밀번호는 최대 20자 이하이어야 합니다."),
    PASSWORD_MISSING_LETTER("MB0304", "비밀번호는 영문자를 포함해야 합니다."),
    PASSWORD_MISSING_DIGIT("MB0305", "비밀번호는 숫자를 포함해야 합니다."),
    PASSWORD_MISSING_SPECIAL_CHAR("MB0306", "비밀번호는 특수문자(!@#$%?)를 포함해야 합니다."),
    PASSWORD_INVALID_CHAR("MB0307", "비밀번호는 영문자, 숫자, 특수문자(!@#$%?)만 사용할 수 있습니다."),

    // 이름 검증 (MB0400~MB0499)
    LAST_NAME_REQUIRED("MB0401", "성은 필수입니다."),
    FIRST_NAME_REQUIRED("MB0402", "이름은 필수입니다."),
    NAME_INVALID("MB0403", "이름이 유효하지 않습니다."),

    // 전화번호 검증 (MB0500~MB0599)
    PHONE_NUMBER_REQUIRED("MB0501", "전화번호는 필수입니다."),
    PHONE_NUMBER_INVALID_FORMAT("MB0502", "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)"),

    // 생년월일 검증 (MB0600~MB0699)
    BIRTH_DATE_REQUIRED("MB0601", "생년월일은 필수입니다."),
    BIRTH_DATE_INVALID_FORMAT("MB0602", "생년월일은 yyyy-MM-dd 형식이어야 합니다. (예: 1990-01-01)"),
    BIRTH_DATE_FUTURE("MB0603", "생년월일은 현재 날짜보다 이후일 수 없습니다."),

    // 성별 검증 (MB0700~MB0799)
    GENDER_INVALID("MB0701", "성별은 MALE, FEMALE, OTHER 중 하나여야 합니다. "),

    // 주소 검증 (MB0800~MB0899)
    ADDRESS_INVALID("MB0801", "주소가 유효하지 않습니다.");

    private final String code;
    private final String message;
}
