package com.example.price_comparator.controller;

import com.example.price_comparator.dto.DiscountDTO;
import com.example.price_comparator.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * REST controller for managing discount-related operations.
 * Provides endpoints for retrieving discount data for products.
 */
@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
@Tag(name = "Discount Management", description = "Endpoints for managing discounts")
public class DiscountController {

    private final DiscountService discountService;

    @Operation(
            summary = "Get all discounts",
            description = "Retrieve a list of all discounts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public ResponseEntity<List<DiscountDTO>> getAllDiscounts() {
        List<DiscountDTO> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(discounts);
    }

    @Operation(
            summary = "Get discounts by store",
            description = "Retrieve all discounts for a specific store by store ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved discounts"),
                    @ApiResponse(responseCode = "404", description = "Store not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<DiscountDTO>> getDiscountsByStore(
            @Parameter(description = "Store ID to filter discounts")
            @PathVariable Long storeId) {
        List<DiscountDTO> discounts = discountService.getDiscountsByStore(storeId);
        if (discounts == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/date/{entryDate}")
    @Operation(
            summary = "Get discounts by entry date",
            description = "Retrieve all discounts for a specific entry date (format: yyyy-MM-dd)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved discounts"),
                    @ApiResponse(responseCode = "400", description = "Invalid date format"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<List<DiscountDTO>> getDiscountsByEntryDate(
            @Parameter(description = "Entry date to filter discounts (yyyy-MM-dd)")
            @PathVariable String entryDate) {
        LocalDate date;
        try {
            date = LocalDate.parse(entryDate);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        List<DiscountDTO> discounts = discountService.getDiscountsByEntryDate(date);
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/highest-discounts")
    @Operation(
            summary = "Get products with highest current discounts",
            description = "Returns products with the highest active discount percentages across all stores",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved highest discounts in descending order"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<DiscountDTO>> getProductsWithHighestDiscounts() {
        List<DiscountDTO> results = discountService.getProductsWithHighestCurrentDiscount();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/recent-active-discounts")
    @Operation(
            summary = "Get recently added active discounts",
            description = "Returns discounts with entryDate up to today and active discounts started from yesterday",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved recent active discounts"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<List<DiscountDTO>> getRecentActiveDiscounts() {
        List<DiscountDTO> recentDiscounts = discountService.getNewDiscounts();
        return ResponseEntity.ok(recentDiscounts);
    }
}
