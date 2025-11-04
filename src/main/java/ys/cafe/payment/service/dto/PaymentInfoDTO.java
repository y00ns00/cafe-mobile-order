package ys.cafe.payment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentInfoDTO {

    private final Long orderId;
    private final String name;
    private final String amount;

    public static PaymentInfoDTO of(Long orderId, String name, String amount) {
        return new PaymentInfoDTO(orderId, name, amount);
    }
}