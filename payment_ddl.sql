-- Payment 테이블 DDL

-- MySQL/MariaDB
CREATE TABLE payment (
    payment_key VARCHAR(100) NOT NULL COMMENT '결제 키 (Primary Key)',
    order_id BIGINT NOT NULL COMMENT '주문 ID',
    member_id BIGINT NOT NULL COMMENT '회원 ID',
    price DECIMAL(19,0) NOT NULL COMMENT '결제 금액',
    currency VARCHAR(255) COMMENT '통화 (KRW)',
    status VARCHAR(20) NOT NULL COMMENT '결제 상태 (PENDING, SUCCESS, FAILED, CANCELED, CANCEL_COMPLETED)',
    transaction_id VARCHAR(100) COMMENT '외부 결제 시스템 트랜잭션 ID',
    created_at TIMESTAMP NOT NULL COMMENT '생성 일시',
    updated_at TIMESTAMP NULL COMMENT '수정 일시',
    PRIMARY KEY (payment_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='결제 정보';

-- 인덱스 추가 (성능 최적화)
CREATE INDEX idx_payment_order_id ON payment(order_id);
CREATE INDEX idx_payment_member_id ON payment(member_id);
CREATE INDEX idx_payment_status ON payment(status);
CREATE INDEX idx_payment_created_at ON payment(created_at);

-- PostgreSQL
-- CREATE TABLE payment (
--     payment_key VARCHAR(100) NOT NULL,
--     order_id BIGINT NOT NULL,
--     member_id BIGINT NOT NULL,
--     price DECIMAL(19,0) NOT NULL,
--     currency VARCHAR(255),
--     status VARCHAR(20) NOT NULL,
--     transaction_id VARCHAR(100),
--     created_at TIMESTAMP NOT NULL,
--     updated_at TIMESTAMP,
--     PRIMARY KEY (payment_key)
-- );
--
-- CREATE INDEX idx_payment_order_id ON payment(order_id);
-- CREATE INDEX idx_payment_member_id ON payment(member_id);
-- CREATE INDEX idx_payment_status ON payment(status);
-- CREATE INDEX idx_payment_created_at ON payment(created_at);
--
-- COMMENT ON TABLE payment IS '결제 정보';
-- COMMENT ON COLUMN payment.payment_key IS '결제 키 (Primary Key)';
-- COMMENT ON COLUMN payment.order_id IS '주문 ID';
-- COMMENT ON COLUMN payment.member_id IS '회원 ID';
-- COMMENT ON COLUMN payment.price IS '결제 금액';
-- COMMENT ON COLUMN payment.currency IS '통화 (KRW)';
-- COMMENT ON COLUMN payment.status IS '결제 상태 (PENDING, SUCCESS, FAILED, CANCELED, CANCEL_COMPLETED)';
-- COMMENT ON COLUMN payment.transaction_id IS '외부 결제 시스템 트랜잭션 ID';
-- COMMENT ON COLUMN payment.created_at IS '생성 일시';
-- COMMENT ON COLUMN payment.updated_at IS '수정 일시';
