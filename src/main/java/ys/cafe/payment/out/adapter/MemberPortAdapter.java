package ys.cafe.payment.out.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ys.cafe.member.service.MemberService;
import ys.cafe.member.service.dto.response.MemberResponse;
import ys.cafe.payment.out.port.MemberPort;
import ys.cafe.payment.service.dto.MemberDTO;

/**
 * Member 아웃바운드 어댑터
 * MemberPort를 구현하여 Member 도메인과 통신
 */
@Component
@RequiredArgsConstructor
public class MemberPortAdapter implements MemberPort {

    private final MemberService memberService;

    @Override
    public MemberDTO getMember(Long memberId) {
        MemberResponse member = memberService.getMember(memberId);

        return MemberDTO.of(
                member.name().fullName(),
                member.phoneNumber().value(),
                member.gender(),
                member.birthDate().birthDate(),
                member.registrationDateTime()
        );
    }
}
