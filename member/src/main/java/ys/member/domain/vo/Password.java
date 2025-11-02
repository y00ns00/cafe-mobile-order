package ys.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ys.member.exception.MemberValidationException;
import ys.member.exception.errorcode.MemberValidationErrorCode;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 비밀번호 Value Object
 *
 * 비밀번호 정책:
 * - 최소 8자 이상, 최대 20자 이하
 * - 영문자 포함 필수
 * - 숫자 포함 필수
 * - 특수문자 포함 필수
 */
@Embeddable
public class Password {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 20;
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%?]");
    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile("^[a-zA-Z0-9!@#$%?]+$");

    @Column(name = "password", nullable = false)
    private String value;

    protected Password() {}

    private Password(String rawPassword, Function<String, String> encoder) {
        validatePassword(rawPassword);
        this.value = encoder.apply(rawPassword);
    }

    /**
     * 비밀번호 유효성 검증
     */
    private void validatePassword(String password) {
        // 필수 검증
        if (password == null || password.isBlank()) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_REQUIRED);
        }

        // 길이 검증
        if (password.length() < MIN_LENGTH) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_TOO_SHORT);
        }
        if (password.length() > MAX_LENGTH) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_TOO_LONG);
        }

        // 영문자 포함 검증
        if (!LETTER_PATTERN.matcher(password).find()) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_MISSING_LETTER);
        }

        // 숫자 포함 검증
        if (!DIGIT_PATTERN.matcher(password).find()) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_MISSING_DIGIT);
        }

        // 특수문자 포함 검증
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_MISSING_SPECIAL_CHAR);
        }

        // 허용된 문자만 사용하는지 검증
        if (!ALLOWED_CHARS_PATTERN.matcher(password).matches()) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_INVALID_CHAR);
        }
    }

    public static Password of(String rawPassword,  Function<String, String> encoder) {
        return new Password(rawPassword, encoder);
    }

    /**
     * 비밀번호 암호화
     */
    private void encode(Function<String, String> passwordEncoder) {
        this.value = passwordEncoder.apply(this.value);
    }

    /**
     * 비밀번호 변경
     */
    public Password change(String newPassword, Function<String, String> passwordEncoder) {
        Password password = Password.of(newPassword, passwordEncoder);
        return password;
    }

    /**
     * 비밀번호 일치 확인
     */
    public boolean matches(String rawPassword, EncodedPasswordMatcher passwordMatcher) {
        return passwordMatcher.matches(rawPassword, this.value);
    }

    @Override
    public String toString() {
        return "********"; // 보안상 비밀번호는 출력하지 않음
    }

    @FunctionalInterface
    public interface EncodedPasswordMatcher {
        boolean matches(String rawPassword,  String encodedPassword);
    }

}


