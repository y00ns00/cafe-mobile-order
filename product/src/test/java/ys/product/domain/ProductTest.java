package ys.product.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    @DisplayName("Product를 생성할 수 있다")
    void createProduct() {
        // given
        ProductName name = ProductName.of("아메리카노");
        String description = "신선한 원두로 만든 아메리카노";
        List<ImageUrl> images = Arrays.asList(
                ImageUrl.of("http://example.com/image1.jpg"),
                ImageUrl.of("http://example.com/image2.jpg")
        );
        Won price = Won.of(4500);
        ProductStatus status = ProductStatus.AVAILABLE;

        // when
        Product product = Product.create(name, description, images, price, status);

        // then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
        assertThat(product.getImages()).hasSize(2);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("Product의 정보를 수정할 수 있다")
    void updateProduct() {
        // given
        Product product = Product.create(
                ProductName.of("아메리카노"),
                "설명",
                List.of(ImageUrl.of("http://example.com/old.jpg")),
                Won.of(4000),
                ProductStatus.AVAILABLE
        );

        ProductName newName = ProductName.of("카페라떼");
        String newDescription = "새로운 설명";
        List<ImageUrl> newImages = List.of(ImageUrl.of("http://example.com/new.jpg"));
        Won newPrice = Won.of(5000);

        // when
        product.update(newName, newDescription, newImages, newPrice);

        // then
        assertThat(product.getName()).isEqualTo(newName);
        assertThat(product.getDescription()).isEqualTo(newDescription);
        assertThat(product.getImages()).hasSize(1);
        assertThat(product.getImages().get(0).getUrl()).isEqualTo("http://example.com/new.jpg");
        assertThat(product.getPrice()).isEqualTo(newPrice);
    }

    @Test
    @DisplayName("Product의 상태를 변경할 수 있다")
    void changeProductStatus() {
        // given
        Product product = Product.create(
                ProductName.of("아메리카노"),
                "설명",
                List.of(ImageUrl.of("http://example.com/image.jpg")),
                Won.of(4500),
                ProductStatus.AVAILABLE
        );

        // when
        product.changeStatus(ProductStatus.SOLD_OUT);

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }

    @Test
    @DisplayName("상태가 AVAILABLE이 아니면 hasNotAvailable이 true를 반환한다")
    void hasNotAvailable() {
        // given
        Product product = Product.create(
                ProductName.of("아메리카노"),
                "설명",
                List.of(ImageUrl.of("http://example.com/image.jpg")),
                Won.of(4500),
                ProductStatus.SOLD_OUT
        );

        // when
        boolean result = product.hasNotAvailable();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("상태가 AVAILABLE이면 hasNotAvailable이 false를 반환한다")
    void hasNotAvailableWithAvailableStatus() {
        // given
        Product product = Product.create(
                ProductName.of("아메리카노"),
                "설명",
                List.of(ImageUrl.of("http://example.com/image.jpg")),
                Won.of(4500),
                ProductStatus.AVAILABLE
        );

        // when
        boolean result = product.hasNotAvailable();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("HIDDEN 상태일 때 hasNotAvailable이 true를 반환한다")
    void hasNotAvailableWithHiddenStatus() {
        // given
        Product product = Product.create(
                ProductName.of("아메리카노"),
                "설명",
                List.of(ImageUrl.of("http://example.com/image.jpg")),
                Won.of(4500),
                ProductStatus.HIDDEN
        );

        // when
        boolean result = product.hasNotAvailable();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("DISCONTINUED 상태일 때 hasNotAvailable이 true를 반환한다")
    void hasNotAvailableWithDiscontinuedStatus() {
        // given
        Product product = Product.create(
                ProductName.of("아메리카노"),
                "설명",
                List.of(ImageUrl.of("http://example.com/image.jpg")),
                Won.of(4500),
                ProductStatus.DISCONTINUED
        );

        // when
        boolean result = product.hasNotAvailable();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이미지가 없는 Product를 생성할 수 있다")
    void createProductWithoutImages() {
        // given
        ProductName name = ProductName.of("아메리카노");
        String description = "설명";
        List<ImageUrl> images = List.of();
        Won price = Won.of(4500);
        ProductStatus status = ProductStatus.AVAILABLE;

        // when
        Product product = Product.create(name, description, images, price, status);

        // then
        assertThat(product.getImages()).isEmpty();
    }

    @Test
    @DisplayName("여러 개의 이미지를 가진 Product를 생성할 수 있다")
    void createProductWithMultipleImages() {
        // given
        List<ImageUrl> images = Arrays.asList(
                ImageUrl.of("http://example.com/image1.jpg"),
                ImageUrl.of("http://example.com/image2.jpg"),
                ImageUrl.of("http://example.com/image3.jpg")
        );

        // when
        Product product = Product.create(
                ProductName.of("아메리카노"),
                "설명",
                images,
                Won.of(4500),
                ProductStatus.AVAILABLE
        );

        // then
        assertThat(product.getImages()).hasSize(3);
    }
}
