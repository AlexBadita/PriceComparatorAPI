package com.example.price_comparator.repository;

import com.example.price_comparator.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, String> {
    Optional<Store> findByName(String name);
}
