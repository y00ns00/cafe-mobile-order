package ys.cafe.member.persistence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ys.cafe.member.domain.Member;
import ys.cafe.member.domain.vo.MemberStatus;
import ys.cafe.member.domain.vo.PhoneNumber;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findOneByPhoneNumber(PhoneNumber phoneNumber);

    @Modifying
    @Query("UPDATE Member m SET m.status = 'DEACTIVATED' WHERE m.status = :status AND m.withdrawRequestedAt <= :deadline")
    int bulkDeactivate(@Param("deadline") LocalDateTime deadline, MemberStatus status);
}
