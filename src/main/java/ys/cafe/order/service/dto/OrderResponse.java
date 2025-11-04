package ys.cafe.order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ys.cafe.order.domain.Order;
import ys.cafe.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long memberId;
    private OrderStatus orderStatus;
    private List<OrderLineResponse> orderLines;
    private LocalDateTime orderDateTime;
    private String totalPrice;
    private String paymentUrl;

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getMemberId(),
                order.getOrderStatus(),
                order.getOrderLines().stream()
                        .map(OrderLineResponse::from)
                        .collect(Collectors.toList()),
                order.getOrderDateTime(),
                order.getTotalPrice().getAmount().toString(),
                null
        );
    }

    public static OrderResponse fromWithPaymentUrl(Order order, String paymentUrl) {
        return new OrderResponse(
                order.getOrderId(),
                order.getMemberId(),
                order.getOrderStatus(),
                order.getOrderLines().stream()
                        .map(OrderLineResponse::from)
                        .collect(Collectors.toList()),
                order.getOrderDateTime(),
                order.getTotalPrice().getAmount().toString(),
                paymentUrl
        );
    }
}
