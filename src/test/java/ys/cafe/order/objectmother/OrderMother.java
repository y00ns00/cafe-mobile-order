package ys.cafe.order.objectmother;

import ys.cafe.order.domain.Order;
import ys.cafe.order.domain.OrderLine;

import java.lang.reflect.Field;
import java.util.List;

public final class OrderMother {

    public static Order order(Long memberId) {
        return orderWithLines(memberId, List.of(OrderLineMother.americano(1)));
    }

    public static Order orderWithLines(Long memberId, List<OrderLine> orderLines) {
        return Order.create(memberId, orderLines);
    }

    public static Order orderWithLinesAndId(Long orderId, Long memberId, List<OrderLine> orderLines) {
        return assignId(orderWithLines(memberId, orderLines), orderId);
    }

    public static Order orderWithId(Long orderId, Long memberId) {
        return assignId(order(memberId), orderId);
    }

    public static Order orderInPreparing(Long memberId) {
        Order order = order(memberId);
        order.completePayment();
        return order;
    }

    public static Order orderInServe(Long memberId) {
        Order order = orderInPreparing(memberId);
        order.startServing();
        return order;
    }

    public static Order orderInCompleted(Long memberId) {
        Order order = orderInServe(memberId);
        order.completeServing();
        return order;
    }

    public static Order assignId(Order order, Long orderId) {
        setField(order, "orderId", orderId);
        return order;
    }

    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to set field value", e);
        }
    }
}
