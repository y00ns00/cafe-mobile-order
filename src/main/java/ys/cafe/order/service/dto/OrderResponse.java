package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ys.cafe.order.domain.Order;
import ys.cafe.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "주문 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    @Schema(description = "주문 ID", example = "1")
    private Long orderId;

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "주문 상태", example = "PREPARING",
            allowableValues = {"PAYMENT_WAITING", "PREPARING", "SERVE", "COMPLETED", "CANCELED", "FAILED"})
    private OrderStatus orderStatus;

    @Schema(description = "주문 항목 목록")
    private List<OrderLineResponse> orderLines;

    @Schema(description = "주문 일시", example = "2025-11-05T10:30:00")
    private LocalDateTime orderDateTime;

    @Schema(description = "총 가격", example = "15000")
    private String totalPrice;

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
