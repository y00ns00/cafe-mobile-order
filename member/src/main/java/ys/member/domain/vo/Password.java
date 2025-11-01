package ys.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.function.Function;

@Embeddable
public class Password {

    @Column(name = "password", nullable = false)
    private String value;

    protected Password() {}

    private Password(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        this.value = rawPassword;
    }

    public static Password of(String rawPassword) {
        return new Password(rawPassword);
    }

    public void encode(Function<String, String> passwordEncoder) {
        this.value = passwordEncoder.apply(this.value);
    }

    public void change(String newPassword, Function<String, String> passwordEncoder) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("새 비밀번호는 필수입니다.");
        }
        this.value = passwordEncoder.apply(newPassword);
    }

    public boolean matches(String rawPassword, Function<String, Boolean> matcher) {
        return matcher.apply(rawPassword);
    }
}
