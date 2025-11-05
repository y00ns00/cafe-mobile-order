package ys.cafe.order.objectmother;

import ys.cafe.order.service.dto.OrderLineCreateRequest;

public class OrderLineCreateRequestMother {

    public static OrderLineCreateRequest orderLine(Long productId, int quantity) {
        return new OrderLineCreateRequest(productId, quantity);
    }
}
