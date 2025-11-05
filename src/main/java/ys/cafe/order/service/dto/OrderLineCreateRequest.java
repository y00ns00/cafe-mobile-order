package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "주문 항목 생성 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineCreateRequest {

    @Schema(description = "상품 ID", example = "1", requiredMode = REQUIRED)
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @Schema(description = "수량", example = "2", requiredMode = REQUIRED, minimum = "1")
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private Integer quantity;
}
