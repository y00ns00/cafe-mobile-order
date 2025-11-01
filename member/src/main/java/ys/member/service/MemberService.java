package ys.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ys.member.domain.Member;
import ys.member.domain.vo.PhoneNumber;
import ys.member.persistence.MemberRepository;
import ys.member.service.dto.request.MemberSignUpRequest;
import ys.member.service.dto.response.MemberResponse;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse signUp(MemberSignUpRequest request) {

        PhoneNumber phoneNumber = PhoneNumber.of(request.getPhoneNumber());
        if(memberRepository.findOneByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다");
        }

        Member member = Member.register(
                request.getPassword(),
                request.getLastName(),
                request.getFirstName(),
                request.getPhoneNumber(),
                request.getGender(),
                request.getBirthDate(),
                passwordEncoder::encode
        );

        Member savedMember = memberRepository.save(member);

        return MemberResponse.from(savedMember);
    }




}
