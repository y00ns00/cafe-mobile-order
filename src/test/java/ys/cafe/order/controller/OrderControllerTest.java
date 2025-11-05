package ys.cafe.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ys.cafe.order.objectmother.OrderCreateRequestMother;
import ys.cafe.order.objectmother.OrderMother;
import ys.cafe.order.service.OrderService;
import ys.cafe.order.service.dto.OrderCancelRequest;
import ys.cafe.order.service.dto.OrderCreateRequest;
import ys.cafe.order.service.dto.OrderListResponse;
import ys.cafe.order.service.dto.OrderResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderController 테스트")
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("주문 생성 성공")
    void placeOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequestMother.requestForMember(1L);
        OrderResponse response = OrderResponse.from(OrderMother.orderWithId(10L, 1L));

        given(orderService.placeOrder(any(OrderCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.memberId").value(1));

        verify(orderService).placeOrder(any(OrderCreateRequest.class));
    }

    @Test
    @DisplayName("주문 단건 조회 성공")
    void getOrder() throws Exception {
        // given
        Long orderId = 20L;
        OrderResponse response = OrderResponse.from(OrderMother.orderWithId(orderId, 1L));

        given(orderService.getOrder(orderId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.memberId").value(1));

        verify(orderService).getOrder(orderId);
    }

    @Test
    @DisplayName("전체 주문 조회 성공")
    void getAllOrders() throws Exception {
        // given
        List<OrderResponse> orders = List.of(
                OrderResponse.from(OrderMother.orderWithId(1L, 1L)),
                OrderResponse.from(OrderMother.orderWithId(2L, 1L))
        );
        OrderListResponse response = OrderListResponse.of(orders, 2, 0, 20, 1, false);

        given(orderService.getAllOrders(0, 20)).willReturn(response);

        // when & then
        mockMvc.perform(get("/orders")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders").isArray())
                .andExpect(jsonPath("$.orders[0].orderId").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(orderService).getAllOrders(0, 20);
    }

    @Test
    @DisplayName("회원별 주문 조회 성공")
    void getMemberOrders() throws Exception {
        // given
        Long memberId = 1L;
        List<OrderResponse> orders = List.of(
                OrderResponse.from(OrderMother.orderWithId(100L, memberId)),
                OrderResponse.from(OrderMother.orderWithId(101L, memberId))
        );
        OrderListResponse response = OrderListResponse.unpaged(orders);

        given(orderService.getMemberOrders(memberId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/orders/members/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders").isArray())
                .andExpect(jsonPath("$.orders[0].orderId").value(100))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(orderService).getMemberOrders(memberId);
    }

    @Test
    @DisplayName("주문 취소 성공")
    void cancelOrder() throws Exception {
        // given
        Long orderId = 50L;
        Long memberId = 1L;
        OrderCancelRequest request = new OrderCancelRequest(memberId);
        OrderResponse response = OrderResponse.from(OrderMother.orderWithId(orderId, memberId));

        given(orderService.cancelOrder(orderId, memberId)).willReturn(response);

        // when & then
        mockMvc.perform(post("/orders/{orderId}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.memberId").value(memberId));

        verify(orderService).cancelOrder(orderId, memberId);
    }
}
