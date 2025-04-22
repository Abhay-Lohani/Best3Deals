package com.group5.best3deals.brand.service;

import com.group5.best3deals.brand.dto.request.BrandPutRequest;
import com.group5.best3deals.brand.dto.request.BrandRequest;
import com.group5.best3deals.brand.dto.response.BrandResponse;
import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.brand.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Brand not found with " + id));
    }

    @Transactional(readOnly = true)
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Transactional
    public BrandResponse createBrand(BrandRequest brandRequest) {
        Brand brand = Brand.builder()
                .name(brandRequest.getName())
                .imageUrl(brandRequest.getImageUrl())
                .build();

        brandRepository.save(brand);

        return new BrandResponse(brand.getId(), brand.getName(), brand.getImageUrl());
    }

    @Transactional
    public BrandResponse updateBrand(Long id, BrandPutRequest brandPutRequest) {
        Brand existingBrand = getBrandById(id);

        Brand brand = Brand.builder()
                .id(id)
                .name(brandPutRequest.getName() != null ? brandPutRequest.getName() : existingBrand.getName())
                .imageUrl(brandPutRequest.getImageUrl() != null ? brandPutRequest.getImageUrl() : existingBrand.getImageUrl())
                .build();

        brandRepository.save(brand);

        return new BrandResponse(brand.getId(), brand.getName(), brand.getImageUrl());
    }

    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new NoSuchElementException("Brand not found with " + id);
        }

        brandRepository.deleteById(id);
    }
}
