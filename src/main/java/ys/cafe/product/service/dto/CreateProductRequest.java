package ys.cafe.product.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.*;

@Schema(description = "상품 생성 요청")
public record CreateProductRequest(

        @Schema(description = "상품명", example = "아메리카노", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "상품명은 필수입니다.")
        @Size(min = 2, max = 50, message = "상품명은 2자 이상 50자 이하여야 합니다.")
        String name,

        @Schema(description = "상품 설명", example = "시원한 아이스 아메리카노", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "상품 설명은 필수입니다.")
        String description,

        @Schema(description = "이미지 URL 목록", example = "[\"https://example.com/images/americano.jpg\"]", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "이미지 URL은 최소 1개 이상이어야 합니다.")
        List<@NotBlank(message = "이미지 URL은 빈 값일 수 없습니다.") String> imageUrls,

        @Schema(description = "가격 (원)", example = "4500", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "가격은 필수입니다.")
        @DecimalMin(value = "0", message = "가격은 0 이상이어야 합니다.")
        BigDecimal price,

        @Schema(description = "상품 상태", example = "AVAILABLE", allowableValues = {"AVAILABLE", "HIDDEN"}, requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "상품 상태는 필수입니다.")
        @Pattern(regexp = "AVAILABLE|HIDDEN", message = "상품 상태는 AVAILABLE 또는 HIDDEN만 가능합니다.")
        String status
) {
}