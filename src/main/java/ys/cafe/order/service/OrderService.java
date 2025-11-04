package ys.cafe.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.order.adapter.ProductAdapter;
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
    private final ProductAdapter productAdapter;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest orderCreateRequest) {
        // 1. orderLines에서 productId 목록 추출
        List<Long> productIds = orderCreateRequest.getOrderLines().stream()
                .map(OrderLineCreateRequest::getProductId)
                .toList();

        // 2-4. ProductResponse로 변환
        List<ProductDTO> products = productAdapter.findAvailableProductsByIds(productIds);

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


}
