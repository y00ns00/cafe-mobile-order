package ys.product.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ys.product.domain.Product;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("""
                SELECT p
                FROM Product p
                WHERE p.productId in :productIds
            """)
    List<Product> findAllByProductIds(List<Long> productIds);
}
