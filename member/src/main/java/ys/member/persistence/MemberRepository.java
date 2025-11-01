package ys.member.persistence;

import org.springframework.data.repository.CrudRepository;
import ys.member.domain.Member;

public interface MemberRepository extends CrudRepository<Member, Long> {


}
