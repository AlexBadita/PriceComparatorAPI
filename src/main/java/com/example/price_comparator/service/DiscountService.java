package com.example.price_comparator.service;

import com.example.price_comparator.dto.DiscountDTO;
import com.example.price_comparator.model.Discount;
import com.example.price_comparator.repository.DiscountRepository;
import com.example.price_comparator.utils.PriceHelpers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {

//    private final LocalDate today = LocalDate.now();
    private final LocalDate today = LocalDate.of(2025, 5, 3);

    private final DiscountRepository discountRepository;
    private final DiscountMapperService discountMapper;
    private final PriceHelpers priceHelpers;

    // Get all discounts
    public List<DiscountDTO> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }

    // Get all active discounts
    public List<DiscountDTO> getActiveDiscounts(LocalDate date) {
        return discountRepository.findAll().stream()
                .filter(d -> priceHelpers.isDiscountActive(d, date))
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves all discounts with the specified entry date.
     *
     * @param entryDate the date to filter discounts by
     * @return list of DiscountDTOs for the given entry date
     */
    public List<DiscountDTO> getDiscountsByEntryDate(LocalDate entryDate) {
        return discountRepository.findByEntryDate(entryDate).stream()
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all discounts for a specific store by store ID.
     *
     * @param storeId the ID of the store
     * @return list of DiscountDTOs for the specified store
     */
    public List<DiscountDTO> getDiscountsByStore(Long storeId) {
        return discountRepository.findByStoreId(storeId).stream()
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }

    public List<DiscountDTO> getProductsWithHighestCurrentDiscount() {
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(today);

        // Group discounts by product id and get max percentage discount per product
        Map<String, Discount> maxDiscountsPerProduct = activeDiscounts.stream()
                .collect(Collectors.toMap(
                        d -> d.getProduct().getId(),
                        d -> d,
                        (d1, d2) -> d1.getPercentage().compareTo(d2.getPercentage()) > 0 ? d1 : d2));

        // Map to DTOs (assumes you have a mapper)
        return maxDiscountsPerProduct.values().stream()
                .sorted((d1, d2) -> d2.getPercentage().compareTo(d1.getPercentage()))
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves discounts that have been newly added (last 24 hours):
     * discounts with entryDate up to today, which started yesterday or later, and are active today.
     *
     * @return list of DiscountDTOs representing recently added and active discounts
     */
    public List<DiscountDTO> getNewDiscounts() {
        LocalDate yesterday = today.minusDays(1);

        // Fetch discounts with entryDate <= today
        List<Discount> discountsUpToToday = discountRepository.findByEntryDateLessThanEqual(today);

        // Filter those that are active and started from yesterday onwards
        List<Discount> recentActiveDiscounts = discountsUpToToday.stream()
                .filter(d -> !d.getFromDate().isBefore(yesterday))  // started yesterday or later
                .filter(d -> priceHelpers.isDiscountActive(d, today))  // active today
                .toList();

        return recentActiveDiscounts.stream()
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }
}
