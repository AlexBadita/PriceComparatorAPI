package com.example.price_comparator.service;

import com.example.price_comparator.dto.*;
import com.example.price_comparator.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for mapping Discount model entities
 * to their corresponding DTO representations.
 */
@Service
public class DiscountMapperService {
    public DiscountDTO toDiscountDTO(Discount discount){
        if(discount == null){
            return null;
        }
        return new DiscountDTO(
                discount.getId(),
                toStoreDTO(discount.getStore()),
                toDiscountProductDTO(discount.getProduct(), discount.getStore(), discount.getEntryDate()),
                discount.getFromDate(),
                discount.getToDate(),
                discount.getPercentage()
        );
    }

    private StoreDTO toStoreDTO(Store store){
        if(store == null){
            return null;
        }
        return new StoreDTO(store.getId(), store.getName());
    }

    private DiscountProductDTO toDiscountProductDTO(Product product, Store store, LocalDate entryDate){
        if(product == null){
            return null;
        }

        return new DiscountProductDTO(
                product.getId(),
                product.getName(),
                toCategoryDTO(product.getCategory()),
                toBrandDTO(product.getBrand()),
                product.getPackageQuantity(),
                product.getPackageUnit(),
                toPriceDTO(product.getPrices(), store, entryDate)
        );
    }

    private CategoryDTO toCategoryDTO(Category category){
        if(category == null){
            return null;
        }
        return new CategoryDTO(category.getId(), category.getName());
    }

    private BrandDTO toBrandDTO(Brand brand){
        if(brand == null){
            return null;
        }
        return new BrandDTO(brand.getId(), brand.getName());
    }

    private PriceDTO toPriceDTO(List<Price> prices, Store store, LocalDate entryDate){
        if(prices == null){
            return null;
        }

        // Find price that matches both store and entry date
        Optional<Price> matchingPrice = prices.stream()
                .filter(price -> price.getStore().getId().equals(store.getId()))
                .filter(price -> price.getEntryDate().equals(entryDate))
                .findFirst();

        return matchingPrice.map(price -> new PriceDTO(price.getPrice(), price.getCurrency()))
                .orElse(null);
    }
}
