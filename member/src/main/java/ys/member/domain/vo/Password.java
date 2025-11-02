package ys.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ys.member.exception.MemberValidationException;
import ys.member.exception.errorcode.MemberValidationErrorCode;

import java.util.function.Function;

@Embeddable
public class Password {

    @Column(name = "password", nullable = false)
    private String value;

    protected Password() {}

    private Password(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_REQUIRED);
        }
        this.value = rawPassword;
    }

    public static Password of(String rawPassword) {
        return new Password(rawPassword);
    }

    /**
     * 비밀번호 암호화
     */
    public void encode(Function<String, String> passwordEncoder) {
        this.value = passwordEncoder.apply(this.value);
    }

    /**
     * 비밀번호 변경
     */
    public void change(String newPassword, Function<String, String> passwordEncoder) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new MemberValidationException(MemberValidationErrorCode.PASSWORD_REQUIRED);
        }
        this.value = passwordEncoder.apply(newPassword);
    }

    /**
     * 비밀번호 일치 확인
     */
    public boolean matches(String rawPassword, Function<String, Boolean> matcher) {
        return matcher.apply(rawPassword);
    }

    @Override
    public String toString() {
        return "********"; // 보안상 비밀번호는 출력하지 않음
    }
}
