package ys.cafe.member.persistence;

import org.springframework.data.repository.CrudRepository;
import ys.cafe.member.domain.Member;
import ys.cafe.member.domain.vo.PhoneNumber;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findOneByPhoneNumber(PhoneNumber phoneNumber);

}
