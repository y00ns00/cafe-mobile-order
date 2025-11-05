package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ys.cafe.order.domain.OrderLine;

@Schema(description = "주문 항목 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineResponse {

    @Schema(description = "주문 항목 ID", example = "1")
    private Long orderLineId;

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품명", example = "아메리카노")
    private String productName;

    @Schema(description = "수량", example = "2")
    private Integer quantity;

    @Schema(description = "단가", example = "4500")
    private String price;

    @Schema(description = "총 가격", example = "9000")
    private String totalPrice;

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
