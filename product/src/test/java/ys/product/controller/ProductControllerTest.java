package ys.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ys.product.common.CommonErrorCode;
import ys.product.common.CommonException;
import ys.product.common.ErrorCodeHttpStatusMapper;
import ys.product.common.GlobalExceptionHandler;
import ys.product.dto.CreateProductRequest;
import ys.product.dto.ProductResponse;
import ys.product.dto.UpdateProductRequest;
import ys.product.service.ProductService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ProductController productController;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        ErrorCodeHttpStatusMapper errorCodeHttpStatusMapper = new ErrorCodeHttpStatusMapper();
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler(errorCodeHttpStatusMapper);

        productController = new ProductController(productService);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Nested
    @DisplayName("상품 생성 API 테스트")
    class CreateProductTest {

        @Test
        @DisplayName("상품 생성 성공")
        public void createProduct() throws Exception {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    "아메리카노",
                    "깊고 진한 아메리카노",
                    List.of("http://example.com/americano.jpg"),
                    new BigDecimal("4500"),
                    "AVAILABLE"
            );

            ProductResponse response = new ProductResponse(
                    1L,
                    "아메리카노",
                    "깊고 진한 아메리카노",
                    List.of("http://example.com/americano.jpg"),
                    "4500원",
                    "AVAILABLE"
            );

            Mockito.when(productService.createProduct(any(CreateProductRequest.class)))
                    .thenReturn(response);

            // when & then
            mockMvc.perform(
                            post("/products")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.productId").value(1))
                    .andExpect(jsonPath("$.name").value("아메리카노"))
                    .andExpect(jsonPath("$.description").value("깊고 진한 아메리카노"))
                    .andExpect(jsonPath("$.imageUrls[0]").value("http://example.com/americano.jpg"))
                    .andExpect(jsonPath("$.price").value("4500원"))
                    .andExpect(jsonPath("$.status").value("AVAILABLE"));
        }

        @Test
        @DisplayName("상품명이 없으면 400 Bad Request 반환")
        public void createProductWithoutName() throws Exception {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    null,
                    "깊고 진한 아메리카노",
                    List.of("http://example.com/americano.jpg"),
                    new BigDecimal("4500"),
                    "AVAILABLE"
            );

            // when & then
            mockMvc.perform(
                            post("/products")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("상품 조회 API 테스트")
    class GetProductTest {

        @Test
        @DisplayName("상품 조회 성공")
        public void getProduct() throws Exception {
            // given
            Long productId = 1L;
            ProductResponse response = new ProductResponse(
                    1L,
                    "아메리카노",
                    "깊고 진한 아메리카노",
                    List.of("http://example.com/americano.jpg"),
                    "4500원",
                    "AVAILABLE"
            );

            Mockito.when(productService.getProduct(productId))
                    .thenReturn(response);

            // when & then
            mockMvc.perform(
                            get("/products/{productId}", productId)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(1))
                    .andExpect(jsonPath("$.name").value("아메리카노"))
                    .andExpect(jsonPath("$.price").value("4500원"));
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회시 404 Not Found 반환")
        public void getProductNotFound() throws Exception {
            // given
            Long productId = 999L;

            Mockito.when(productService.getProduct(productId))
                    .thenThrow(new CommonException(CommonErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다."));

            // when & then
            mockMvc.perform(
                            get("/products/{productId}", productId)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("전체 상품 조회 API 테스트")
    class GetAllProductsTest {

        @Test
        @DisplayName("전체 상품 조회 성공")
        public void getAllProducts() throws Exception {
            // given
            List<ProductResponse> responses = Arrays.asList(
                    new ProductResponse(1L, "아메리카노", "깊고 진한 아메리카노",
                            List.of("http://example.com/americano.jpg"), "4500원", "AVAILABLE"),
                    new ProductResponse(2L, "카페라떼", "부드러운 카페라떼",
                            List.of("http://example.com/latte.jpg"), "5000원", "AVAILABLE")
            );

            Mockito.when(productService.getAllProducts())
                    .thenReturn(responses);

            // when & then
            mockMvc.perform(
                            get("/products")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].productId").value(1))
                    .andExpect(jsonPath("$[0].name").value("아메리카노"))
                    .andExpect(jsonPath("$[1].productId").value(2))
                    .andExpect(jsonPath("$[1].name").value("카페라떼"));
        }
    }

    @Nested
    @DisplayName("판매 가능한 상품 조회 API 테스트")
    class GetAvailableProductsTest {

        @Test
        @DisplayName("판매 가능한 상품 조회 성공")
        public void getAvailableProducts() throws Exception {
            // given
            List<Long> productIds = Arrays.asList(1L, 2L);
            List<ProductResponse> responses = Arrays.asList(
                    new ProductResponse(1L, "아메리카노", "깊고 진한 아메리카노",
                            List.of("http://example.com/americano.jpg"), "4500원", "AVAILABLE"),
                    new ProductResponse(2L, "카페라떼", "부드러운 카페라떼",
                            List.of("http://example.com/latte.jpg"), "5000원", "AVAILABLE")
            );

            Mockito.when(productService.getAvailableProductsByIds(productIds))
                    .thenReturn(responses);

            // when & then
            mockMvc.perform(
                            post("/products/available")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(productIds))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].productId").value(1))
                    .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                    .andExpect(jsonPath("$[1].productId").value(2))
                    .andExpect(jsonPath("$[1].status").value("AVAILABLE"));
        }
    }

    @Nested
    @DisplayName("상품 수정 API 테스트")
    class UpdateProductTest {

        @Test
        @DisplayName("상품 수정 성공")
        public void updateProduct() throws Exception {
            // given
            Long productId = 1L;
            UpdateProductRequest request = new UpdateProductRequest(
                    "아메리카노 (수정)",
                    "새롭게 개선된 아메리카노",
                    List.of("http://example.com/new-americano.jpg"),
                    new BigDecimal("5000"),
                    "AVAILABLE"
            );

            ProductResponse response = new ProductResponse(
                    1L,
                    "아메리카노 (수정)",
                    "새롭게 개선된 아메리카노",
                    List.of("http://example.com/new-americano.jpg"),
                    "5000원",
                    "AVAILABLE"
            );

            Mockito.when(productService.updateProduct(eq(productId), any(UpdateProductRequest.class)))
                    .thenReturn(response);

            // when & then
            mockMvc.perform(
                            put("/products/{productId}", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(1))
                    .andExpect(jsonPath("$.name").value("아메리카노 (수정)"))
                    .andExpect(jsonPath("$.description").value("새롭게 개선된 아메리카노"))
                    .andExpect(jsonPath("$.price").value("5000원"));
        }

        @Test
        @DisplayName("존재하지 않는 상품 수정시 404 Not Found 반환")
        public void updateProductNotFound() throws Exception {
            // given
            Long productId = 999L;
            UpdateProductRequest request = new UpdateProductRequest(
                    "아메리카노",
                    "깊고 진한 아메리카노",
                    List.of("http://example.com/americano.jpg"),
                    new BigDecimal("4500"),
                    "AVAILABLE"
            );

            Mockito.when(productService.updateProduct(eq(productId), any(UpdateProductRequest.class)))
                    .thenThrow(new CommonException(CommonErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다."));

            // when & then
            mockMvc.perform(
                            put("/products/{productId}", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("상품 삭제 API 테스트")
    class DeleteProductTest {

        @Test
        @DisplayName("상품 삭제 성공")
        public void deleteProduct() throws Exception {
            // given
            Long productId = 1L;

            Mockito.doNothing().when(productService).deleteProduct(productId);

            // when & then
            mockMvc.perform(
                            delete("/products/{productId}", productId)
                    )
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제시 404 Not Found 반환")
        public void deleteProductNotFound() throws Exception {
            // given
            Long productId = 999L;

            Mockito.doThrow(new CommonException(CommonErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다."))
                    .when(productService).deleteProduct(productId);

            // when & then
            mockMvc.perform(
                            delete("/products/{productId}", productId)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("상품 상태 변경 API 테스트")
    class ChangeProductStatusTest {

        @Test
        @DisplayName("상품 상태 변경 성공")
        public void changeProductStatus() throws Exception {
            // given
            Long productId = 1L;
            String newStatus = "SOLD_OUT";

            ProductResponse response = new ProductResponse(
                    1L,
                    "아메리카노",
                    "깊고 진한 아메리카노",
                    List.of("http://example.com/americano.jpg"),
                    "4500원",
                    "SOLD_OUT"
            );

            Mockito.when(productService.changeProductStatus(productId, newStatus))
                    .thenReturn(response);

            // when & then
            mockMvc.perform(
                            patch("/products/{productId}/status", productId)
                                    .param("status", newStatus)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(1))
                    .andExpect(jsonPath("$.status").value("SOLD_OUT"));
        }

        @Test
        @DisplayName("존재하지 않는 상품의 상태 변경시 404 Not Found 반환")
        public void changeProductStatusNotFound() throws Exception {
            // given
            Long productId = 999L;
            String newStatus = "SOLD_OUT";

            Mockito.when(productService.changeProductStatus(productId, newStatus))
                    .thenThrow(new CommonException(CommonErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다."));

            // when & then
            mockMvc.perform(
                            patch("/products/{productId}/status", productId)
                                    .param("status", newStatus)
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
