package com.group5.best3deals.storeproduct.repository;

import com.group5.best3deals.storeproduct.entity.StoreProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface StoreProductRepository extends JpaRepository<StoreProduct, Long> {
    Optional<List<StoreProduct>> findAllByStoreId(Long StoreId);
        @Query("SELECT sp FROM StoreProduct sp WHERE sp.product.name = :productName")
        List<StoreProduct> findByProductName(@Param("productName") String productName);
}
