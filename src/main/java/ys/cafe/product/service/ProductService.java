package ys.cafe.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ys.cafe.common.exception.CommonErrorCode;
import ys.cafe.common.exception.CommonException;
import ys.cafe.common.vo.Won;
import ys.cafe.product.domain.*;
import ys.cafe.product.service.dto.CreateProductRequest;
import ys.cafe.product.service.dto.ProductResponse;
import ys.cafe.product.service.dto.UpdateProductRequest;
import ys.cafe.product.exception.ProductDomainException;
import ys.cafe.product.exception.errorcode.ProductDomainErrorCode;
import ys.cafe.product.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.create(
                ProductName.of(request.name()),
                request.description(),
                request.imageUrls().stream()
                        .map(ImageUrl::of)
                        .collect(Collectors.toList()),
                Won.of(request.price()),
                ProductStatus.fromString(request.status())
        );

        Product savedProduct = productRepository.save(product);
        return toProductResponse(savedProduct);
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다. ID: " + productId));
        return toProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        Iterable<Product> products = productRepository.findAll();
        return StreamSupport.stream(products.spliterator(), false)
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getAvailableProductsByIds(List<Long> productIds) {
        if(productIds.isEmpty()) {
            return List.of();
        }

        List<Product> productList = productRepository.findAllByProductIds(productIds);
        Products products = Products.from(productList);

        if(products.hasUnavailableProducts()) {
            List<Product> unavailableProducts = products.getUnavailableProducts();
            throw new ProductDomainException(ProductDomainErrorCode.PRODUCT_NOT_AVAILABLE, "판매할 수 없는 상품이 포함되어 있습니다: " + unavailableProducts);
        }

        return products.getAvailableProducts().stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다. ID: " + productId));

        product.update(
                ProductName.of(request.name()),
                request.description(),
                request.imageUrls().stream()
                        .map(ImageUrl::of)
                        .collect(Collectors.toList()),
                Won.of(request.price())
        );

        return toProductResponse(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다. ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Transactional
    public ProductResponse changeProductStatus(Long productId, String status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다. ID: " + productId));

        product.changeStatus(ProductStatus.fromString(status));

        return toProductResponse(product);
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName().getValue(),
                product.getDescription(),
                product.getImages().stream()
                        .map(imageUrl -> imageUrl.getUrl())
                        .collect(Collectors.toList()),
                product.getPrice().toString(),
                product.getStatus().name()
        );
    }


}
