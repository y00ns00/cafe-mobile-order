package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "주문 목록 응답")
public record OrderListResponse(

        @Schema(description = "주문 목록")
        List<OrderResponse> orders,

        @Schema(description = "총 주문 개수", example = "10")
        int totalCount
) {
    public static OrderListResponse of(List<OrderResponse> orders) {
        return new OrderListResponse(orders, orders.size());
    }
}
