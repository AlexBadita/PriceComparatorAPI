package com.example.price_comparator.repository;

import com.example.price_comparator.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, String> {
}
