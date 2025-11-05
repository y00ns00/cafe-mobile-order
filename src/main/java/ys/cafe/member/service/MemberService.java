package ys.cafe.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.CommonException;
import ys.cafe.member.domain.Member;
import ys.cafe.member.domain.vo.MemberStatus;
import ys.cafe.member.domain.vo.PhoneNumber;
import ys.cafe.member.persistence.MemberRepository;
import ys.cafe.member.service.dto.request.MemberSignUpRequest;
import ys.cafe.member.service.dto.response.MemberResponse;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse signUp(MemberSignUpRequest request) {

        PhoneNumber phoneNumber = PhoneNumber.of(request.phoneNumber());
        if(memberRepository.findOneByPhoneNumber(phoneNumber).isPresent()) {
            throw new CommonException(CommonErrorCode.ALREADY_EXISTS, "이미 존재하는 회원입니다.");
        }

        Member member = Member.register(
                request.password(),
                request.lastName(),
                request.firstName(),
                request.phoneNumber(),
                request.gender(),
                request.birthDate(),
                passwordEncoder::encode
        );

        memberRepository.save(member);

        return MemberResponse.from(member);
    }


    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));

        return MemberResponse.from(member);

    }

    /**
     * 회원 탈퇴
     * 회원 상태를 WITHDRAW_REQUESTED로 변경하고 탈퇴 요청 일시를 기록합니다.
     * 30일 이내에는 탈퇴를 철회할 수 있습니다.
     *
     * @param memberId 탈퇴할 회원 ID
     * @return 탈퇴 처리된 회원 정보
     */
    @Transactional
    public MemberResponse withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));

        member.withdraw();
        memberRepository.save(member);

        log.info("회원 탈퇴 요청 완료 - memberId: {}, withdrawRequestedAt: {}", memberId, member.getWithdrawRequestedAt());

        return MemberResponse.from(member);
    }

    /**
     * 회원 탈퇴 철회
     * WITHDRAW_REQUESTED 상태의 회원만 철회 가능하며, ACTIVE 상태로 복구합니다.
     * DELETED 상태는 철회 불가능합니다.
     *
     * @param memberId 탈퇴 철회할 회원 ID
     * @return 철회 처리된 회원 정보
     */
    @Transactional
    public MemberResponse cancelWithdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));

        member.cancelWithdraw();
        memberRepository.save(member);

        log.info("회원 탈퇴 철회 완료 - memberId: {}, status: {}", memberId, member.getStatus());

        return MemberResponse.from(member);
    }

    @Transactional
    public void deactivateExpiredWithdrawnMembers() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(30);
        int deactivatedCount = memberRepository.bulkDeactivate(deadline, MemberStatus.DELETED);

        log.info("deactivateExpiredWithdrawnMembers : deactivatedCount = {}", deactivatedCount);
    }


    /**
     * 테스트용
     * 멤버 상태 변경
     */
    @Transactional
    public MemberResponse changeStatus(Long memberId, String status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));

        member.changeStatus(status);

        return MemberResponse.from(member);
    }

}
