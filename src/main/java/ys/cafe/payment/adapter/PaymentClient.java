package ys.cafe.payment.adapter;

import ys.cafe.payment.service.dto.PaymentResponse;

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
