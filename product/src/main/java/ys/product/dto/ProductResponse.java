package ys.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ys.product.domain.Product;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "상품 응답")
public record ProductResponse(

        @Schema(description = "상품 ID", example = "1")
        Long productId,

        @Schema(description = "상품명", example = "아메리카노")
        String name,

        @Schema(description = "상품 설명", example = "시원한 아이스 아메리카노")
        String description,

        @Schema(description = "이미지 URL 목록", example = "[\"https://example.com/images/americano.jpg\"]")
        List<String> imageUrls,

        @Schema(description = "가격 (원)", example = "4500")
        String price,

        @Schema(description = "상품 상태", example = "AVAILABLE", allowableValues = {"AVAILABLE", "SOLD_OUT"})
        String status
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName().getValue(),
                product.getDescription(),
                product.getImages().stream()
                        .map(imageUrl -> imageUrl.getUrl())
                        .collect(Collectors.toList()),
                product.getPrice().toString(),
                product.getStatus().name()
        );
    }
}