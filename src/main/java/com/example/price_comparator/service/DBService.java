package com.example.price_comparator.service;

import com.example.price_comparator.dto.csv.BaseCSVEntry;
import com.example.price_comparator.dto.csv.DiscountCSVEntry;
import com.example.price_comparator.dto.csv.PriceCSVEntry;
import com.example.price_comparator.model.*;
import com.example.price_comparator.model.enums.Currency;
import com.example.price_comparator.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsible for saving parsed CSV data entries to the database.
 * Handles creation or reuse of related entities like products, categories,
 * brands, and stores before saving price or discount data.
 */
@Service
public class DBService {
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private PriceRepository priceRepository;
    @Autowired private DiscountRepository discountRepository;

    /**
     * Saves a list of parsed CSV entries (prices or discounts) into the database.
     * Delegates each entry to the appropriate saving logic based on its type.
     *
     * @param entries list of parsed CSV entries
     */
    @Transactional
    public void saveAllEntries(List<? extends BaseCSVEntry> entries) {
        entries.forEach(entry -> {
            if(entry instanceof  PriceCSVEntry) {
                savePriceEntry((PriceCSVEntry) entry);
            } else if(entry instanceof DiscountCSVEntry) {
                saveDiscountEntry((DiscountCSVEntry) entry);
            }
        });
    }

    /**
     * Saves a single PriceCSVEntry into the database, creating or reusing related
     * entities such as Product, Category, Brand, and Store as needed.
     *
     * @param entry the price entry to persist
     */
    @Transactional
    public void savePriceEntry(PriceCSVEntry entry){
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
        try {
            price.setCurrency(Currency.valueOf(entry.getCurrency().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid currency format: " + entry.getCurrency());
        }
        price.setEntryDate(entry.getEntryDate());
        priceRepository.save(price);
    }

    /**
     * Saves a single DiscountCSVEntry into the database.
     * Assumes the associated Product already exists; will create the Store if needed.
     *
     * @param entry the discount entry to persist
     * @throws RuntimeException if the referenced Product does not exist
     */
    @Transactional
    public void saveDiscountEntry(DiscountCSVEntry entry){
        // Find or create Store
        Store store = storeRepository.findByName(entry.getStore())
                .orElseGet(() -> storeRepository.save(new Store(entry.getStore())));

        // Product must exist
        Product product = productRepository.findById(entry.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for discount: " + entry.getProductId()));

        // Create and save Discount
        Discount discount = new Discount();
        discount.setProduct(product);
        discount.setStore(store);
        discount.setPercentage(entry.getPercentage());
        discount.setFromDate(entry.getFromDate());
        discount.setToDate(entry.getToDate());
        discount.setEntryDate(entry.getEntryDate());
        discountRepository.save(discount);
    }
}