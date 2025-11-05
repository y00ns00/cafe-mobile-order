package ys.cafe.order.out.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ys.cafe.order.out.port.ProductPort;
import ys.cafe.order.service.dto.ProductDTO;
import ys.cafe.product.service.ProductService;
import ys.cafe.product.service.dto.ProductResponse;

import java.util.List;

/**
 * Product 아웃바운드 어댑터
 * ProductPort를 구현하여 Product 서비스와 통신
 */
@Component
@RequiredArgsConstructor
public class ProductPortAdapter implements ProductPort {

    private final ProductService productService;

    @Override
    public List<ProductDTO> findAvailableProductsByIds(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return List.of();
        }
        List<ProductResponse> products = productService.getAvailableProductsByIds(productIds);

        return products.stream()
                .map(productResponse ->
                    ProductDTO.of(
                            productResponse.productId(),
                            productResponse.name(),
                            productResponse.price()
                    )
                )
                .toList();
    }
}
