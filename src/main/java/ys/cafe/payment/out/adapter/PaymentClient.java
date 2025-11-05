package ys.cafe.payment.out.adapter;

import ys.cafe.payment.service.dto.response.PaymentResponse;

import java.util.concurrent.CompletableFuture;

public interface PaymentClient {

    PaymentResponse pay(
            String name,
            String birthDate,
            String phone,
            String amount
    );

    CompletableFuture<PaymentResponse> cancel(
            String paymentKey
    );
}
