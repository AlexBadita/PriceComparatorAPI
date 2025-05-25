package com.example.price_comparator.repository;

import com.example.price_comparator.model.Discount;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByProductAndStore(Product product, Store store);
    List<Discount> findByEntryDate(LocalDate entryDate);
    List<Discount> findByEntryDateLessThanEqual(LocalDate date);

    List<Discount> findByProductAndStoreAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            Product product,
            Store store,
            LocalDate fromDate,
            LocalDate toDate
    );

    @Query("SELECT d FROM Discount d WHERE d.store.id = :storeId")
    List<Discount> findByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT d FROM Discount d WHERE :today BETWEEN d.fromDate AND d.toDate")
    List<Discount> findActiveDiscounts(@Param("today") LocalDate today);

    @Query("SELECT d FROM Discount d WHERE d.product.id = :productId AND d.store.id = :storeId")
    List<Discount> findByProductIdAndStoreId(
            @Param("productId") String productId,
            @Param("storeId") Long storeId);
}
