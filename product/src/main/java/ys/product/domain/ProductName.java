package ys.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ys.product.exception.ProductValidationException;
import ys.product.exception.errorcode.ProductValidationErrorCode;

import java.util.Objects;

@Embeddable
public class ProductName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    @Column(name = "name")
    private String value;

    protected ProductName() {}

    private ProductName(String value) {
        validateName(value);
        this.value = value.trim();
    }

    private static void validateName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ProductValidationException(ProductValidationErrorCode.PRODUCT_NAME_REQUIRED);
        }

        String trimmed = value.trim();

        if (trimmed.length() < MIN_LENGTH) {
            throw new ProductValidationException(ProductValidationErrorCode.PRODUCT_NAME_TOO_SHORT);
        }

        if (trimmed.length() > MAX_LENGTH) {
            throw new ProductValidationException(ProductValidationErrorCode.PRODUCT_NAME_TOO_LONG);
        }
    }

    public static ProductName of(String value) {
        return new ProductName(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductName that = (ProductName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}