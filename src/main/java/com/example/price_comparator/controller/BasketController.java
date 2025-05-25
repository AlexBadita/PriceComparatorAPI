package com.example.price_comparator.controller;

import com.example.price_comparator.dto.basket.StoreBasketDTO;
import com.example.price_comparator.service.BasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for optimizing a user's shopping basket.
 * Receives a list of product IDs and returns price-optimized shopping lists grouped by store.
 */
@RestController
@RequestMapping("/api/basket")
@RequiredArgsConstructor
@Tag(name = "Shopping Basket Optimization", description = "Endpoints for optimizing shopping baskets")
public class BasketController {

    private final BasketService basketService;

    @Operation(
            summary = "Optimize shopping basket",
            description = "Takes a list of product IDs and returns optimized shopping lists grouped by store with lowest prices",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully optimized basket"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/optimize")
    public ResponseEntity<List<StoreBasketDTO>> optimizeBasket(
            @Parameter(description = "List of product IDs to optimize")
            @RequestBody  List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400
        }

        try {
            List<StoreBasketDTO> optimizedBasket = basketService.optimizeBasket(productIds);
            return ResponseEntity.ok(optimizedBasket); // 200
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(null); // 400
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build(); // 500
        }
    }
}
