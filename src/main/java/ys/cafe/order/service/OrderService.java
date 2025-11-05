package ys.cafe.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.order.port.PaymentPort;
import ys.cafe.order.port.ProductPort;
import ys.cafe.order.domain.Order;
import ys.cafe.order.domain.OrderLine;
import ys.cafe.order.exception.OrderValidationException;
import ys.cafe.order.exception.errorcode.OrderValidationErrorCode;
import ys.cafe.order.repository.OrderRepository;
import ys.cafe.order.service.dto.OrderCreateRequest;
import ys.cafe.order.service.dto.OrderLineCreateRequest;
import ys.cafe.order.service.dto.OrderResponse;
import ys.cafe.order.service.dto.ProductDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductPort productPort;
    private final PaymentPort paymentPort;

    @Transactional
    public OrderResponse placeOrder(OrderCreateRequest orderCreateRequest) {
        // 1. 요청된 상품 ID 목록 추출
        List<Long> requestedProductIds = orderCreateRequest.getOrderLines().stream()
                .map(OrderLineCreateRequest::getProductId)
                .toList();

        // 2. 사용 가능한 상품 조회 (한 번의 쿼리로 조회)
        List<ProductDTO> availableProducts = productPort.findAvailableProductsByIds(requestedProductIds);

        // 3. 조회된 상품 ID를 Set으로 관리 (빠른 검색을 위해)
        Set<Long> availableProductIds = availableProducts.stream()
                .map(ProductDTO::productId)
                .collect(Collectors.toSet());

        // 4. 요청된 상품이 모두 존재하는지 검증 (Fail Fast)
        validateAllProductsExist(requestedProductIds, availableProductIds);

        // 5. 빠른 조회를 위한 상품 맵 생성
        Map<Long, ProductDTO> productMap = availableProducts.stream()
                .collect(Collectors.toMap(ProductDTO::productId, product -> product));

        // 6. OrderLine 생성 (검증 완료되어 null 체크 불필요)
        List<OrderLine> orderLines = orderCreateRequest.getOrderLines().stream()
                .map(request -> createOrderLine(request, productMap))
                .toList();

        // 7. Order 생성 및 저장 (ID 생성을 위해 먼저 저장)
        Order order = Order.create(orderCreateRequest.getMemberId(), orderLines);
        Order savedOrder = orderRepository.save(order);

        // 8. 결제 처리
        boolean paymentSuccess = paymentPort.processPayment(
                savedOrder.getOrderId(),
                savedOrder.getMemberId(),
                savedOrder.getTotalPrice()
        );

        // 9. 결제 결과에 따른 처리
        if (paymentSuccess) {
            savedOrder.completePayment();
        } else {
            savedOrder.failPayment();
        }

        return OrderResponse.from(savedOrder);
    }

    /**
     * 요청된 모든 상품이 존재하는지 검증
     */
    private void validateAllProductsExist(List<Long> requestedProductIds, Set<Long> availableProductIds) {
        List<Long> notFoundProductIds = requestedProductIds.stream()
                .filter(productId -> !availableProductIds.contains(productId))
                .toList();

        if (!notFoundProductIds.isEmpty()) {
            throw new OrderValidationException(
                    OrderValidationErrorCode.ORDER_LINE_PRODUCT_NOT_FOUND,
                    "상품을 찾을 수 없습니다. ID: " + notFoundProductIds
            );
        }
    }

    /**
     * OrderLine 생성
     */
    private OrderLine createOrderLine(OrderLineCreateRequest request, Map<Long, ProductDTO> productMap) {
        ProductDTO product = productMap.get(request.getProductId());
        return OrderLine.create(
                product.productId(),
                product.name(),
                request.getQuantity(),
                product.price()
        );
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderValidationException(
                        OrderValidationErrorCode.ORDER_NOT_FOUND,
                        "주문을 찾을 수 없습니다. ID: " + orderId
                ));
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderResponse::from)
                .toList();
    }

    /**
     * 주문 취소
     * 회원이 주문을 취소하면 결제도 함께 취소됩니다.
     *
     * @param orderId 주문 ID
     * @param memberId 회원 ID (본인 확인용)
     * @return 취소된 주문 응답
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long memberId) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderValidationException(OrderValidationErrorCode.ORDER_NOT_FOUND, "주문을 찾을 수 없습니다. ID: " + orderId));

        // 2. 본인 확인
        if (!order.getMemberId().equals(memberId)) {
            throw new OrderValidationException(OrderValidationErrorCode.ORDER_STATUS_INVALID, "본인의 주문만 취소할 수 있습니다.");
        }

        // 3. 결제 취소 등록 -> 취소등록 후 cronJob으로 최종 취소 처리
        paymentPort.cancelPayment(orderId);

        // 4. 저장
        orderRepository.save(order);

        return OrderResponse.from(order);
    }
}
