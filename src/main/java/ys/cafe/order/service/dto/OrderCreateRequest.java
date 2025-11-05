package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "주문 생성 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @Schema(description = "회원 ID", example = "1", required = true)
    private Long memberId;

    @Schema(description = "주문 항목 목록", required = true)
    private List<OrderLineCreateRequest> orderLines;

}
