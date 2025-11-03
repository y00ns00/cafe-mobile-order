package ys.cafe.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import ys.cafe.member.domain.vo.*;
import ys.member.domain.vo.*;
import ys.cafe.member.exception.MemberValidationException;
import ys.cafe.member.exception.errorcode.MemberValidationErrorCode;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * 회원 기본 정보
 * 이름, 전화번호, 성별, 생년월일을 필수
 */
@Entity
@Getter
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Embedded
    private Password password;

    @Embedded
    private MemberName name;

    @Embedded
    private PhoneNumber phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Embedded
    private BirthDate birthDate;

    @Column(name = "registration_date_time", nullable = false)
    private LocalDateTime registrationDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Column(name = "withdraw_request_at")
    private LocalDateTime withdrawRequestedAt;


    protected Member() {
    }

    public static Member register(
            String rawPassword,
            String lastName,
            String firstName,
            String phoneNumberValue,
            String genderValue,
            String birthDateValue,
            Function<String, String> passwordEncoder
    ) {
        MemberName name = MemberName.of(lastName, firstName);
        PhoneNumber phoneNumber = PhoneNumber.of(phoneNumberValue);
        Gender genderEnum = parseGender(genderValue);
        BirthDate birthDate = BirthDate.of(birthDateValue);

        Member member = new Member();
        member.name = name;
        member.phoneNumber = phoneNumber;
        member.gender = genderEnum;
        member.birthDate = birthDate;
        member.registrationDateTime = LocalDateTime.now();
        member.status = MemberStatus.ACTIVE;

        Password password = Password.of(rawPassword, passwordEncoder);
        member.password = password;

        return member;
    }

    private static Gender parseGender(String genderValue) {
        try {
            return Gender.valueOf(genderValue);
        } catch (IllegalArgumentException e) {
            throw new MemberValidationException(MemberValidationErrorCode.GENDER_INVALID);
        }
    }

}