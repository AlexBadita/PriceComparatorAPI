package com.example.price_comparator.service;

import com.example.price_comparator.dto.alert.PriceAlertDTO;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.model.Store;
import com.example.price_comparator.repository.ProductRepository;
import com.example.price_comparator.repository.StoreRepository;
import com.example.price_comparator.utils.PriceHelpers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PriceAlertService {

    // In-memory storage
    private final List<PriceAlertDTO> alerts = new ArrayList<>();

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PriceHelpers priceHelpers;

    // Create alert
    public PriceAlertDTO createAlert(String productId, Long storeId, BigDecimal targetPrice) {
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        PriceAlertDTO newAlert = new PriceAlertDTO(
                UUID.randomUUID().toString(),
                productId,
                storeId,
                targetPrice,
                true,
                LocalDateTime.now()
        );

        synchronized(alerts) {
            alerts.add(newAlert);
        }

        return newAlert;
    }

    // Get alerts
    public List<PriceAlertDTO> getActiveAlerts() {
        synchronized(alerts) {
            return alerts.stream()
                    .filter(PriceAlertDTO::isActive)
                    .collect(Collectors.toList());
        }
    }

    // Deactivate alert
    public void deactivateAlert(String alertId) {
        synchronized(alerts) {
            alerts.stream()
                    .filter(alert -> alert.getId().equals(alertId))
                    .findFirst()
                    .ifPresent(alert -> alert.setActive(false));
        }
    }

    // Check all active alerts
    public List<PriceAlertDTO> checkAllActiveAlerts() {
        List<PriceAlertDTO> triggeredAlerts = new ArrayList<>();
        List<PriceAlertDTO> currentAlerts;

        synchronized(this) {
            currentAlerts = new ArrayList<>(alerts);
        }

        for (PriceAlertDTO alert : currentAlerts) {
            if (alert.isActive() && checkAlertCondition(alert)) {
                triggeredAlerts.add(alert);
                alert.setActive(false); // Deactivate triggered alert
            }
        }

        return triggeredAlerts;
    }

    // Check alerts for a specific product
    public List<PriceAlertDTO> checkAlertsForProduct(String productId) {
        List<PriceAlertDTO> triggeredAlerts = new ArrayList<>();
        List<PriceAlertDTO> currentAlerts;

        synchronized(this) {
            currentAlerts = new ArrayList<>(alerts);
        }

        for (PriceAlertDTO alert : currentAlerts) {
            if (alert.getProductId().equals(productId) &&
                    alert.isActive() &&
                    checkAlertCondition(alert)) {

                triggeredAlerts.add(alert);
                alert.setActive(false); // Deactivate triggered alert
            }
        }

        return triggeredAlerts;
    }

    private boolean checkAlertCondition(PriceAlertDTO alert) {
        Product product = productRepository.findById(alert.getProductId()).orElse(null);
        Store store = storeRepository.findById(alert.getStoreId()).orElse(null);

        if (product == null && store == null) {
            return false;
        }

        BigDecimal currentPrice = priceHelpers.getCurrentPrice(product, store, LocalDate.now());
        if (currentPrice == null) return false;

        return currentPrice.compareTo(alert.getTargetPrice()) <= 0;
    }
}
