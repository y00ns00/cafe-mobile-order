package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ys.cafe.order.domain.OrderLine;

@Schema(description = "주문 항목 응답")
public record OrderLineResponse(

        @Schema(description = "주문 항목 ID", example = "1")
        Long orderLineId,

        @Schema(description = "상품 ID", example = "1")
        Long productId,

        @Schema(description = "상품명", example = "아메리카노")
        String productName,

        @Schema(description = "수량", example = "2")
        Integer quantity,

        @Schema(description = "단가", example = "4500")
        String price,

        @Schema(description = "총 가격", example = "9000")
        String totalPrice
) {
    public static OrderLineResponse from(OrderLine orderLine) {
        return new OrderLineResponse(
                orderLine.getOrderLineId(),
                orderLine.getProductId(),
                orderLine.getProductName(),
                orderLine.getQuantity().getValue(),
                orderLine.getPrice().getAmount().toString(),
                orderLine.getTotalPrice().getAmount().toString()
        );
    }
}
