package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ys.cafe.order.domain.Order;
import ys.cafe.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "주문 응답")
public record OrderResponse(

        @Schema(description = "주문 ID", example = "1")
        Long orderId,

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "주문 상태", example = "PREPARING",
                allowableValues = {"PAYMENT_WAITING", "PREPARING", "SERVE", "COMPLETED", "CANCELED", "FAILED"})
        OrderStatus orderStatus,

        @Schema(description = "주문 항목 목록")
        List<OrderLineResponse> orderLines,

        @Schema(description = "주문 일시", example = "2025-11-05T10:30:00")
        LocalDateTime orderDateTime,

        @Schema(description = "총 가격", example = "15000")
        String totalPrice
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getMemberId(),
                order.getOrderStatus(),
                order.getOrderLines().stream()
                        .map(OrderLineResponse::from)
                        .collect(Collectors.toList()),
                order.getOrderDateTime(),
                order.getTotalPrice().getAmount().toString()
        );
    }
}
