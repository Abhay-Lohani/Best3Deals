package com.group5.best3deals.product.repository;

import com.group5.best3deals.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<List<Product>> findAllByCategoryId(Long CategoryId);

}
