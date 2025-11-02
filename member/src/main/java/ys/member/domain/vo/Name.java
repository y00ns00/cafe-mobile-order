package ys.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import ys.member.exception.MemberValidationException;
import ys.member.exception.errorcode.MemberValidationErrorCode;

@Embeddable
@Getter
public class Name {

    @Column(name = "last_name", nullable = false)
    private String lastName;  // 성

    @Column(name = "first_name", nullable = false)
    private String firstName;  // 이름

    public Name() {
    }

    private Name(String lastName, String firstName) {
        if (lastName == null || lastName.isBlank()) {
            throw new MemberValidationException(MemberValidationErrorCode.LAST_NAME_REQUIRED);
        }
        if (firstName == null || firstName.isBlank()) {
            throw new MemberValidationException(MemberValidationErrorCode.FIRST_NAME_REQUIRED);
        }
        this.lastName = lastName.trim();
        this.firstName = firstName.trim();
    }

    public static Name of(String lastName, String firstName) {
        return new Name(lastName, firstName);
    }

    /**
     * 전체 이름 반환 (성 + 이름)
     */
    public String getFullName() {
        return lastName + firstName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
