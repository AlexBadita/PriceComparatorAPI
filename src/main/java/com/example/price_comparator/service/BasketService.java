package com.example.price_comparator.service;

import com.example.price_comparator.dto.basket.BasketItemDTO;
import com.example.price_comparator.dto.basket.ProductWithBestOffer;
import com.example.price_comparator.dto.basket.StoreBasketDTO;
import com.example.price_comparator.exception.ResourceNotFoundException;
import com.example.price_comparator.model.Discount;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.model.Store;
import com.example.price_comparator.repository.DiscountRepository;
import com.example.price_comparator.repository.ProductRepository;
import com.example.price_comparator.repository.StoreRepository;
import com.example.price_comparator.utils.PriceHelpers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/**
 * Service responsible for optimizing a shopping basket by finding the best prices
 * (including discounts) for a given list of product IDs across all available stores.
 */
public class BasketService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BasketService.class);

    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final StoreRepository storeRepository;
    private final PriceHelpers priceHelpers;
    private final ProductMapperService productMapper;

    /**
     * Optimizes a shopping basket by retrieving the lowest available price (including discounts)
     * for each given product across all stores, and grouping them into store-specific baskets.
     *
     * @param productIds list of product IDs to optimize
     * @return list of StoreBasketDTOs, each representing the optimal selection of products per store
     * @throws IllegalArgumentException if the input list is null or empty
     * @throws ResourceNotFoundException if any product ID does not exist
     */
    public List<StoreBasketDTO> optimizeBasket(List<String> productIds) {
        // Validate input
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs list cannot be empty");
        }

        logger.info("Optimizing basket for product IDs: {}", productIds);
//        LocalDate today = LocalDate.now();
        LocalDate today = LocalDate.of(2025, 5, 1);  // May 1, 2025
        List<Store> allStores = storeRepository.findAll();
        logger.debug("Retrieved {} stores from the database", allStores.size());

        // Get all products with their current best prices
        Map<Product, ProductWithBestOffer> bestPricePerProduct = productIds.stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId)))
                .collect(Collectors.toMap(
                        product -> product,
                        product -> findBestOfferForProduct(product, allStores, today)));

        // Group products by store
        Map<Store, List<ProductWithBestOffer>> productsByStore = bestPricePerProduct.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(ProductWithBestOffer::getStore));

        // Convert to StoreBasketDTO
        return productsByStore.entrySet().stream()
                .map(entry -> createStoreBasket(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Finds the best available price for a given product across all stores, including applicable discounts.
     *
     * @param product the product to search pricing for
     * @param stores the list of stores to evaluate
     * @param date the date used to evaluate current prices and active discounts
     * @return the best ProductWithBestOffer available or null if the product is unavailable in all stores
     */
    private ProductWithBestOffer findBestOfferForProduct(Product product, List<Store> stores, LocalDate date) {
        ProductWithBestOffer result = stores.stream()
                .map(store -> {
                    // Get the base price for this product at this store
                    BigDecimal basePrice = priceHelpers.getCurrentPrice(product, store, date);

                    // Product not available at this store
                    if(basePrice == null) {
                        return null;
                    }

                    // Find active discount for this product at this store (if any)
                    Optional<Discount> activeDiscount = discountRepository.findByProductAndStore(product, store).stream()
                            .filter(d -> priceHelpers.isDiscountActive(d, date))
                            .findFirst();

                    BigDecimal finalPrice = activeDiscount
                            .map(discount -> priceHelpers.applyDiscount(basePrice, discount.getPercentage()))
                            .orElse(basePrice);

                    BigDecimal discountPercentage = activeDiscount
                            .map(Discount::getPercentage)
                            .orElse(BigDecimal.ZERO);

                    return new ProductWithBestOffer(product, store, basePrice, finalPrice, discountPercentage);
                })
                .filter(Objects::nonNull)
                .min(Comparator.comparing(ProductWithBestOffer::getDiscountedPrice))
                .orElse(null);

        logger.debug("Best offer for product {} is at store {}: discounted price = {}",
                product.getId(),
                result != null ? result.getStore().getName() : "N/A",
                result != null ? result.getDiscountedPrice() : "N/A");
        return result;
    }

    /**
     * Converts a list of discounted product offers for a specific store into a StoreBasketDTO.
     *
     * @param store the store for which the basket is being created
     * @param products the list of best offers for products available at the given store
     * @return a StoreBasketDTO representing the store and its associated product offers
     */
    private StoreBasketDTO createStoreBasket(Store store, List<ProductWithBestOffer> products) {
        List<BasketItemDTO> items = products.stream()
                .map(p -> new BasketItemDTO(
                        p.getProduct().getId(),
                        p.getProduct().getName(),
                        p.getOriginalPrice(),
                        p.getDiscountedPrice(),
                        p.getDiscountPercentage()))
                .toList();

        BigDecimal total = items.stream()
                .map(BasketItemDTO::getDiscountedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new StoreBasketDTO(
                store.getId(),
                store.getName(),
                items,
                total);
    }
}
