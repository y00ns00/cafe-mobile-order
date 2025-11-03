package ys.product.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Embedded
    private ProductName name;

    @Column
    private String description;

    @ElementCollection
    private List<ImageUrl> images = new ArrayList<>();

    @Embedded
    private Won price;

    // 판매 가능 상품 여부
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    protected Product() {}

    public static Product create(
            ProductName name,
            String description,
            List<ImageUrl> images,
            Won price,
            ProductStatus status
    ) {
        Product product = new Product();
        product.name = name;
        product.description = description;
        product.images = images;
        product.price = price;
        product.status = status;

        return product;
    }

    public void update(
            ProductName name,
            String description,
            List<ImageUrl> images,
            Won price
    ) {
        this.name = name;
        this.description = description;
        this.images = images;
        this.price = price;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    public Long getProductId() {
        return productId;
    }

    public ProductName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ImageUrl> getImages() {
        return images;
    }

    public Won getPrice() {
        return price;
    }

    public ProductStatus getStatus() {
        return status;
    }


    public boolean hasNotAvailable() {
        return status != ProductStatus.AVAILABLE;
    }
}
