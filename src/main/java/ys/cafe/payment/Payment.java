package ys.cafe.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Payment {

    @Id
    String paymentKey;

    @Column
    Long orderId;

    @Column
    String paymentStatus;

    @Column
    String paymentMethod;

    @Column
    String paymentUrl;



}
