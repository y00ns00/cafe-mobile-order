package ys.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public class Name {

    @Column(name = "last_name", nullable = false)
    private String lastName;  // 성

    @Column(name = "first_name", nullable = false)
    private String firstName;  // 이름

    protected Name() {}

    private Name(String lastName, String firstName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("성은 필수입니다.");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        this.lastName = lastName.trim();
        this.firstName = firstName.trim();
    }

    public static Name of(String lastName, String firstName) {
        return new Name(lastName, firstName);
    }

    public String getFullName() {
        return lastName + firstName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
