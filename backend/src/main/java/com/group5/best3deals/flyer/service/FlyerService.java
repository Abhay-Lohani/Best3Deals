package com.group5.best3deals.flyer.service;

import com.group5.best3deals.flyer.dto.request.FlyerPutRequest;
import com.group5.best3deals.flyer.dto.request.FlyerRequest;
import com.group5.best3deals.flyer.dto.response.FlyerResponse;
import com.group5.best3deals.flyer.entity.Flyer;
import com.group5.best3deals.flyer.entity.FlyerProduct;
import com.group5.best3deals.flyer.repository.FlyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FlyerService {
    private final FlyerRepository flyerRepository;
    private final FlyerProductService flyerProductService;

    @Transactional(readOnly = true)
    public FlyerResponse getFlyerById(Long id) {
        Flyer flyer = flyerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Flyer not found with " + id));

        List<FlyerProduct> products = flyerProductService.getAllFlyerProductByFlyerId(flyer.getId());

        return new FlyerResponse(
                flyer.getId(), flyer.getName(), flyer.getDescription(), flyer.getImageUrl(), flyer.getStoreId(), products);
    }

    @Transactional(readOnly = true)
    public List<Flyer> getAllFlyers() {
        return flyerRepository.findAll();
    }

    // Get flyers by storeId
    @Transactional(readOnly = true)
    public List<Flyer> getAllFlyersByStoreId(Long storeId) {
        return flyerRepository.findAllByStoreId(storeId).orElseThrow(() -> new NoSuchElementException("Flyer not found with storeId " + storeId));
    }

    @Transactional
    public FlyerResponse createFlyer(FlyerRequest flyerRequest) {
        Flyer flyer = Flyer
                .builder()
                .name(flyerRequest.getName())
                .description(flyerRequest.getDescription())
                .imageUrl(flyerRequest.getImageUrl())
                .storeId(flyerRequest.getStoreId())
                .build();

        flyerRepository.save(flyer);

        return new FlyerResponse(
                flyer.getId(), flyer.getName(), flyer.getDescription(), flyer.getImageUrl(), flyer.getStoreId(), new ArrayList<>());
    }

    @Transactional
    public FlyerResponse updateFlyer(Long id, FlyerPutRequest flyerRequest) {
        Flyer existingFlyer = flyerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Flyer not found with " + id));

        Flyer flyer = Flyer.builder()
                .id(id)
                .name(flyerRequest.getName() != null ? flyerRequest.getName() : existingFlyer.getName())
                .description(flyerRequest.getDescription() != null ? flyerRequest.getDescription() : existingFlyer.getDescription())
                .imageUrl(flyerRequest.getImageUrl() != null ? flyerRequest.getImageUrl() : existingFlyer.getImageUrl())
                .storeId(flyerRequest.getStoreId() != null ? flyerRequest.getStoreId() : existingFlyer.getStoreId())
                .build();

        flyerRepository.save(flyer);

        // Finding related products by flyerId
        List<FlyerProduct> products = flyerProductService.getAllFlyerProductByFlyerId(flyer.getId());

        return new FlyerResponse(
                flyer.getId(), flyer.getName(), flyer.getDescription(), flyer.getImageUrl(), flyer.getStoreId(), products);
    }

    @Transactional
    public void deleteFlyer(Long id) {
        if (!flyerRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found with " + id);
        }

        flyerRepository.deleteById(id);
    }
}
