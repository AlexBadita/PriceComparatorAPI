package com.example.price_comparator.service;

import com.example.price_comparator.dto.ProductPrice;
import com.example.price_comparator.model.Discount;
import com.example.price_comparator.model.Price;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.model.Store;
import com.example.price_comparator.repository.DiscountRepository;
import com.example.price_comparator.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceService {
    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private DiscountRepository discountRepository;

    private DiscountService discountService;

    public BigDecimal getCurrentPrice(Price price){

    }

    public Store findStoreWithLowestPrice(Product product){
        List<Price> prices = priceRepository.findByProduct(product);

        return prices.stream()
                .min(Comparator.comparing(this::getCurrentPrice))
                .map(Price::getStore)
                .orElseThrow(() -> new RuntimeException("No prices found for " + product.getName()));
    }

    public List<ProductPrice> getProductPrices(Product product){
        return priceRepository.findByProduct(product).stream()
                .map(price -> {
                    BigDecimal originalPrice = price.getPrice();
                    BigDecimal currentPrice = getCurrentPrice(price);
                    boolean hasDiscount = !originalPrice.equals(currentPrice);

                    return new ProductPrice(
                            price.getStore(),
                            originalPrice,
                            currentPrice,
                            hasDiscount
                    );
                })
                .collect(Collectors.toList());
    }
}
