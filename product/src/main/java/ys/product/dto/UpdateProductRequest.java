package ys.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "상품 수정 요청")
public record UpdateProductRequest(

        @Schema(description = "상품명", example = "카페라떼", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "상품명은 필수입니다.")
        @Size(min = 2, max = 50, message = "상품명은 2자 이상 50자 이하여야 합니다.")
        String name,

        @Schema(description = "상품 설명", example = "부드러운 카페라떼", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "상품 설명은 필수입니다.")
        String description,

        @Schema(description = "이미지 URL 목록", example = "[\"https://example.com/images/latte.jpg\"]", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "이미지 URL은 최소 1개 이상이어야 합니다.")
        List<@NotBlank(message = "이미지 URL은 빈 값일 수 없습니다.") String> imageUrls,

        @Schema(description = "가격 (원)", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "가격은 필수입니다.")
        @DecimalMin(value = "0", message = "가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @Schema(description = "상품 상태", example = "AVAILABLE", allowableValues = {"AVAILABLE", "SOLD_OUT"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "상품 상태는 필수입니다.")
        @Pattern(regexp = "AVAILABLE|SOLD_OUT", message = "상품 상태는 AVAILABLE 또는 SOLD_OUT만 가능합니다.")
        String status
) {
}