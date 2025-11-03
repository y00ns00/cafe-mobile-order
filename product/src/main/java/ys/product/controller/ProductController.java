package ys.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ys.product.dto.CreateProductRequest;
import ys.product.dto.ProductResponse;
import ys.product.dto.UpdateProductRequest;
import ys.product.service.ProductService;

import java.util.List;

@Tag(name = "Product", description = "상품 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 생성", description = "새로운 상품을 등록합니다.")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> createProduct(@Validated @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "상품 조회", description = "상품 ID로 상품을 조회합니다.")
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 상품 조회", description = "모든 상품을 조회합니다.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responses = productService.getAllProducts();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "판매 가능한 상품 조회", description = "상품 ID 목록으로 판매 가능한 상품들을 조회합니다.")
    @PostMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponse>> getAvailableProducts(@RequestBody List<Long> productIds) {
        List<ProductResponse> responses = productService.getAvailableProductsByIds(productIds);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다.")
    @PutMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Validated @RequestBody UpdateProductRequest request
    ) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품 상태 변경", description = "상품의 판매 상태를 변경합니다.")
    @PatchMapping(value = "/{productId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> changeProductStatus(
            @PathVariable Long productId,
            @RequestParam String status
    ) {
        ProductResponse response = productService.changeProductStatus(productId, status);
        return ResponseEntity.ok(response);
    }
}
