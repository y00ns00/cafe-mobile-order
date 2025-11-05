package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "주문 항목 생성 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineCreateRequest {

    @Schema(description = "상품 ID", example = "1", required = true)
    private Long productId;

    @Schema(description = "수량", example = "2", required = true, minimum = "1")
    private Integer quantity;
}
