package ys.cafe.order.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "주문 생성 요청")
public record OrderCreateRequest(

        @Schema(description = "회원 ID", example = "1", requiredMode = REQUIRED)
        @NotNull(message = "회원 ID는 필수입니다.")
        Long memberId,

        @Schema(description = "주문 항목 목록", requiredMode = REQUIRED)
        @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다.")
        List<@Valid OrderLineCreateRequest> orderLines
) {
}
