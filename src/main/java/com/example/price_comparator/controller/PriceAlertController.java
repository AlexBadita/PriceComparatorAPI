package com.example.price_comparator.controller;

import com.example.price_comparator.dto.alert.PriceAlertDTO;
import com.example.price_comparator.service.PriceAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for managing user price alerts.
 * Provides endpoints for creating alerts, listing active alerts, deactivating alerts,
 * and checking if current prices meet alert conditions.
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Price Alerts", description = "Endpoints for managing price alerts")
public class PriceAlertController {

    private final PriceAlertService priceAlertService;

    @Operation(
            summary = "Create a new price alert",
            description = "Creates a new price alert for a specific product and store at a target price.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Alert successfully created")
            }
    )
    @PostMapping
    public ResponseEntity<PriceAlertDTO> createAlert(
            @RequestParam String productId,
            @RequestParam Long storeId,
            @RequestParam BigDecimal targetPrice) {

        PriceAlertDTO alert = priceAlertService.createAlert(productId, storeId, targetPrice);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @Operation(
            summary = "Get all active price alerts",
            description = "Retrieves all currently active price alerts.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Active alerts retrieved successfully")
            }
    )
    @GetMapping
    public ResponseEntity<List<PriceAlertDTO>> getActiveAlerts() {
        return ResponseEntity.ok(priceAlertService.getActiveAlerts());
    }

    @Operation(
            summary = "Deactivate an alert",
            description = "Deactivates a price alert by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Alert successfully deactivated")
            }
    )
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deactivateAlert(@PathVariable String alertId) {
        priceAlertService.deactivateAlert(alertId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Check all alerts",
            description = "Checks all active alerts to determine if their conditions are met.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Alerts checked successfully")
            }
    )
    @GetMapping("/check")
    public ResponseEntity<List<PriceAlertDTO>> checkAllAlerts() {
        return ResponseEntity.ok(priceAlertService.checkAllActiveAlerts());
    }

    @Operation(
            summary = "Check alerts for a specific product",
            description = "Checks price alerts for a specific product ID to see if they are triggered.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product alerts checked successfully")
            }
    )
    @GetMapping("/check/product/{productId}")
    public ResponseEntity<List<PriceAlertDTO>> checkProductAlerts(
            @PathVariable String productId) {
        return ResponseEntity.ok(priceAlertService.checkAlertsForProduct(productId));
    }
}
