package ys.cafe.order.port;

import ys.cafe.order.service.dto.ProductDTO;

import java.util.List;

/**
 * Product 아웃바운드 포트
 * Order 도메인에서 Product 서비스에 접근하기 위한 인터페이스
 */
public interface ProductPort {

    /**
     * 여러 상품 ID로 판매 가능한 상품 목록 조회
     *
     * @param productIds 조회할 상품 ID 목록
     * @return 판매 가능한 상품 응답 목록
     */
    List<ProductDTO> findAvailableProductsByIds(List<Long> productIds);
}
