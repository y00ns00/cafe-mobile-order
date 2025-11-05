package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "주문 목록 응답")
public record OrderListResponse(

        @Schema(description = "주문 목록")
        List<OrderResponse> orders,

        @Schema(description = "총 주문 수", example = "10")
        long totalElements,

        @Schema(description = "현재 페이지(0부터 시작)", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "20")
        int size,

        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {

    public static OrderListResponse of(
            List<OrderResponse> orders,
            long totalElements,
            int page,
            int size,
            int totalPages,
            boolean hasNext
    ) {
        return new OrderListResponse(orders, totalElements, page, size, totalPages, hasNext);
    }

    public static OrderListResponse unpaged(List<OrderResponse> orders) {
        int total = orders.size();
        int totalPages = total == 0 ? 0 : 1;
        return new OrderListResponse(orders, total, 0, total, totalPages, false);
    }
}
