package com.example.price_comparator.repository;

import com.example.price_comparator.model.Price;
import com.example.price_comparator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByProduct(Product product);
}
