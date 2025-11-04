package ys.cafe.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.CommonException;
import ys.cafe.member.domain.Member;
import ys.cafe.member.domain.vo.PhoneNumber;
import ys.cafe.member.persistence.MemberRepository;
import ys.cafe.member.service.dto.request.MemberSignUpRequest;
import ys.cafe.member.service.dto.response.MemberResponse;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse signUp(MemberSignUpRequest request) {

        PhoneNumber phoneNumber = PhoneNumber.of(request.getPhoneNumber());
        if(memberRepository.findOneByPhoneNumber(phoneNumber).isPresent()) {
            throw new CommonException(CommonErrorCode.ALREADY_EXISTS, "이미 존재하는 회원입니다.");
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

        memberRepository.save(member);

        return MemberResponse.from(member);
    }


    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));

        return MemberResponse.from(member);

    }




}
