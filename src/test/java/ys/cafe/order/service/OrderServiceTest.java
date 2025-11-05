package ys.cafe.order.service;

import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ys.cafe.order.domain.Order;
import ys.cafe.order.domain.OrderLine;
import ys.cafe.order.domain.OrderStatus;
import ys.cafe.order.domain.vo.Won;
import ys.cafe.order.exception.OrderValidationException;
import ys.cafe.order.objectmother.*;
import ys.cafe.order.out.port.PaymentPort;
import ys.cafe.order.out.port.ProductPort;
import ys.cafe.order.repository.OrderRepository;
import ys.cafe.order.service.dto.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 테스트")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductPort productPort;

    @Mock
    private PaymentPort paymentPort;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 시 결제가 성공하면 주문 상태가 PREPARING으로 변경된다")
    void placeOrder_Success() {
        // given
        Long memberId = 1L;
        OrderCreateRequest orderCreateRequest = OrderCreateRequestMother.requestForMember(memberId);
        List<ProductDTO> availableProducts = ProductDTOMother.availableProducts();
        Map<Long, ProductDTO> productById = availableProducts.stream().collect(Collectors.toMap(ProductDTO::productId, Function.identity()));

        List<OrderLine> orderLines = orderCreateRequest.getOrderLines().stream()
                .map(request -> {
                    return OrderLineMother.orderLine(
                            request.getProductId(),
                            productById.get(request.getProductId()).name(),
                            request.getQuantity(),
                            productById.get(request.getProductId()).price()
                    );
                }).toList();

        when(productPort.findAvailableProductsByIds(List.of(1L, 2L))).thenReturn(availableProducts);
        when(orderRepository.save(any(Order.class))).thenReturn(OrderMother.orderWithLinesAndId(10L, memberId, orderLines));
        when(paymentPort.processPayment(anyLong(), anyLong(), any(Won.class)))
                .thenReturn(true);

        // when
        OrderResponse response = orderService.placeOrder(orderCreateRequest);

        // then
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PREPARING);
        assertThat(response.getTotalPrice()).isEqualTo("14000");
        assertThat(response.getOrderLines()).hasSize(2);

        verify(paymentPort).processPayment(eq(10L), eq(memberId), eq(Won.of("14000")));
        verify(productPort).findAvailableProductsByIds(List.of(1L, 2L));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 시 대상 상품이 존재하지 않으면 예외가 발생한다")
    void placeOrder_ProductNotFound() {
        // given
        Long memberId = 1L;
        when(productPort.findAvailableProductsByIds(List.of(1L, 2L)))
                .thenReturn(List.of(ProductDTO.of(1L, "아메리카노", "4500")));

        // when & then
        assertThatThrownBy(() -> orderService.placeOrder(
                OrderCreateRequestMother.requestForMember(
                        memberId,
                        List.of(
                                OrderLineCreateRequestMother.orderLine(1L, 2),
                                OrderLineCreateRequestMother.orderLine(2L, 1)
                        )
                )))
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");

        verify(orderRepository, never()).save(any());
        verify(paymentPort, never()).processPayment(anyLong(), anyLong(), any());
    }

    @Test
    @DisplayName("주문 생성 시 결제가 실패하면 주문 상태가 PAYMENT_FAILED가 된다")
    void placeOrder_PaymentFailed() {

        // given
        Long memberId = 1L;
        Long orderId = 20L;
        List<ProductDTO> availableProducts = ProductDTOMother.availableProducts();
        OrderCreateRequest orderCreateRequest = OrderCreateRequestMother.requestForMember(memberId);

        when(productPort.findAvailableProductsByIds(List.of(1L, 2L))).thenReturn(availableProducts);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = (Order) invocation.getArgument(0);
            OrderMother.setField(order, "orderId", orderId);
            return order;
        } );

        when(paymentPort.processPayment(anyLong(), anyLong(), any(Won.class)))
                .thenReturn(false);

        // when
        OrderResponse response = orderService.placeOrder(orderCreateRequest);

        // then
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        verify(paymentPort).processPayment(eq(orderId), eq(memberId), any(Won.class));
    }

    @Test
    @DisplayName("주문 ID로 주문을 조회할 수 있다")
    void getOrder_Success() {
        // given
        Long memberId = 1L;
        Long orderId = 30L;
        Order order = OrderMother.orderWithId(orderId, memberId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        OrderResponse response = orderService.getOrder(orderId);

        // then
        assertThat(response.getOrderId()).isEqualTo(orderId);
        assertThat(response.getMemberId()).isEqualTo(memberId);
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID를 조회하면 예외가 발생한다")
    void getOrder_NotFound() {
        // given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("주문을 찾을 수 없습니다");

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("전체 주문 목록을 조회할 수 있다")
    void getAllOrders_Success() {
        // given
        Long memberId = 1L;
        Order order1 = OrderMother.orderWithId(100L, memberId);
        Order order2 = OrderMother.orderWithId(101L, memberId);
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // when
        OrderListResponse response = orderService.getAllOrders();

        // then
        assertThat(response.getOrders()).hasSize(2);
        assertThat(response.getTotalCount()).isEqualTo(2);
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("회원 ID로 주문 목록을 조회할 수 있다")
    void getMemberOrders_Success() {
        // given
        Long memberId = 1L;
        Order order1 = OrderMother.orderWithId(200L, memberId);
        Order order2 = OrderMother.orderWithId(201L, memberId);
        when(orderRepository.findByMemberId(memberId)).thenReturn(List.of(order1, order2));

        // when
        OrderListResponse response = orderService.getMemberOrders(memberId);

        // then
        assertThat(response.getOrders()).hasSize(2);
        assertThat(response.getTotalCount()).isEqualTo(2);
        verify(orderRepository).findByMemberId(memberId);
    }

    @Test
    @DisplayName("주문 취소 시 회원이 일치하면 결제 취소를 호출한다")
    void cancelOrder_Success() {
        // given
        Long memberId = 1L;
        Long orderId = 300L;
        Order order = OrderMother.orderWithId(orderId, memberId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        OrderResponse response = orderService.cancelOrder(orderId, memberId);

        // then
        assertThat(response.getOrderId()).isEqualTo(orderId);
        verify(paymentPort).cancelPayment(orderId);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("주문 취소 시 다른 회원이면 예외가 발생한다")
    void cancelOrder_InvalidMember() {
        // given
        Long memberId = 1L;
        Long orderId = 400L;
        Order order = OrderMother.orderWithId(orderId, memberId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, 999L))
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("본인의 주문만 취소할 수 있습니다");

        verify(paymentPort, never()).cancelPayment(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문 취소 시 주문을 찾을 수 없으면 예외가 발생한다")
    void cancelOrder_OrderNotFound() {
        // given
        Long memberId = 1L;
        Long orderId = 500L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, memberId))
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("주문을 찾을 수 없습니다");

        verify(paymentPort, never()).cancelPayment(anyLong());
        verify(orderRepository, never()).save(any());
    }

}
