package com.example.price_comparator.service;

import com.example.price_comparator.dto.DiscountDTO;
import com.example.price_comparator.model.Discount;
import com.example.price_comparator.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountMapperService discountMapper;

    // Get all discounts
    public List<DiscountDTO> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }

    // Helper functions

    public boolean isActive(Discount discount, LocalDate selectedDate){
        LocalDate date = Optional.ofNullable(selectedDate).orElse(LocalDate.now());

        return (!date.isBefore(discount.getFromDate()) &&
                !date.isAfter(discount.getToDate()));
    }

    public boolean isUpcoming(Discount discount, LocalDate selectedDate) {
        LocalDate date = Optional.ofNullable(selectedDate).orElse(LocalDate.now());

        return date.isBefore(discount.getFromDate());
    }

    public boolean isExpired(Discount discount, LocalDate selectedDate) {
        LocalDate date = Optional.ofNullable(selectedDate).orElse(LocalDate.now());

        return date.isAfter(discount.getToDate());
    }

    public BigDecimal applyDiscount(BigDecimal originalPrice, BigDecimal discountPercentage){
        BigDecimal discountMultiplier = BigDecimal.ONE
                .subtract(discountPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

        return originalPrice.multiply(discountMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
