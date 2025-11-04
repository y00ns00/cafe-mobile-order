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
        // 1. orderLines에서 productId 목록 추출
        List<Long> productIds = orderCreateRequest.getOrderLines().stream()
                .map(OrderLineCreateRequest::getProductId)
                .toList();

        // 2-4. ProductResponse로 변환
        List<ProductDTO> products = productPort.findAvailableProductsByIds(productIds);

        // 3. productId별로 ProductResponse 매핑
        Map<Long, ProductDTO> productMap = products.stream()
                .collect(Collectors.toMap(ProductDTO::productId, p -> p));

        // 4. OrderLine 생성
        List<OrderLine> orderLines = orderCreateRequest.getOrderLines().stream()
                .map(orderLineCreateRequest -> {
                    ProductDTO product = productMap.get(orderLineCreateRequest.getProductId());
                    if (product == null) {
                        throw new OrderValidationException(
                                OrderValidationErrorCode.ORDER_LINE_PRODUCT_NOT_FOUND,
                                "상품을 찾을 수 없습니다. ID: " + orderLineCreateRequest.getProductId()
                        );
                    }
                    return OrderLine.create(
                            product.productId(),
                            product.name(),
                            orderLineCreateRequest.getQuantity(),
                            product.price()
                    );
                })
                .toList();

        // 5. Order 생성 및 저장
        Order order = Order.create(
                orderCreateRequest.getMemberId(),
                orderLines
        );
        Order savedOrder = orderRepository.save(order);

        // 6. 결제 처리
        boolean paymentResult = paymentPort.processPayment(
                savedOrder.getOrderId(),
                savedOrder.getMemberId(),
                savedOrder.getTotalPrice()
        );

        if (paymentResult) {
            savedOrder.completePayment();
        } else {
            savedOrder.failPayment();
        }

        return OrderResponse.from(savedOrder);
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

        // 3. 주문 취소 (상태 검증 포함)
        order.cancel();

        // 4. 결제 취소 - 실패 시 트랜잭션 롤백
        boolean paymentCancelSuccess = paymentPort.cancelPayment(orderId);
        if (!paymentCancelSuccess) {
            throw new OrderValidationException(
                    OrderValidationErrorCode.ORDER_STATUS_INVALID,
                    "결제 취소에 실패했습니다. 주문 취소가 롤백됩니다."
            );
        }

        // 5. 저장
        orderRepository.save(order);

        return OrderResponse.from(order);
    }
}
