package ys.member.objectmother;

import ys.member.domain.Member;

public class MemberMother {


    /**
     * 테스트용 활성 회원 생성 헬퍼 메서드
     */
    public static Member getRegisterMember() {
        String rawPassword = "Password123!";
        String lastName = "김";
        String firstName = "철수";
        String phoneNumber = "010-1234-5678";
        String gender = "MALE";
        String birthDate = "1990-01-01";

        // when
        Member member = Member.register(
                rawPassword,
                lastName,
                firstName,
                phoneNumber,
                gender,
                birthDate,
                raw -> "$2a$10$encoded_" + raw
        );

        return member;
    }
}


