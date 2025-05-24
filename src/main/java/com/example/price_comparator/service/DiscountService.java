package com.example.price_comparator.service;

import com.example.price_comparator.model.Discount;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class DiscountService {
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
