package ys.cafe.order.objectmother;

import ys.cafe.order.service.dto.ProductDTO;

import java.util.List;

public final class ProductDTOMother {

    public static List<ProductDTO> availableProducts() {
        return List.of(
                ProductDTO.of(1L, "아메리카노", "4500"),
                ProductDTO.of(2L, "카페라떼", "5000")
        );
    }
}
