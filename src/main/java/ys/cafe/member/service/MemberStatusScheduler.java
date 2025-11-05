package ys.cafe.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.CommonException;
import ys.cafe.member.domain.Member;
import ys.cafe.member.domain.vo.PhoneNumber;
import ys.cafe.member.persistence.MemberRepository;
import ys.cafe.member.service.dto.request.MemberSignUpRequest;
import ys.cafe.member.service.dto.response.MemberResponse;

@Component
@RequiredArgsConstructor
public class MemberStatusScheduler {

    private final MemberService memberService;

    // 매일 00:00:00에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void updateWithdrawnMembers() {
        memberService.deactivateExpiredWithdrawnMembers();
    }


}
