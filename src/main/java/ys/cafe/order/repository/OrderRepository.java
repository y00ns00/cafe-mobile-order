package ys.cafe.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ys.cafe.order.domain.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 회원 ID로 주문 목록 조회
     * @param memberId 회원 ID
     * @return 해당 회원의 주문 목록
     */
    List<Order> findByMemberId(Long memberId);
}
