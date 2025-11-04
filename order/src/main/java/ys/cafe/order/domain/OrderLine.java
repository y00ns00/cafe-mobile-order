package ys.cafe.order.domain;

import jakarta.persistence.*;
import ys.cafe.common.vo.Quantity;
import ys.cafe.common.vo.Won;
import ys.cafe.order.exception.OrderValidationException;
import ys.cafe.order.exception.errorcode.OrderValidationErrorCode;

@Entity
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderLineId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Embedded
    private Quantity quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price"))
    })
    private Won price;

    protected OrderLine() {
    }

    public static OrderLine create(
            Long productId,
            String productName,
            int quantity,
            String price
    ) {
        validateOrderLineInput(productId, productName, price);

        OrderLine orderLine = new OrderLine();
        orderLine.productId = productId;
        orderLine.productName = productName;
        orderLine.quantity = Quantity.of(quantity);
        orderLine.price = Won.of(price);

        return orderLine;
    }

    private static void validateOrderLineInput(
            Long productId,
            String productName,
            String price
    ) {
        if (productId == null) {
            throw new OrderValidationException(OrderValidationErrorCode.ORDER_LINE_PRODUCT_ID_REQUIRED);
        }
        if (productName == null || productName.isBlank()) {
            throw new OrderValidationException(OrderValidationErrorCode.ORDER_LINE_PRODUCT_NAME_REQUIRED);
        }
        if (price == null || price.isBlank()) {
            throw new OrderValidationException(OrderValidationErrorCode.ORDER_LINE_PRICE_REQUIRED);
        }
    }


    public Won getTotalPrice() {
        return price.multiply(quantity.getValue());
    }

    public Long getOrderLineId() {
        return orderLineId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Won getPrice() {
        return price;
    }
}
