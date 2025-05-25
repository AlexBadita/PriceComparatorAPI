package com.example.price_comparator.service;

import com.example.price_comparator.dto.recommendation.ProductRecommendationDTO;
import com.example.price_comparator.model.Category;
import com.example.price_comparator.model.Discount;
import com.example.price_comparator.model.Price;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.repository.DiscountRepository;
import com.example.price_comparator.repository.ProductRepository;
import com.example.price_comparator.utils.PriceHelpers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final PriceHelpers priceHelpers;

    /**
     * Finds cheaper alternatives for a given product
     * @param productId ID of the original product
     * @param date Date for price evaluation (current date if null)
     * @param targetUnit Target unit for comparison (null to keep original unit)
     * @return List of recommended cheaper alternatives
     */
    public List<ProductRecommendationDTO> getCheaperAlternatives(String productId, LocalDate date, String targetUnit) {
        LocalDate evaluationDate = Optional.ofNullable(date).orElse(LocalDate.now());

        // 1. Get the original product
        Product originalProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        // 2. Get its category
        Category category = originalProduct.getCategory();

        // 3. Find products in the same category with compatible units
        List<Product> sameCategoryProducts = productRepository.findByCategory(category);

        // Filter products with units in the same category as the original
        sameCategoryProducts = sameCategoryProducts.stream()
                .filter(p -> !p.getId().equals(productId)) // exclude original product
//                .filter(p -> p.getPackageUnit().getCategory() == originalProduct.getPackageUnit().getCategory())
                .filter(p -> p.getPackageUnit().equals(originalProduct.getPackageUnit()))
                .toList();

        // 4. Compute price per unit for original product (with discounts if any)
        BigDecimal originalPricePerUnit = calculateProductPricePerUnit(originalProduct, evaluationDate, targetUnit);

        if (originalPricePerUnit == null) {
            return new ArrayList<>(); // no price available for original product
        }

        List<ProductRecommendationDTO> recommendations = new ArrayList<>();

        // 5. Compute price per unit for other products and find cheaper ones
        for (Product alternativeProduct : sameCategoryProducts) {
            BigDecimal alternativePricePerUnit = calculateProductPricePerUnit(alternativeProduct, evaluationDate, targetUnit);

            if (alternativePricePerUnit != null && alternativePricePerUnit.compareTo(originalPricePerUnit) < 0) {
                // 6. This is a cheaper alternative
                BigDecimal savingsPercentage = originalPricePerUnit.subtract(alternativePricePerUnit)
                        .divide(originalPricePerUnit, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);

                recommendations.add(new ProductRecommendationDTO(
                        alternativeProduct.getId(),
                        alternativeProduct.getName(),
                        alternativeProduct.getBrand().getName(),
                        "", // store name will be set below
                        getCurrentPriceForProduct(alternativeProduct, evaluationDate),
                        targetUnit != null ? targetUnit : alternativeProduct.getPackageUnit(),
                        convertUnitValueIfNeeded(alternativeProduct, targetUnit),
                        alternativePricePerUnit,
                        savingsPercentage
                ));
            }
        }

        return recommendations;
    }

    /**
     * Calculates the price per unit for a product, applying discounts if available
     */
    private BigDecimal calculateProductPricePerUnit(Product product, LocalDate date, String targetUnit) {
        // Get the most recent price for each store
        // For simplicity, we'll use the lowest price available across all stores
        BigDecimal lowestPrice = null;

        if (product.getPrices() == null || product.getPrices().isEmpty()) {
            return null;
        }

        for (Price price : product.getPrices()) {
            if (!price.getEntryDate().isAfter(date)) {
                BigDecimal currentPrice = price.getPrice();

                // Check for active discounts - now we query them separately
                List<Discount> activeDiscounts = discountRepository.findByProductAndStoreAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                        product,
                        price.getStore(),
                        date,
                        date
                );

                if (!activeDiscounts.isEmpty()) {
                    currentPrice = priceHelpers.applyDiscount(currentPrice, activeDiscounts.get(0).getPercentage());
                }

                if (lowestPrice == null || currentPrice.compareTo(lowestPrice) < 0) {
                    lowestPrice = currentPrice;
                }
            }
        }

        if (lowestPrice == null) {
            return null;
        }

        // Calculate price per unit
        return priceHelpers.calculatePricePerUnit(
                lowestPrice,
                product.getPackageQuantity(),
                product.getPackageUnit(),
                targetUnit
        );
    }
    /**
     * Gets the current price for a product (lowest across all stores)
     */
    private BigDecimal getCurrentPriceForProduct(Product product, LocalDate date) {
        if (product.getPrices() == null || product.getPrices().isEmpty()) {
            return null;
        }

        BigDecimal lowestPrice = null;

        for (Price price : product.getPrices()) {
            if (!price.getEntryDate().isAfter(date)) {
                BigDecimal currentPrice = price.getPrice();

                if (lowestPrice == null || currentPrice.compareTo(lowestPrice) < 0) {
                    lowestPrice = currentPrice;
                }
            }
        }

        return lowestPrice;
    }

    /**
     * Converts the unit value if a target unit is specified
     */
    private BigDecimal convertUnitValueIfNeeded(Product product, String targetUnit) {
        if (targetUnit == null || targetUnit.equals(product.getPackageUnit())) {
            return product.getPackageQuantity();
        }

        try {
            return priceHelpers.convertUnit(
                    product.getPackageQuantity(),
                    product.getPackageUnit(),
                    targetUnit
            );
        } catch (IllegalArgumentException e) {
            return product.getPackageQuantity(); // return original if conversion fails
        }
    }
}
