package ys.cafe.payment.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "결제 목록 응답")
public record PaymentListResponse(

        @Schema(description = "결제 목록")
        List<PaymentInfoResponse> payments,

        @Schema(description = "총 결제 개수", example = "5")
        int totalCount
) {
    public static PaymentListResponse of(List<PaymentInfoResponse> payments) {
        return new PaymentListResponse(payments, payments.size());
    }
}
