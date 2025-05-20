package com.example.price_comparator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

   @Id
   @Column(name = "id", length = 10)
   private String id;

   @Column(nullable = false)
   private String name;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "category_id", nullable = false)
   private Category category;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "brand_id", nullable = false)
   private Brand brand;

   @Column(name = "package_quantity", nullable = false)
   private BigDecimal packageQuantity;

   @Column(name = "package_unit", nullable = false, length = 20)
   private String packageUnit;

   @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
   private List<Price> prices;
}
