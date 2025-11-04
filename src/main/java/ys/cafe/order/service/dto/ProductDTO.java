package ys.cafe.order.service.dto;

import ys.cafe.order.event.payload.ProductQueryPayload;

public record ProductDTO(
        Long productId,
        String name,
        String price
) {
    public static ProductDTO of(
            Long productId,
            String name,
            String price
    ) {

        return new ProductDTO(productId, name, price);
    }
}
