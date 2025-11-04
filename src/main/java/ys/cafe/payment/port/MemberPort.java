package ys.cafe.payment.port;

import ys.cafe.payment.service.dto.MemberDTO;

/**
 * Member 아웃바운드 포트
 * Payment 도메인에서 Member 도메인에 접근하기 위한 인터페이스
 */
public interface MemberPort {

    /**
     * 회원 정보 조회
     * @param memberId 회원 ID
     * @return 회원 정보
     */
    MemberDTO getMember(Long memberId);
}
