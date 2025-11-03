package ys.product.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Products {

    private final List<Product> products;

    public Products(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    public static Products from(List<Product> products) {
        return new Products(products);
    }

    public List<Product> getUnavailableProducts() {
        return products.stream()
                .filter(Product::hasNotAvailable)
                .collect(Collectors.toList());
    }

    public List<Product> getAvailableProducts() {
        return products.stream()
                .filter(product -> !product.hasNotAvailable())
                .collect(Collectors.toList());
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public int size() {
        return products.size();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public boolean hasUnavailableProducts() {
        return products.stream()
                .anyMatch(Product::hasNotAvailable);
    }
}
