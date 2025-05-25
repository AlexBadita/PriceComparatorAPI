package com.example.price_comparator.service;

import com.example.price_comparator.dto.price_history.PriceHistoryDTO;
import com.example.price_comparator.dto.price_history.PriceHistoryFilter;
import com.example.price_comparator.dto.price_history.PriceHistoryPointDTO;
import com.example.price_comparator.dto.price_history.PriceHistoryStoreDTO;
import com.example.price_comparator.model.Discount;
import com.example.price_comparator.model.Price;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.model.Store;
import com.example.price_comparator.repository.DiscountRepository;
import com.example.price_comparator.repository.PriceRepository;
import com.example.price_comparator.utils.PriceHelpers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for generating historical price timelines for products.
 * It provides functionality to retrieve the pricing and discount history of a given product
 * across different stores, supporting various filtering criteria such as store, brand, category,
 * and date range.
 *
 * The service compiles timelines showing original and final (discounted) prices over time,
 * broken down per store.
 */
@Service
@RequiredArgsConstructor
public class PriceHistoryService {

    private final PriceRepository priceRepository;
    private final DiscountRepository discountRepository;
    private final PriceHelpers priceHelpers;

    /**
     * Retrieves the historical price data for a product, optionally filtered by store, category, brand,
     * and constrained within a specific date range. Also includes any applicable discounts during those periods.
     *
     * @param filter an object containing filter criteria including product name (required),
     *               and optionally store name, category name, brand name, start date, and end date
     * @return a list of PriceHistoryDTO representing the price timeline of each store for the given product,
     *         or null if no prices match the filter
     * @throws IllegalArgumentException if the product name is not provided in the filter
     */
    public List<PriceHistoryDTO> getPriceHistory(PriceHistoryFilter filter) {
        // Validate product ID
        if(filter.getProductName() == null) {
            throw new IllegalArgumentException("Product name is required");
        }

        // Get all prices and filter them
        List<Price> prices = priceRepository.findAll().stream()
                .filter(price -> price.getProduct().getName().equalsIgnoreCase(filter.getProductName()))
                .filter(p -> filter.getStoreName() == null
                        || p.getStore().getName().equals(filter.getStoreName()))
                .filter(p -> filter.getCategoryName() == null
                        || p.getProduct().getCategory().getName().equals(filter.getCategoryName()))
                .filter(p -> filter.getBrandName() == null
                        || p.getProduct().getBrand().getName().equals(filter.getBrandName()))
                .toList();

        if(prices.isEmpty()) {
            return null;
        }

        // Get all discounts and filter them
        List<Discount> discounts = discountRepository.findAll().stream()
                .filter(d -> d.getProduct().getName().equalsIgnoreCase(filter.getProductName()))
                .filter(d -> filter.getStoreName() == null
                        || d.getStore().getName().equals(filter.getStoreName()))
                .toList();

        Map<Product, List<Price>> pricesByProduct = prices.stream()
                .collect(Collectors.groupingBy(Price::getProduct));

        return pricesByProduct.entrySet().stream()
                .map(entry -> buildPriceHistoryForProduct(entry.getKey(), entry.getValue(), discounts, filter))
                .toList();
    }

    private PriceHistoryDTO buildPriceHistoryForProduct(Product product, List<Price> prices, List<Discount> allDiscounts, PriceHistoryFilter filter) {
        // Determine date range
        LocalDate start = prices.stream().map(Price::getEntryDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate end = prices.stream().map(Price::getEntryDate).max(LocalDate::compareTo).orElse(start);

        if (filter.getStartDate() != null) {
            start = filter.getStartDate();
        }

        if (filter.getEndDate() != null) {
            end = filter.getEndDate();
        }

        List<Discount> productDiscounts = allDiscounts.stream()
                .filter(d -> d.getProduct().equals(product))
                .toList();

        // Group prices by store
        Map<Store, List<Price>> pricesByStore = prices.stream()
                .collect(Collectors.groupingBy(Price::getStore));

        // Build timeline per store
        List<PriceHistoryStoreDTO> storeHistory = new ArrayList<>();

        for (Map.Entry<Store, List<Price>> entry : pricesByStore.entrySet()) {
            Store currentStore = entry.getKey();
            List<Price> storePrices = entry.getValue();
            List<Discount> storeDiscounts = productDiscounts.stream()
                    .filter(d -> d.getStore().equals(currentStore))
                    .collect(Collectors.toList());

            storePrices.sort(Comparator.comparing(Price::getEntryDate));
            storeDiscounts.sort(Comparator.comparing(Discount::getFromDate));

            List<PriceHistoryPointDTO> points = new ArrayList<>();

            for (int i = 0; i < storePrices.size(); i++) {
                Price currentPrice = storePrices.get(i);
                LocalDate priceStart = currentPrice.getEntryDate();
                LocalDate priceEnd = (i + 1 < storePrices.size()) ? storePrices.get(i + 1).getEntryDate().minusDays(1) : end;

                // Split the price period by discounts
                LocalDate segmentStart = priceStart;
                while (!segmentStart.isAfter(priceEnd)) {
                    final LocalDate currentSegmentDate = segmentStart;
                    Optional<Discount> active = storeDiscounts.stream()
                            .filter(d -> !d.getFromDate().isAfter(currentSegmentDate) && !d.getToDate().isBefore(currentSegmentDate))
                            .findFirst();

                    LocalDate segmentEnd = active.map(d -> d.getToDate().isBefore(priceEnd) ? d.getToDate() : priceEnd).orElse(priceEnd);

                    PriceHistoryPointDTO point = new PriceHistoryPointDTO();
                    point.setStartDate(segmentStart);
                    point.setEndDate(segmentEnd);
                    point.setOriginalPrice(currentPrice.getPrice());

                    if (active.isPresent()) {
                        Discount d = active.get();
                        BigDecimal discounted = priceHelpers.applyDiscount(currentPrice.getPrice(), d.getPercentage());
                        point.setFinalPrice(discounted);
                        point.setDiscountedPercentage(d.getPercentage());
                    } else {
                        point.setFinalPrice(currentPrice.getPrice());
                        point.setDiscountedPercentage(BigDecimal.ZERO);
                    }

                    points.add(point);
                    segmentStart = segmentEnd.plusDays(1);
                }
            }

            storeHistory.add(new PriceHistoryStoreDTO(currentStore.getName(), points));
        }

        return new PriceHistoryDTO(
                product.getId(),
                product.getName(),
                product.getBrand().getName(),
                product.getCategory().getName(),
                storeHistory
        );
    }
}
