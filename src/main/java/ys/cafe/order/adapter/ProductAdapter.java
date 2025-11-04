package ys.cafe.order.adapter;

import ys.cafe.order.service.dto.ProductDTO;

import java.util.List;

/**
 * Product 서비스에 접근하기 위한 어댑터 인터페이스
 * Order 도메인과 외부 Product 서비스 간의 경계를 정의
 */
public interface ProductAdapter {

    /**
     * 여러 상품 ID로 판매 가능한 상품 목록 조회
     *
     * @param productIds 조회할 상품 ID 목록
     * @return 판매 가능한 상품 응답 목록
     */
    List<ProductDTO> findAvailableProductsByIds(List<Long> productIds);
}
