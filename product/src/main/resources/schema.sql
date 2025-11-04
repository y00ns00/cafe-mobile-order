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