package ys.cafe.order.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ys.cafe.order.service.dto.ProductDTO;
import ys.cafe.product.domain.Product;
import ys.cafe.product.service.ProductService;
import ys.cafe.product.service.dto.ProductResponse;

import java.util.List;

/**
 * Product Repository에 직접 접근하는 어댑터 구현체
 * Product Entity를 Order에서 필요한 ProductResponse로 변환
 */
@Component
@RequiredArgsConstructor
public class ProductAdapterImpl implements ProductAdapter {

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
