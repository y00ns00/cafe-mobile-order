package ys.cafe.product.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ys.cafe.product.domain.Product;
import ys.cafe.product.domain.ProductMother;
import ys.product.domain.*;
import ys.cafe.product.service.dto.CreateProductRequest;
import ys.cafe.product.service.dto.ProductResponse;
import ys.cafe.product.service.dto.UpdateProductRequest;
import ys.cafe.product.common.CommonException;
import ys.cafe.product.exception.ProductDomainException;
import ys.cafe.product.exception.ProductValidationException;
import ys.cafe.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품을 생성할 수 있다")
    void createProduct() {
        // given
        CreateProductRequest request = new CreateProductRequest(
                "아메리카노",
                "신선한 원두로 만든 아메리카노",
                List.of("http://example.com/americano.jpg"),
                new BigDecimal("4500"),
                "AVAILABLE"
        );

        Product savedProduct = ProductMother.createAvailableProduct();
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);

        // when
        ProductResponse response = productService.createProduct(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("아메리카노");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품을 ID로 조회할 수 있다")
    void getProduct() {
        // given
        Long productId = 1L;
        Product product = ProductMother.createAvailableProduct();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.getProduct(productId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("아메리카노");
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 조회하면 예외가 발생한다")
    void getProduct_NotFound() {
        // given
        Long productId = 999L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProduct(productId))
                .isInstanceOf(CommonException.class);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("모든 상품을 조회할 수 있다")
    void getAllProducts() {
        // given
        List<Product> products = Arrays.asList(
                ProductMother.createAvailableProduct("아메리카노", 4500),
                ProductMother.createAvailableProduct("카페라떼", 5000),
                ProductMother.createAvailableProduct("카푸치노", 5500)
        );
        given(productRepository.findAll()).willReturn(products);

        // when
        List<ProductResponse> responses = productService.getAllProducts();

        // then
        assertThat(responses).hasSize(3);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("빈 리스트로 판매 가능한 상품을 조회하면 빈 리스트를 반환한다")
    void getAvailableProductsByIds_EmptyList() {
        // given
        List<Long> productIds = List.of();

        // when
        List<ProductResponse> responses = productService.getAvailableProductsByIds(productIds);

        // then
        assertThat(responses).isEmpty();
        verify(productRepository, never()).findAllByProductIds(any());
    }

    @Test
    @DisplayName("ID 목록으로 판매 가능한 상품들을 조회할 수 있다")
    void getAvailableProductsByIds() {
        // given
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        List<Product> products = Arrays.asList(
                ProductMother.createAvailableProduct("아메리카노", 4500),
                ProductMother.createAvailableProduct("카페라떼", 5000),
                ProductMother.createAvailableProduct("카푸치노", 5500)
        );
        given(productRepository.findAllByProductIds(productIds)).willReturn(products);

        // when
        List<ProductResponse> responses = productService.getAvailableProductsByIds(productIds);

        // then
        assertThat(responses).hasSize(3);
        verify(productRepository, times(1)).findAllByProductIds(productIds);
    }

    @Test
    @DisplayName("판매 불가능한 상품이 포함되어 있으면 예외가 발생한다")
    void getAvailableProductsByIds_WithUnavailableProducts() {
        // given
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        List<Product> products = Arrays.asList(
                ProductMother.createAvailableProduct("아메리카노", 4500),
                ProductMother.createSoldOutProduct("카페라떼", 5000),
                ProductMother.createAvailableProduct("카푸치노", 5500)
        );
        given(productRepository.findAllByProductIds(productIds)).willReturn(products);

        // when & then
        assertThatThrownBy(() -> productService.getAvailableProductsByIds(productIds))
                .isInstanceOf(ProductDomainException.class);
        verify(productRepository, times(1)).findAllByProductIds(productIds);
    }

    @Test
    @DisplayName("상품을 수정할 수 있다")
    void updateProduct() {
        // given
        Long productId = 1L;
        UpdateProductRequest request = new UpdateProductRequest(
                "카페라떼",
                "부드러운 우유와 에스프레소의 조화",
                List.of("http://example.com/latte.jpg"),
                new BigDecimal("5000"),
                "AVAILABLE"
        );

        Product product = ProductMother.createAvailableProduct();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.updateProduct(productId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("카페라떼");
        assertThat(response.price()).isEqualTo("5000원");
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 수정하면 예외가 발생한다")
    void updateProduct_NotFound() {
        // given
        Long productId = 999L;
        UpdateProductRequest request = new UpdateProductRequest(
                "카페라떼",
                "부드러운 카페라떼",
                List.of("http://example.com/latte.jpg"),
                new BigDecimal("5000"),
                "AVAILABLE"
        );
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.updateProduct(productId, request))
                .isInstanceOf(CommonException.class);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품을 삭제할 수 있다")
    void deleteProduct() {
        // given
        Long productId = 1L;
        given(productRepository.existsById(productId)).willReturn(true);

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 삭제하면 예외가 발생한다")
    void deleteProduct_NotFound() {
        // given
        Long productId = 999L;
        given(productRepository.existsById(productId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(CommonException.class);
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("상품의 상태를 변경할 수 있다")
    void changeProductStatus() {
        // given
        Long productId = 1L;
        String newStatus = "SOLD_OUT";
        Product product = ProductMother.createAvailableProduct();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.changeProductStatus(productId, newStatus);

        // then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("SOLD_OUT");
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 상품의 상태를 변경하면 예외가 발생한다")
    void changeProductStatus_NotFound() {
        // given
        Long productId = 999L;
        String newStatus = "SOLD_OUT";
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.changeProductStatus(productId, newStatus))
                .isInstanceOf(CommonException.class);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("유효하지 않은 상태로 변경하면 예외가 발생한다")
    void changeProductStatus_InvalidStatus() {
        // given
        Long productId = 1L;
        String invalidStatus = "INVALID_STATUS";
        Product product = ProductMother.createAvailableProduct();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> productService.changeProductStatus(productId, invalidStatus))
                .isInstanceOf(ProductValidationException.class);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("AVAILABLE에서 HIDDEN으로 상태를 변경할 수 있다")
    void changeProductStatus_AvailableToHidden() {
        // given
        Long productId = 1L;
        String newStatus = "HIDDEN";
        Product product = ProductMother.createAvailableProduct();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.changeProductStatus(productId, newStatus);

        // then
        assertThat(response.status()).isEqualTo("HIDDEN");
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("AVAILABLE에서 DISCONTINUED로 상태를 변경할 수 있다")
    void changeProductStatus_AvailableToDiscontinued() {
        // given
        Long productId = 1L;
        String newStatus = "DISCONTINUED";
        Product product = ProductMother.createAvailableProduct();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.changeProductStatus(productId, newStatus);

        // then
        assertThat(response.status()).isEqualTo("DISCONTINUED");
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("모든 상품이 판매 불가능하면 빈 리스트를 반환한다")
    void getAvailableProductsByIds_AllUnavailable() {
        // given
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        List<Product> products = Arrays.asList(
                ProductMother.createSoldOutProduct("아메리카노", 4500),
                ProductMother.createHiddenProduct("카페라떼", 5000),
                ProductMother.createDiscontinuedProduct("카푸치노", 5500)
        );
        given(productRepository.findAllByProductIds(productIds)).willReturn(products);

        // when & then
        assertThatThrownBy(() -> productService.getAvailableProductsByIds(productIds))
                .isInstanceOf(ProductDomainException.class);
        verify(productRepository, times(1)).findAllByProductIds(productIds);
    }
}
