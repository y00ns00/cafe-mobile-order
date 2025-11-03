package ys.cafe.order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ys.cafe.order.domain.OrderLine;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineResponse {

    private Long orderLineId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String price;
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
