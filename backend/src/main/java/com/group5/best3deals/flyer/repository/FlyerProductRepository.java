package com.group5.best3deals.flyer.repository;

import com.group5.best3deals.flyer.entity.FlyerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlyerProductRepository extends JpaRepository<FlyerProduct, Long> {
    List<FlyerProduct> findAllByFlyerId(Long flyerId);
    List<FlyerProduct> findAllByStoreProductId(Long productId);
}
