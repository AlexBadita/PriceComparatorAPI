package com.example.price_comparator.service;

import com.example.price_comparator.repository.DiscountRepository;
import com.example.price_comparator.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingBasketService {
    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private DiscountRepository discountRepository;


}
