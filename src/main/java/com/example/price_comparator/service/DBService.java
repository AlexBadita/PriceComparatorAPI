package com.example.price_comparator.service;

import com.example.price_comparator.dto.PriceEntry;
import com.example.price_comparator.model.*;
import com.example.price_comparator.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DBService {
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private PriceRepository priceRepository;

    @Transactional
    public void saveAllPriceEntries(List<PriceEntry> entries){
        entries.forEach(this::savePriceEntry);
    }

    @Transactional
    public void savePriceEntry(PriceEntry entry){
        // Find or create Category
        Category category = categoryRepository.findByName(entry.getProductCategory())
                .orElseGet(() -> categoryRepository.save(new Category(entry.getProductCategory())));

        // Find or create Brand
        Brand brand = brandRepository.findByName(entry.getBrand())
                .orElseGet(() -> brandRepository.save(new Brand(entry.getBrand())));

        // Find or create Store
        Store store = storeRepository.findByName(entry.getStore())
                .orElseGet(() -> storeRepository.save(new Store(entry.getStore())));

        // Find or create Product
        Product product = productRepository.findById(entry.getProductId())
                .orElseGet(() -> {
                    Product newProduct = new Product();
                    newProduct.setId(entry.getProductId());
                    newProduct.setName(entry.getProductName());
                    newProduct.setCategory(category);
                    newProduct.setBrand(brand);
                    newProduct.setPackageQuantity(entry.getPackageQuantity());
                    newProduct.setPackageUnit(entry.getPackageUnit());
                    return productRepository.save(newProduct);
                });

        // Create Price entry
        Price price = new Price();
        price.setProduct(product);
        price.setStore(store);
        price.setPrice(entry.getPrice());
        price.setCurrency(entry.getCurrency());
        price.setEntryDate(entry.getEntryDate());
        priceRepository.save(price);
    }
}
