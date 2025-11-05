package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "주문 취소 요청")
public record OrderCancelRequest(

        @Schema(description = "회원 ID (본인 확인용)", example = "1", requiredMode = REQUIRED)
        @NotNull(message = "회원 ID는 필수입니다.")
        @Min(value = 1, message = "회원 ID는 1 이상이어야 합니다.")
        Long memberId
) {
}
