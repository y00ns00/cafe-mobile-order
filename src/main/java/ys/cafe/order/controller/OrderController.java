package ys.cafe.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ys.cafe.order.service.OrderService;
import ys.cafe.order.service.dto.OrderCancelRequest;
import ys.cafe.order.service.dto.OrderCreateRequest;
import ys.cafe.order.service.dto.OrderResponse;

import java.util.List;

@Tag(name = "Order", description = "주문 관리 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "주문 생성",
            description = "상품을 주문하고 결제를 진행합니다. 결제가 성공하면 주문이 생성되고, 실패하면 주문이 취소됩니다."
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody OrderCreateRequest request
    ) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "주문 단건 조회",
            description = "주문 ID로 특정 주문의 상세 정보를 조회합니다."
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "주문 ID", required = true, example = "1")
            @PathVariable Long orderId
    ) {
        OrderResponse response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "전체 주문 조회",
            description = "시스템의 모든 주문 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> responses = orderService.getAllOrders();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "주문 취소",
            description = "주문을 취소합니다. 본인의 주문만 취소할 수 있으며, 결제 취소도 함께 진행됩니다. " +
                    "서빙 중이거나 완료된 주문은 취소할 수 없습니다."
    )
    @PostMapping(value = "/{orderId}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "주문 ID", required = true, example = "1")
            @PathVariable Long orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "주문 취소 요청 (회원 ID 포함)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderCancelRequest.class))
            )
            @RequestBody OrderCancelRequest request
    ) {
        OrderResponse response = orderService.cancelOrder(orderId, request.getMemberId());
        return ResponseEntity.ok(response);
    }
}
