package com.example.price_comparator.repository;

import com.example.price_comparator.model.Price;
import com.example.price_comparator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByProduct(Product product);
    List<Price> findByProductId(String productId);

    @Query("SELECT p FROM Price p WHERE p.product.id = :productId ORDER BY p.entryDate ASC")
    List<Price> findByProductIdOrderByEntryDateAsc(@Param("productId") String productId);

    @Query("SELECT p FROM Price p WHERE p.product.id = :productId AND p.store.id = :storeId ORDER BY p.entryDate ASC")
    List<Price> findByProductIdAndStoreIdOrderByEntryDateAsc(
            @Param("productId") String productId,
            @Param("storeId") Long storeId);
}
