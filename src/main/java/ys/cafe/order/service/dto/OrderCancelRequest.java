package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "주문 취소 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelRequest {

    @Schema(description = "회원 ID (본인 확인용)", example = "1", required = true)
    private Long memberId;

}
