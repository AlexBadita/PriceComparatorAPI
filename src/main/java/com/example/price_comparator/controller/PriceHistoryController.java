package com.example.price_comparator.controller;

import com.example.price_comparator.dto.price_history.PriceHistoryDTO;
import com.example.price_comparator.dto.price_history.PriceHistoryFilter;
import com.example.price_comparator.dto.price_history.PriceHistoryPointDTO;
import com.example.price_comparator.service.PriceHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/price-history")
@RequiredArgsConstructor
@Tag(name = "Price History", description = "Endpoints for accessing product price history data")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @Operation(
            summary = "Get price history for a product",
            description = "Retrieves historical price data for a specific product, optionally filtered by store and date range",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved price history"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/product/{productName}")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistory(
            @Parameter(description = "Name of the product to get history for", required = true)
            @PathVariable String productName,

            @Parameter(description = "Optional store name to filter by")
            @RequestParam(required = false) String storeName,

            @Parameter(description = "Optional category name to filter by")
            @RequestParam(required = false) String categoryName,

            @Parameter(description = "Optional brand name to filter by")
            @RequestParam(required = false) String brandName,

            @Parameter(description = "Optional start date (inclusive) for filtering (format: yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Optional end date (inclusive) for filtering (format: yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        PriceHistoryFilter filter = new PriceHistoryFilter();
        filter.setProductName(productName);
        filter.setStoreName(storeName);
        filter.setCategoryName(categoryName);
        filter.setBrandName(brandName);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

       List<PriceHistoryDTO> history = priceHistoryService.getPriceHistory(filter);
       return ResponseEntity.ok(history);
    }
}
