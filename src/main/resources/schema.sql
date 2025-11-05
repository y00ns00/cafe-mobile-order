create schema `cafe-order`;

use `cafe-order`;

-- member 테이블
CREATE TABLE member
(
    member_id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '멤버 ID',
    password               VARCHAR(255) NOT NULL COMMENT '비밀번호',
    last_name              VARCHAR(50)  NOT NULL COMMENT '성',
    first_name             VARCHAR(50)  NOT NULL COMMENT '이름',
    phone_number           VARCHAR(11)  NOT NULL COMMENT '휴대폰 번호',
    gender                 VARCHAR(20)  NOT NULL COMMENT '성별',
    birth_date             DATE         NOT NULL COMMENT '생년월일 (yyyy-MM-dd)',
    registration_date_time DATETIME     NOT NULL COMMENT '가입일자',
    status                 VARCHAR(20)  NOT NULL COMMENT '회원 상태 (ACTIVE, WITHDRAW_REQUESTED, DELETED)',
    withdraw_request_at    DATETIME COMMENT '회원 탈퇴 요청 일자 (yyyy-MM-dd HH:mm:ss)',
    PRIMARY KEY (member_id),
    UNIQUE KEY uk_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '회원 정보';


-- Product 테이블
CREATE TABLE product
(
    product_id  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상품 ID',
    name        VARCHAR(100)   NOT NULL COMMENT '상품명',
    description TEXT COMMENT '상품 설명',
    price       DECIMAL(19, 0) NOT NULL COMMENT '가격',
    currency    VARCHAR(10) COMMENT '통화 (KRW)',
    status      VARCHAR(50)    NOT NULL COMMENT '상품 상태 (AVAILABLE, SOLD_OUT, HIDDEN, DISCONTINUED)',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX       idx_status (status),
    INDEX       idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품';

-- Product 이미지 테이블 (ElementCollection)
CREATE TABLE product_images
(
    product_id BIGINT       NOT NULL COMMENT '상품 ID',
    url        VARCHAR(500) NOT NULL COMMENT '이미지 URL',
    FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE CASCADE,
    INDEX      idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 이미지';

-- Order 테이블
CREATE TABLE `orders`
(
    order_id        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 ID',
    member_id       BIGINT         NOT NULL COMMENT '회원 ID',
    order_status    VARCHAR(50)    NOT NULL COMMENT '주문 상태 (PAYMENT_WAITING, PREPARING, PAYMENT_FAILED, SERVE, COMPLETED, CANCELED)',
    order_date_time DATETIME       NOT NULL COMMENT '주문 일시',
    total_price     DECIMAL(19, 0) NOT NULL COMMENT '총 가격',
    currency        VARCHAR(10) COMMENT '통화 (KRW)',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX           idx_member_id (member_id),
    INDEX           idx_order_status (order_status),
    INDEX           idx_order_date_time (order_date_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문';

-- OrderLine 테이블
CREATE TABLE order_line
(
    order_line_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 항목 ID',
    order_id      BIGINT COMMENT '주문 ID',
    product_id    BIGINT         NOT NULL COMMENT '상품 ID',
    product_name  VARCHAR(100)   NOT NULL COMMENT '상품명',
    quantity      INT            NOT NULL COMMENT '수량',
    price         DECIMAL(19, 0) NOT NULL COMMENT '단가',
    currency      VARCHAR(10) COMMENT '통화 (KRW)',
    FOREIGN KEY (order_id) REFERENCES `orders` (order_id) ON DELETE CASCADE,
    INDEX         idx_order_id (order_id),
    INDEX         idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문 항목';


-- Payment 테이블 DDL

CREATE TABLE payment
(
    payment_key    VARCHAR(100)   NOT NULL COMMENT '결제 키 (Primary Key)',
    order_id       BIGINT         NOT NULL COMMENT '주문 ID',
    member_id      BIGINT         NOT NULL COMMENT '회원 ID',
    price          DECIMAL(19, 0) NOT NULL COMMENT '결제 금액',
    currency       VARCHAR(255) COMMENT '통화 (KRW)',
    status         VARCHAR(20)    NOT NULL COMMENT '결제 상태 (PENDING, SUCCESS, FAILED, CANCELED, CANCEL_COMPLETED)',
    transaction_id VARCHAR(100) COMMENT '외부 결제 시스템 트랜잭션 ID',
    created_at     TIMESTAMP      NOT NULL COMMENT '생성 일시',
    updated_at     TIMESTAMP NULL COMMENT '수정 일시',
    PRIMARY KEY (payment_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='결제 정보';

-- 인덱스 추가 (성능 최적화)
CREATE INDEX idx_payment_order_id ON payment (order_id);
CREATE INDEX idx_payment_member_id ON payment (member_id);
CREATE INDEX idx_payment_status ON payment (status);
CREATE INDEX idx_payment_created_at ON payment (created_at);

