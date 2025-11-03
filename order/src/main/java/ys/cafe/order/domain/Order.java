package ys.cafe.order.domain;

import com.google.common.collect.Lists;
import jakarta.persistence.*;
import ys.cafe.common.vo.Won;
import ys.cafe.order.exception.OrderValidationException;
import ys.cafe.order.exception.errorcode.OrderValidationErrorCode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Order {


    // todo id 객체로 분리하기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column
    private Long memberId;

    // 수락대기, 수락, 완료, 취소
    @Enumerated(EnumType.STRING)
    @Column
    private OrderStatus orderStatus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderLine> orderLines = Lists.newArrayList();

    private LocalDateTime orderDateTime;

    private Won totalPrice;

    protected Order() {}

    public static Order create(
            Long memberId,
            List<OrderLine> orderLines
    ) {
        validateOrderInput(memberId, orderLines);

        Order order = new Order();
        order.memberId = memberId;
        order.orderLines = orderLines;
        order.orderDateTime = LocalDateTime.now();
        order.orderStatus = OrderStatus.PAYMENT_WAITING;
        order.totalPrice = calculateTotalPrice(orderLines.stream().map(OrderLine::getTotalPrice).toList());
        return order;
    }

    private static void validateOrderInput(Long memberId, List<OrderLine> orderLines) {
        if (memberId == null) {
            throw new OrderValidationException(OrderValidationErrorCode.ORDER_MEMBER_ID_REQUIRED);
        }
        if (orderLines == null) {
            throw new OrderValidationException(OrderValidationErrorCode.ORDER_LINES_REQUIRED);
        }
        if (orderLines.isEmpty()) {
            throw new OrderValidationException(OrderValidationErrorCode.ORDER_LINES_EMPTY);
        }
    }

    private static Won calculateTotalPrice(List<Won> prices) {
        return prices.stream()
                .reduce(Won.zero(), Won::add);
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public Won getTotalPrice() {
        return totalPrice;
    }
}
