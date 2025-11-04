package ys.cafe.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductsTest {

    @Test
    @DisplayName("Products를 생성할 수 있다")
    void createProducts() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct(),
                ProductMother.createSoldOutProduct()
        );

        // when
        Products products = Products.from(productList);

        // then
        assertThat(products.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 Products를 생성할 수 있다")
    void createEmptyProducts() {
        // given
        List<Product> productList = List.of();

        // when
        Products products = Products.from(productList);

        // then
        assertThat(products.isEmpty()).isTrue();
        assertThat(products.size()).isZero();
    }

    @Test
    @DisplayName("판매 가능한 상품만 필터링할 수 있다")
    void getAvailableProducts() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct("아메리카노", 4500),
                ProductMother.createSoldOutProduct("카페라떼", 5000),
                ProductMother.createAvailableProduct("카푸치노", 5500),
                ProductMother.createHiddenProduct("바닐라라떼", 6000)
        );
        Products products = Products.from(productList);

        // when
        List<Product> availableProducts = products.getAvailableProducts();

        // then
        assertThat(availableProducts).hasSize(2);
        assertThat(availableProducts)
                .allMatch(product -> product.getStatus() == ProductStatus.AVAILABLE);
    }

    @Test
    @DisplayName("판매 불가능한 상품만 필터링할 수 있다")
    void getUnavailableProducts() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct("아메리카노", 4500),
                ProductMother.createSoldOutProduct("카페라떼", 5000),
                ProductMother.createHiddenProduct("바닐라라떼", 6000),
                ProductMother.createDiscontinuedProduct("카라멜마끼아또", 6500)
        );
        Products products = Products.from(productList);

        // when
        List<Product> unavailableProducts = products.getUnavailableProducts();

        // then
        assertThat(unavailableProducts).hasSize(3);
        assertThat(unavailableProducts)
                .allMatch(Product::hasNotAvailable);
    }

    @Test
    @DisplayName("모든 상품이 판매 가능하면 판매 불가능한 상품이 없다")
    void getUnavailableProducts_AllAvailable() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct("아메리카노", 4500),
                ProductMother.createAvailableProduct("카페라떼", 5000)
        );
        Products products = Products.from(productList);

        // when
        List<Product> unavailableProducts = products.getUnavailableProducts();

        // then
        assertThat(unavailableProducts).isEmpty();
    }

    @Test
    @DisplayName("판매 불가능한 상품이 있는지 확인할 수 있다")
    void hasUnavailableProducts() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct("아메리카노", 4500),
                ProductMother.createSoldOutProduct("카페라떼", 5000)
        );
        Products products = Products.from(productList);

        // when
        boolean hasUnavailable = products.hasUnavailableProducts();

        // then
        assertThat(hasUnavailable).isTrue();
    }

    @Test
    @DisplayName("모든 상품이 판매 가능하면 hasUnavailableProducts가 false를 반환한다")
    void hasUnavailableProducts_AllAvailable() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct("아메리카노", 4500),
                ProductMother.createAvailableProduct("카페라떼", 5000)
        );
        Products products = Products.from(productList);

        // when
        boolean hasUnavailable = products.hasUnavailableProducts();

        // then
        assertThat(hasUnavailable).isFalse();
    }

    @Test
    @DisplayName("빈 Products는 판매 불가능한 상품이 없다")
    void hasUnavailableProducts_Empty() {
        // given
        Products products = Products.from(List.of());

        // when
        boolean hasUnavailable = products.hasUnavailableProducts();

        // then
        assertThat(hasUnavailable).isFalse();
    }

    @Test
    @DisplayName("Products의 크기를 확인할 수 있다")
    void size() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct(),
                ProductMother.createSoldOutProduct(),
                ProductMother.createHiddenProduct()
        );
        Products products = Products.from(productList);

        // when
        int size = products.size();

        // then
        assertThat(size).isEqualTo(3);
    }

    @Test
    @DisplayName("Products가 비어있는지 확인할 수 있다")
    void isEmpty() {
        // given
        Products products = Products.from(List.of());

        // when
        boolean empty = products.isEmpty();

        // then
        assertThat(empty).isTrue();
    }

    @Test
    @DisplayName("Products가 비어있지 않으면 isEmpty가 false를 반환한다")
    void isNotEmpty() {
        // given
        List<Product> productList = List.of(ProductMother.createAvailableProduct());
        Products products = Products.from(productList);

        // when
        boolean empty = products.isEmpty();

        // then
        assertThat(empty).isFalse();
    }

    @Test
    @DisplayName("getProducts는 모든 상품 목록을 반환한다")
    void getProducts() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct(),
                ProductMother.createSoldOutProduct(),
                ProductMother.createHiddenProduct()
        );
        Products products = Products.from(productList);

        // when
        List<Product> allProducts = products.getProducts();

        // then
        assertThat(allProducts).hasSize(3);
    }

    @Test
    @DisplayName("getProducts로 반환된 리스트를 수정해도 원본에 영향을 주지 않는다 (방어적 복사)")
    void getProducts_DefensiveCopy() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct(),
                ProductMother.createSoldOutProduct()
        );
        Products products = Products.from(productList);

        // when
        List<Product> allProducts = products.getProducts();
        allProducts.add(ProductMother.createHiddenProduct());

        // then
        assertThat(products.size()).isEqualTo(2);
        assertThat(allProducts).hasSize(3);
    }

    @Test
    @DisplayName("모든 상태의 상품을 포함할 수 있다")
    void containsAllStatuses() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createProductWithStatus(ProductStatus.AVAILABLE),
                ProductMother.createProductWithStatus(ProductStatus.SOLD_OUT),
                ProductMother.createProductWithStatus(ProductStatus.HIDDEN),
                ProductMother.createProductWithStatus(ProductStatus.DISCONTINUED)
        );
        Products products = Products.from(productList);

        // when & then
        assertThat(products.size()).isEqualTo(4);
        assertThat(products.getAvailableProducts()).hasSize(1);
        assertThat(products.getUnavailableProducts()).hasSize(3);
        assertThat(products.hasUnavailableProducts()).isTrue();
    }

    @Test
    @DisplayName("SOLD_OUT 상품만 필터링할 수 있다")
    void filterBySoldOut() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct(),
                ProductMother.createSoldOutProduct(),
                ProductMother.createSoldOutProduct("모카", 5500),
                ProductMother.createHiddenProduct()
        );
        Products products = Products.from(productList);

        // when
        List<Product> unavailableProducts = products.getUnavailableProducts();
        long soldOutCount = unavailableProducts.stream()
                .filter(p -> p.getStatus() == ProductStatus.SOLD_OUT)
                .count();

        // then
        assertThat(soldOutCount).isEqualTo(2);
    }

    @Test
    @DisplayName("HIDDEN 상품만 필터링할 수 있다")
    void filterByHidden() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct(),
                ProductMother.createHiddenProduct(),
                ProductMother.createHiddenProduct("모카", 5500),
                ProductMother.createSoldOutProduct()
        );
        Products products = Products.from(productList);

        // when
        List<Product> unavailableProducts = products.getUnavailableProducts();
        long hiddenCount = unavailableProducts.stream()
                .filter(p -> p.getStatus() == ProductStatus.HIDDEN)
                .count();

        // then
        assertThat(hiddenCount).isEqualTo(2);
    }

    @Test
    @DisplayName("DISCONTINUED 상품만 필터링할 수 있다")
    void filterByDiscontinued() {
        // given
        List<Product> productList = Arrays.asList(
                ProductMother.createAvailableProduct(),
                ProductMother.createDiscontinuedProduct(),
                ProductMother.createDiscontinuedProduct("모카", 5500),
                ProductMother.createSoldOutProduct()
        );
        Products products = Products.from(productList);

        // when
        List<Product> unavailableProducts = products.getUnavailableProducts();
        long discontinuedCount = unavailableProducts.stream()
                .filter(p -> p.getStatus() == ProductStatus.DISCONTINUED)
                .count();

        // then
        assertThat(discontinuedCount).isEqualTo(2);
    }
}
