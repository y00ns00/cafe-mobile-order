-- Product 테이블
CREATE TABLE product (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상품 ID',
    name VARCHAR(100) NOT NULL COMMENT '상품명',
    description TEXT COMMENT '상품 설명',
    price DECIMAL(19, 0) NOT NULL COMMENT '가격',
    currency VARCHAR(10) COMMENT '통화 (KRW)',
    status VARCHAR(50) NOT NULL COMMENT '상품 상태 (AVAILABLE, SOLD_OUT, HIDDEN, DISCONTINUED)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_status (status),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품';

-- Product 이미지 테이블 (ElementCollection)
CREATE TABLE product_images (
    product_id BIGINT NOT NULL COMMENT '상품 ID',
    url VARCHAR(500) NOT NULL COMMENT '이미지 URL',
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 이미지';

-- Order 테이블
CREATE TABLE `order` (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 ID',
    member_id BIGINT NOT NULL COMMENT '회원 ID',
    order_status VARCHAR(50) NOT NULL COMMENT '주문 상태 (PAYMENT_WAITING, PREPARING, COMPLETED, CANCELED)',
    order_date_time DATETIME NOT NULL COMMENT '주문 일시',
    total_price DECIMAL(19, 0) NOT NULL COMMENT '총 가격',
    currency VARCHAR(10) COMMENT '통화 (KRW)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_member_id (member_id),
    INDEX idx_order_status (order_status),
    INDEX idx_order_date_time (order_date_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문';

-- OrderLine 테이블
CREATE TABLE order_line (
    order_line_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 항목 ID',
    order_id BIGINT NOT NULL COMMENT '주문 ID',
    product_id BIGINT NOT NULL COMMENT '상품 ID',
    product_name VARCHAR(100) NOT NULL COMMENT '상품명',
    quantity INT NOT NULL COMMENT '수량',
    price DECIMAL(19, 0) NOT NULL COMMENT '단가',
    currency VARCHAR(10) COMMENT '통화 (KRW)',
    FOREIGN KEY (order_id) REFERENCES `order`(order_id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문 항목';