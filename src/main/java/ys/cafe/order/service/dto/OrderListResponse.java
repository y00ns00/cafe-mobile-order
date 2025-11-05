package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "주문 목록 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {

    @Schema(description = "주문 목록")
    private List<OrderResponse> orders;

    @Schema(description = "총 주문 개수", example = "10")
    private int totalCount;

    public static OrderListResponse of(List<OrderResponse> orders) {
        return new OrderListResponse(orders, orders.size());
    }
}
