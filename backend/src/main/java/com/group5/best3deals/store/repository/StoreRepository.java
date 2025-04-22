package com.group5.best3deals.store.repository;

import com.group5.best3deals.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<List<Store>> findAllByBrandId(Long brandId);

    @Query(value = "SELECT s FROM Store s WHERE " +
            "6371 * acos(cos(radians(:latitude)) * cos(radians(s.location.latitude)) * " +
            "cos(radians(s.location.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(s.location.latitude))) <= :distance")
    List<Store> findStoresWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("distance") Double distance
    );
}
