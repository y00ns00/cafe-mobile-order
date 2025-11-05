package ys.cafe.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ys.cafe.payment.service.PaymentService;
import ys.cafe.payment.service.dto.PaymentCancelRequest;
import ys.cafe.payment.service.dto.PaymentInfoResponse;
import ys.cafe.payment.service.dto.PaymentListResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Payment", description = "결제 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 정보 조회", description = "결제 키로 결제 정보를 조회합니다")
    @GetMapping("/{paymentKey}")
    public ResponseEntity<PaymentInfoResponse> getPaymentInfo(
            @Parameter(description = "결제 키", required = true, example = "test-payment-key")
            @PathVariable String paymentKey
    ) {
        PaymentInfoResponse response = paymentService.getPaymentInfo(paymentKey);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 결제 목록 조회", description = "회원 ID로 해당 사용자의 모든 결제 정보를 조회합니다")
    @GetMapping("/members/{memberId}")
    public ResponseEntity<PaymentListResponse> getMemberPayments(
            @Parameter(description = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId
    ) {
        PaymentListResponse response = paymentService.getUserPayments(memberId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 취소", description = "주문 ID로 결제를 취소합니다")
    @PostMapping("/cancel")
    public ResponseEntity<PaymentInfoResponse> cancelPayment(
            @RequestBody PaymentCancelRequest request
    ) {
        PaymentInfoResponse paymentInfoResponse = paymentService.cancelPayment(request.getOrderId());

        return ResponseEntity.ok(paymentInfoResponse);
    }
}