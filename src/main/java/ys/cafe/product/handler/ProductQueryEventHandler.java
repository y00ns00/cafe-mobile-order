package ys.cafe.product.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ys.cafe.common.event.EventHandler;
import ys.cafe.common.event.GenericQueryEvent;
import ys.cafe.order.util.DataSerializer;
import ys.cafe.product.service.ProductService;
import ys.cafe.product.service.dto.ProductResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product 조회 이벤트를 처리하는 핸들러
 * Order의 event 구조(DataSerializer)를 사용하여 직렬화/역직렬화 수행
 */
@Component
@RequiredArgsConstructor
public class ProductQueryEventHandler implements EventHandler {

    private static final String PRODUCT_QUERY_EVENT = "PRODUCT_QUERY";
    private final ProductService productService;

    @Override
    public boolean canHandle(String eventType) {
        return PRODUCT_QUERY_EVENT.equals(eventType);
    }

    @Override
    public void handle(GenericQueryEvent event) {
        // 1. JSON 역직렬화: payloadJson → ProductQueryPayloadDto
        String payloadJson = event.getPayloadJson();
        ProductQueryPayloadDto request = DataSerializer.deserialize(
                payloadJson,
                ProductQueryPayloadDto.class
        );

        // 2. Product 조회
        List<ProductResponse> productResponses =
                productService.getAvailableProductsByIds(request.getProductIds());

        // 3. ProductQueryResponsePayloadDto로 변환
        List<ProductInfoDto> products = productResponses.stream()
                .map(p -> new ProductInfoDto(p.productId(), p.name(), p.price()))
                .collect(Collectors.toList());

        ProductQueryResponsePayloadDto response = new ProductQueryResponsePayloadDto(products);

        // 4. JSON 직렬화하여 결과 저장
        String responseJson = DataSerializer.serialize(response);
        event.setResultJson(responseJson);
    }

    // Product 모듈에서 사용하는 DTO (Order의 EventPayload 구조를 모방)
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ProductQueryPayloadDto {
        private List<Long> productIds;
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ProductQueryResponsePayloadDto {
        private List<ProductInfoDto> products;
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ProductInfoDto {
        private Long productId;
        private String name;
        private String price;
    }
}
