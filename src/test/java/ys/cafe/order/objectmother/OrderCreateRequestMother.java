package ys.cafe.order.objectmother;

import ys.cafe.order.service.dto.OrderCreateRequest;
import ys.cafe.order.service.dto.OrderLineCreateRequest;

import java.util.List;

public class OrderCreateRequestMother {

    private static final Long DEFAULT_MEMBER_ID = 1L;

    public static OrderCreateRequest defaultRequest() {
        return requestForMember(DEFAULT_MEMBER_ID);
    }

    public static OrderCreateRequest requestForMember(Long memberId) {
        return requestForMember(memberId, defaultOrderLines());
    }

    public static OrderCreateRequest requestForMember(Long memberId, List<OrderLineCreateRequest> orderLines) {
        return new OrderCreateRequest(memberId, orderLines);
    }

    public static List<OrderLineCreateRequest> defaultOrderLines() {
        return List.of(
                OrderLineCreateRequestMother.orderLine(1L, 2),
                OrderLineCreateRequestMother.orderLine(2L, 1)
        );
    }
}
