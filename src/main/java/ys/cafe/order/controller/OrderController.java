package ys.cafe.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ys.cafe.order.service.OrderService;
import ys.cafe.order.service.dto.OrderCancelRequest;
import ys.cafe.order.service.dto.OrderCreateRequest;
import ys.cafe.order.service.dto.OrderListResponse;
import ys.cafe.order.service.dto.OrderResponse;

@Tag(name = "Order", description = "주문 관리 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "주문 생성",
            description = "상품을 주문하고 결제를 진행합니다. 결제가 성공하면 주문이 생성되고, 실패하면 주문이 취소됩니다."
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderCreateRequest request
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
            @PathVariable @Positive(message = "주문 ID는 1 이상이어야 합니다.") Long orderId
    ) {
        OrderResponse response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "전체 주문 조회",
            description = "시스템의 모든 주문 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<OrderListResponse> getAllOrders(
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "페이지는 0 이상이어야 합니다.") int page,
            @RequestParam(defaultValue = "20") @Positive(message = "페이지 크기는 1 이상이어야 합니다.") int size
    ) {
        OrderListResponse response = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "회원별 주문 조회",
            description = "특정 회원의 모든 주문 내역을 조회합니다."
    )
    @GetMapping("/members/{memberId}")
    public ResponseEntity<OrderListResponse> getMemberOrders(
            @Parameter(description = "회원 ID", required = true, example = "1")
            @PathVariable @Positive(message = "회원 ID는 1 이상이어야 합니다.") Long memberId
    ) {
        OrderListResponse response = orderService.getMemberOrders(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "주문 취소",
            description = "주문을 취소합니다. 본인의 주문만 취소할 수 있으며, 결제 취소도 함께 진행됩니다. 서빙 중이거나 완료된 주문은 취소할 수 없습니다."
    )
    @PostMapping(value = "/{orderId}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "주문 ID", required = true, example = "1")
            @PathVariable @Positive(message = "주문 ID는 1 이상이어야 합니다.") Long orderId,
            @Valid @RequestBody OrderCancelRequest request
    ) {
        OrderResponse response = orderService.cancelOrder(orderId, request.memberId());
        return ResponseEntity.ok(response);
    }
}
