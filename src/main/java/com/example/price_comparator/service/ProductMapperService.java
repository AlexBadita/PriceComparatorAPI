package com.example.price_comparator.service;

import com.example.price_comparator.dto.*;
import com.example.price_comparator.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductMapperService {
    public ProductDTO toProductDTO(Product product) {
        if(product == null){
            return null;
        }
        return new ProductDTO(
                product.getId(),
                product.getName(),
                toCategoryDTO(product.getCategory()),
                toBrandDTO(product.getBrand()),
                product.getPackageQuantity(),
                product.getPackageUnit(),
                groupPricesByStore(product.getPrices())
        );
    }

    private CategoryDTO toCategoryDTO(Category category) {
        if(category == null){
            return null;
        }
        return new CategoryDTO(category.getId(), category.getName());
    }

    private BrandDTO toBrandDTO(Brand brand) {
        if(brand == null){
            return null;
        }
        return new BrandDTO(brand.getId(), brand.getName());
    }

    private StoreDTO toStoreDTO(Store store) {
        if(store == null){
            return null;
        }
        return new StoreDTO(store.getId(), store.getName());
    }

    private PriceEntryDTO toPriceEntryDTO(Price price) {
        return new PriceEntryDTO(
                price.getPrice(),
                price.getCurrency(),
                price.getEntryDate()
        );
    }


    private List<StorePriceHistoryDTO> groupPricesByStore(List<Price> prices) {
        if(prices == null || prices.isEmpty()){
            return Collections.emptyList();
        }

        // Group prices by store
        Map<Store, List<Price>> pricesByStore = prices.stream()
                .collect(Collectors.groupingBy(Price::getStore));

        // Concert to DTO structure
        return pricesByStore.entrySet().stream()
                .map(entry -> new StorePriceHistoryDTO(
                        toStoreDTO(entry.getKey()),
                        entry.getValue().stream()
                                .sorted(Comparator.comparing(Price::getEntryDate).reversed())
                                .map(this::toPriceEntryDTO)
                                .collect(Collectors.toList())
                ))
                .sorted(Comparator.comparing(dto -> dto.getStore().getName()))
                .collect(Collectors.toList());
    }
}
