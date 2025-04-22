package com.group5.best3deals.flyer.repository;

import com.group5.best3deals.flyer.entity.Flyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlyerRepository extends JpaRepository<Flyer, Long> {
    Optional<List<Flyer>> findAllByStoreId(Long storeId);
}
