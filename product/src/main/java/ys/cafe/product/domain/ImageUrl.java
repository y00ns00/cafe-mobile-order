package ys.cafe.product.domain;

import jakarta.persistence.Embeddable;
import ys.cafe.product.exception.ProductValidationException;
import ys.cafe.product.exception.errorcode.ProductValidationErrorCode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Embeddable
public class ImageUrl {

    private static final int MAX_LENGTH = 500;

    private String url;

    protected ImageUrl() {}

    private ImageUrl(String url) {
        validateUrl(url);
        this.url = url;
    }

    private static void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new ProductValidationException(ProductValidationErrorCode.IMAGE_URL_REQUIRED);
        }

        String trimmed = url.trim();

        if (trimmed.length() > MAX_LENGTH) {
            throw new ProductValidationException(ProductValidationErrorCode.IMAGE_URL_TOO_LONG);
        }

        try {
            new URL(trimmed);
        } catch (MalformedURLException e) {
            throw new ProductValidationException(ProductValidationErrorCode.IMAGE_URL_INVALID_FORMAT);
        }
    }

    public static ImageUrl of(String url) {
        return new ImageUrl(url);
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageUrl imageUrl = (ImageUrl) o;
        return Objects.equals(url, imageUrl.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return url;
    }
}