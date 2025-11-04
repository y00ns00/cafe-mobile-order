package ys.cafe.order.domain;

import jakarta.persistence.*;
import ys.cafe.common.vo.Won;
import ys.cafe.order.exception.OrderValidationException;
import ys.cafe.order.exception.errorcode.OrderValidationErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Order {
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
    @JoinColumn(name = "order_id")
    private List<OrderLine> orderLines = new ArrayList<>();

    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_price"))
    })
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

    /**
     * 결제 완료 처리
     * PAYMENT_WAITING → PREPARING
     */
    public void completePayment() {
        if (this.orderStatus != OrderStatus.PAYMENT_WAITING) {
            throw new OrderValidationException(
                    OrderValidationErrorCode.ORDER_STATUS_INVALID,
                    "결제 대기 상태가 아닙니다. 현재 상태: " + this.orderStatus
            );
        }
        this.orderStatus = OrderStatus.PREPARING;
    }

    /**
     * 결제 실패 처리
     * PAYMENT_WAITING → CANCELED
     */
    public void failPayment() {
        if (this.orderStatus != OrderStatus.PAYMENT_WAITING) {
            throw new OrderValidationException(
                    OrderValidationErrorCode.ORDER_STATUS_INVALID,
                    "결제 대기 상태가 아닙니다. 현재 상태: " + this.orderStatus
            );
        }
        this.orderStatus = OrderStatus.CANCELED;
    }

    /**
     * 주문 취소
     * PAYMENT_WAITING, PREPARING → CANCELED
     * 회원이 주문을 취소할 수 있음
     */
    public void cancel() {
        // 이미 취소된 경우
        if (this.orderStatus == OrderStatus.CANCELED) {
            throw new OrderValidationException(
                    OrderValidationErrorCode.ORDER_ALREADY_CANCELED,
                    "이미 취소된 주문입니다."
            );
        }

        // 서빙 중이거나 완료된 주문은 취소 불가
        if (this.orderStatus == OrderStatus.SERVE || this.orderStatus == OrderStatus.COMPLETED) {
            throw new OrderValidationException(
                    OrderValidationErrorCode.ORDER_CANNOT_CANCEL,
                    "취소할 수 없는 주문 상태입니다. 현재 상태: " + this.orderStatus
            );
        }

        this.orderStatus = OrderStatus.CANCELED;
    }
}
