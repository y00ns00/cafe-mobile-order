package ys.cafe.payment.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "결제 목록 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentListResponse {

    @Schema(description = "결제 목록")
    private List<PaymentInfoResponse> payments;

    @Schema(description = "총 결제 개수", example = "5")
    private int totalCount;

    public static PaymentListResponse of(List<PaymentInfoResponse> payments) {
        return new PaymentListResponse(payments, payments.size());
    }
}
