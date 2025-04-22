package com.group5.best3deals.store.service;

import com.group5.best3deals.location.entity.Location;
import com.group5.best3deals.location.service.LocationService;
import com.group5.best3deals.store.dto.request.StorePutRequest;
import com.group5.best3deals.store.dto.request.StoreRequest;
import com.group5.best3deals.store.dto.response.StoreResponse;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.store.repository.StoreRepository;
import com.group5.best3deals.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final LocationService locationService;

    @Transactional(readOnly = true)
    public Store getStoreById(Long id) {
        return storeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Store not found with " + id));
    }

    @Transactional(readOnly = true)
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Store> getStoresWithinRadius(Double latitude, Double longitude, Integer radius) {
        List<Store> allStores = storeRepository.findAll();
        List<Store> nearbyStores = new ArrayList<>();

        for (Store store : allStores) {
            double distance = DistanceCalculator.calculateDistance(
                    latitude, longitude, store.getLocation().getLatitude(), store.getLocation().getLongitude());

            if (distance <= radius) {
                nearbyStores.add(store);
            }
        }

        return nearbyStores;
    }

    @Transactional(readOnly = true)
    public List<Store> getStoresByBrandId(Long brandId) {
        return storeRepository.findAllByBrandId(brandId)
                .orElseThrow(() -> new NoSuchElementException("Store not found with brand id " + brandId));
    }

    @Transactional
    public StoreResponse createStore(StoreRequest storeReq) {
        Location location = locationService.createLocation(storeReq.getLocation());

        Store store = Store.builder()
                .name(storeReq.getName())
                .address(storeReq.getAddress())
                .imgUrl(storeReq.getImageUrl())
                .location(location)
                .brandId(storeReq.getBrandId())
                .build();

        storeRepository.save(store);

        return new StoreResponse(
                store.getId(), store.getName(), store.getAddress(), store.getImgUrl(), store.getLocation(), store.getBrandId());
    }

    @Transactional
    public StoreResponse updateStore(Long id, StorePutRequest storePutReq) {
        Store existingStore = getStoreById(id);

        Location updatedLocation = storePutReq.getLocation() != null
                ? locationService.updateLocation(existingStore.getLocation().getId(), storePutReq.getLocation())
                : existingStore.getLocation();

        Store store = Store.builder()
                .id(existingStore.getId())
                .name(storePutReq.getName() != null ? storePutReq.getName() : existingStore.getName())
                .address(storePutReq.getAddress() != null ? storePutReq.getAddress() : existingStore.getAddress())
                .imgUrl(storePutReq.getImgUrl() != null ? storePutReq.getImgUrl() : existingStore.getImgUrl())
                .location(updatedLocation)
                .brandId(storePutReq.getBrandId() != null ? storePutReq.getBrandId() : existingStore.getBrandId())
                .build();
        storeRepository.save(store);
        return new StoreResponse(
                store.getId(), store.getName(), store.getAddress(), store.getImgUrl(), store.getLocation(), store.getBrandId());
    }

    @Transactional
    public void deleteStoreById(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new NoSuchElementException("Store not found with " + id);
        }

        storeRepository.deleteById(id);
    }
        public List<Store> findStoresWithinDistance(double latitude, double longitude, double distanceInKm) {
            return storeRepository.findStoresWithinDistance(latitude, longitude, distanceInKm);
        }

}
