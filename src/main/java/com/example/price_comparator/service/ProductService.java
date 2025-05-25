package com.example.price_comparator.service;

import com.example.price_comparator.dto.ProductDTO;
import com.example.price_comparator.exception.ResourceNotFoundException;
import com.example.price_comparator.model.Product;
import com.example.price_comparator.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling business logic related to products.
 * It provides methods to retrieve all products or a specific product by ID.
 * This service maps Product entities to their corresponding DTO representations.
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapperService productMapper;

    /**
     * Retrieves a list of all products from the repository and maps them to ProductDTOs.
     *
     * @return a list of ProductDTOs representing all available products
     */
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the ID of the product to retrieve
     * @return the corresponding ProductDTO
     * @throws ResourceNotFoundException if the product with the given ID is not found
     */
    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toProductDTO(product);
    }
}
