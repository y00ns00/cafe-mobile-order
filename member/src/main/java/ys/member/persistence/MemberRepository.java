package ys.member.persistence;

import org.springframework.data.repository.CrudRepository;
import ys.member.domain.Member;
import ys.member.domain.vo.PhoneNumber;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findOneByPhoneNumber(PhoneNumber phoneNumber);

}
