package ys.cafe.order.objectmother;

import ys.cafe.order.domain.OrderLine;

public final class OrderLineMother {

    private OrderLineMother() {
    }

    public static OrderLine orderLine(Long productId, String productName, int quantity, String price) {
        return OrderLine.create(productId, productName, quantity, price);
    }

    public static OrderLine americano(int quantity) {
        return orderLine(1L, "아메리카노", quantity, "4500");
    }

    public static OrderLine cafeLatte(int quantity) {
        return orderLine(2L, "카페라떼", quantity, "5000");
    }

    public static OrderLine cappuccino(int quantity) {
        return orderLine(3L, "카푸치노", quantity, "5500");
    }
}
