package ys.cafe.payment.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.order.domain.Order;
import ys.cafe.order.domain.OrderStatus;
import ys.cafe.order.repository.OrderRepository;
import ys.cafe.payment.port.OrderPort;

/**
 * Order 아웃바운드 어댑터
 * OrderPort를 구현하여 Order 도메인과 통신
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPortAdapter implements OrderPort {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public void validatePaymentWaiting(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId: " + orderId));

        if (order.getOrderStatus() != OrderStatus.PAYMENT_WAITING) {
            throw new IllegalStateException(
                    "주문이 결제 대기 상태가 아닙니다. orderId: " + orderId + ", currentStatus: " + order.getOrderStatus()
            );
        }

        log.info("결제 대기 상태 확인 완료: orderId={}, status={}", orderId, order.getOrderStatus());
    }

    @Override
    @Transactional
    public void completePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId: " + orderId));

        order.completePayment();
        orderRepository.save(order);

        log.info("결제 완료 처리: orderId={}, newStatus={}", orderId, order.getOrderStatus());
    }

    @Override
    @Transactional
    public void failPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId: " + orderId));

        order.failPayment();
        orderRepository.save(order);

        log.info("결제 실패 처리: orderId={}, newStatus={}", orderId, order.getOrderStatus());
    }
}
