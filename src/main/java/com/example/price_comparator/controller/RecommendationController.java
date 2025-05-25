package com.example.price_comparator.controller;

import com.example.price_comparator.dto.recommendation.ProductRecommendationDTO;
import com.example.price_comparator.service.RecommendationService;
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

/**
 * REST controller for providing product recommendation endpoints.
 * Currently supports finding cheaper alternatives to a given product,
 * comparing them based on unit-adjusted price.
 */
@RestController
@RequestMapping("/api/product-recommendations")
@RequiredArgsConstructor
@Tag(name = "Product Recommendations", description = "Endpoints for finding product alternatives and recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(
            summary = "Get cheaper alternatives",
            description = "Finds products in the same category with better value (price per unit)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of cheaper alternatives found"),
                    @ApiResponse(responseCode = "404", description = "Original product not found")
            }
    )
    @GetMapping("/{productId}/cheaper-alternatives")
    public ResponseEntity<List<ProductRecommendationDTO>> getCheaperAlternatives(
            @Parameter(description = "ID of the product to find alternatives for", required = true)
            @PathVariable String productId,

            @Parameter(description = "Date to evaluate prices (defaults to current date)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @Parameter(description = "Target unit for comparison (e.g., 'kg', 'l') - converts all prices to this unit")
            @RequestParam(required = false)
            String targetUnit) {

        List<ProductRecommendationDTO> alternatives = recommendationService
                .getCheaperAlternatives(productId, date, targetUnit);

        return ResponseEntity.ok(alternatives);
    }
}
