package ys.cafe.product.domain;

import java.util.List;

import ys.cafe.common.vo.Won;

/**
 * ObjectMother 패턴을 사용한 Product 테스트 객체 생성 유틸리티
 */
public class ProductMother {

    public static Product createAvailableProduct() {
        return Product.create(
                ProductName.of("아메리카노"),
                "신선한 원두로 만든 아메리카노",
                List.of(ImageUrl.of("http://example.com/americano.jpg")),
                Won.of(4500),
                ProductStatus.AVAILABLE
        );
    }

    public static Product createAvailableProduct(String name, long price) {
        return Product.create(
                ProductName.of(name),
                name + " 설명",
                List.of(ImageUrl.of("http://example.com/" + name + ".jpg")),
                Won.of(price),
                ProductStatus.AVAILABLE
        );
    }

    public static Product createSoldOutProduct() {
        return Product.create(
                ProductName.of("카페라떼"),
                "부드러운 우유와 에스프레소의 조화",
                List.of(ImageUrl.of("http://example.com/latte.jpg")),
                Won.of(5000),
                ProductStatus.SOLD_OUT
        );
    }

    public static Product createSoldOutProduct(String name, long price) {
        return Product.create(
                ProductName.of(name),
                name + " 설명",
                List.of(ImageUrl.of("http://example.com/" + name + ".jpg")),
                Won.of(price),
                ProductStatus.SOLD_OUT
        );
    }

    public static Product createHiddenProduct() {
        return Product.create(
                ProductName.of("바닐라라떼"),
                "달콤한 바닐라 시럽이 들어간 라떼",
                List.of(ImageUrl.of("http://example.com/vanilla-latte.jpg")),
                Won.of(5500),
                ProductStatus.HIDDEN
        );
    }

    public static Product createHiddenProduct(String name, long price) {
        return Product.create(
                ProductName.of(name),
                name + " 설명",
                List.of(ImageUrl.of("http://example.com/" + name + ".jpg")),
                Won.of(price),
                ProductStatus.HIDDEN
        );
    }

    public static Product createDiscontinuedProduct() {
        return Product.create(
                ProductName.of("카라멜마끼아또"),
                "카라멜 시럽과 에스프레소의 만남",
                List.of(ImageUrl.of("http://example.com/caramel.jpg")),
                Won.of(6000),
                ProductStatus.DISCONTINUED
        );
    }

    public static Product createDiscontinuedProduct(String name, long price) {
        return Product.create(
                ProductName.of(name),
                name + " 설명",
                List.of(ImageUrl.of("http://example.com/" + name + ".jpg")),
                Won.of(price),
                ProductStatus.DISCONTINUED
        );
    }

    public static Product createProductWithStatus(ProductStatus status) {
        return Product.create(
                ProductName.of("테스트 상품"),
                "테스트용 상품",
                List.of(ImageUrl.of("http://example.com/test.jpg")),
                Won.of(3000),
                status
        );
    }

    public static Product createProductWithoutImages() {
        return Product.create(
                ProductName.of("이미지 없는 상품"),
                "이미지가 없는 상품입니다",
                List.of(),
                Won.of(3000),
                ProductStatus.AVAILABLE
        );
    }

    public static Product createProductWithMultipleImages() {
        return Product.create(
                ProductName.of("다중 이미지 상품"),
                "여러 이미지를 가진 상품",
                List.of(
                        ImageUrl.of("http://example.com/image1.jpg"),
                        ImageUrl.of("http://example.com/image2.jpg"),
                        ImageUrl.of("http://example.com/image3.jpg")
                ),
                Won.of(7000),
                ProductStatus.AVAILABLE
        );
    }
}
