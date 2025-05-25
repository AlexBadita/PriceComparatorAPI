package com.example.price_comparator.utils;

import com.example.price_comparator.model.Discount;
import com.example.price_comparator.model.Price;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.model.Store;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

/**
 * Utility component for handling pricing and discount logic across the application.
 * Provides methods for evaluating discount periods, applying discount calculations,
 * and retrieving current product prices.
 */
@Component
public class PriceHelpers {

    /**
     * Determines if the given discount is currently active based on the selected date.
     *
     * @param discount the discount to evaluate
     * @param selectedDate the date to check against (uses current date if null)
     * @return true if the discount is active on the selected date; false otherwise
     */
    public boolean isDiscountActive(Discount discount, LocalDate selectedDate) {
        LocalDate date = Optional.ofNullable(selectedDate).orElse(LocalDate.now());
        return (!date.isBefore(discount.getFromDate()) &&
                !date.isAfter(discount.getToDate()));
    }

    /**
     * Checks if the given discount is scheduled to start in the future based on the selected date.
     *
     * @param discount the discount to evaluate
     * @param selectedDate the date to check against (uses current date if null)
     * @return true if the discount is upcoming; false otherwise
     */
    public boolean isDiscountUpcoming(Discount discount, LocalDate selectedDate) {
        LocalDate date = Optional.ofNullable(selectedDate).orElse(LocalDate.now());
        return date.isBefore(discount.getFromDate());
    }

    /**
     * Checks whether the given discount has already expired based on the selected date.
     *
     * @param discount the discount to evaluate
     * @param selectedDate the date to check against (uses current date if null)
     * @return true if the discount has expired; false otherwise
     */
    public boolean isDiscountExpired(Discount discount, LocalDate selectedDate) {
        LocalDate date = Optional.ofNullable(selectedDate).orElse(LocalDate.now());
        return date.isAfter(discount.getToDate());
    }

    /**
     * Applies a percentage discount to the original price and rounds the result to two decimal places.
     *
     * @param originalPrice the original price before discount
     * @param discountPercentage the discount percentage to apply (e.g., 15 for 15%)
     * @return the discounted price rounded to two decimal places
     */
    public BigDecimal applyDiscount(BigDecimal originalPrice, BigDecimal discountPercentage) {
        BigDecimal discountMultiplier = BigDecimal.ONE
                .subtract(discountPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        return originalPrice.multiply(discountMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Retrieves the most recent price of a given product from a specified store,
     * valid on or before the specified date.
     *
     * @param product the product whose price is to be retrieved; must not be null
     * @param store the store from which the price is retrieved; must not be null
     * @param date the date for which the price must be valid (i.e., on or before this date); must not be null
     * @return the most recent valid price as a BigDecimal, or null if no price is available
     * @throws IllegalArgumentException if any of the parameters are null
     */
    public BigDecimal getCurrentPrice(Product product, Store store, LocalDate date) {
        if (product == null || store == null || date == null) {
            throw new IllegalArgumentException("Product, store, and date must not be null.");
        }

        if (product.getPrices() == null) {
            return null;
        }

        Optional<Price> price = product.getPrices().stream()
                .filter(p -> p.getStore().equals(store))
                .filter(p -> !p.getEntryDate().isAfter(date))
                .max(Comparator.comparing(Price::getEntryDate));

        return price.map(Price::getPrice).orElse(null);
    }

    /**
     * Converts between different units of measurement for price comparison
     * Supported conversions: kg-g, g-kg, l-ml, ml-l, unit conversions
     *
     * @param value The value to convert
     * @param fromUnit Original unit (kg, g, l, ml, unit)
     * @param toUnit Target unit (kg, g, l, ml, unit)
     * @return Converted value
     * @throws IllegalArgumentException for unsupported conversions
     */
    public BigDecimal convertUnit(BigDecimal value, String fromUnit, String toUnit) {
        if (value == null || fromUnit == null || toUnit == null) {
            throw new IllegalArgumentException("Value and units cannot be null");
        }

        // If units are the same, return original value
        if (fromUnit.equals(toUnit)) {
            return value;
        }

        // Handle supported conversions
        switch (fromUnit) {
            case "kg":
                if (toUnit.equals("g")) {
                    return value.multiply(BigDecimal.valueOf(1000));
                }
                break;
            case "g":
                if (toUnit.equals("kg")) {
                    return value.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
                }
                break;
            case "l":
                if (toUnit.equals("ml")) {
                    return value.multiply(BigDecimal.valueOf(1000));
                }
                break;
            case "ml":
                if (toUnit.equals("l")) {
                    return value.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
                }
                break;
            default:
                break;
        }

        throw new IllegalArgumentException(
                String.format("Unsupported unit conversion: from %s to %s", fromUnit, toUnit)
        );
    }

    /**
     * Calculates price per standardized unit for value comparison
     *
     * @param price Product price
     * @param unitValue Size/quantity (e.g., 1.5)
     * @param unitType Unit type (kg, g, l, ml, unit)
     * @param targetUnit Unit to standardize to (null returns price per original unit)
     * @return Price per target unit
     * @throws IllegalArgumentException for invalid inputs
     */
    public BigDecimal calculatePricePerUnit(BigDecimal price, BigDecimal unitValue, String unitType, String targetUnit) {
        if (price == null || unitValue == null || unitType == null) {
            throw new IllegalArgumentException("Price, unitValue and unitType cannot be null");
        }

        if (unitValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit value must be positive");
        }

        // Calculate price per original unit first
        BigDecimal pricePerUnit = price.divide(unitValue, 6, RoundingMode.HALF_UP);

        // Convert to target unit if specified
        if (targetUnit != null && !unitType.equalsIgnoreCase(targetUnit)) {
            pricePerUnit = convertUnit(pricePerUnit, unitType, targetUnit);
        }

        return pricePerUnit.setScale(2, RoundingMode.HALF_UP);
    }
}
