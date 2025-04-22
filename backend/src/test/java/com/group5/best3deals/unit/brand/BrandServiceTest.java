package com.group5.best3deals.unit.brand;

import com.group5.best3deals.brand.dto.request.BrandPutRequest;
import com.group5.best3deals.brand.dto.request.BrandRequest;
import com.group5.best3deals.brand.dto.response.BrandResponse;
import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.brand.repository.BrandRepository;
import com.group5.best3deals.brand.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    private Brand brand;

    private BrandRequest brandReq;

    private BrandPutRequest brandPutReq;


    @BeforeEach
    void setUp() {
        brand = Brand.builder().id(1L).name("Walmart").imageUrl("http://example.com/walmart.jpg").build();
        brandReq = new BrandRequest("Walmart", "http://example.com/walmart.jpg");
        brandPutReq = new BrandPutRequest();
        brandPutReq.setImageUrl("http://example.com/walmart.png");
    }

    @Test
    void getBrandById_whenBrandExists_shouldReturnBrand() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        Brand result = brandService.getBrandById(1L);

        assertNotNull(result);
        assertEquals(brand.getId(), result.getId());
        assertEquals(brand.getName(), result.getName());
        assertEquals(brand.getImageUrl(), result.getImageUrl());
    }

    @Test
    void getBrandById_whenBrandDoesNotExist_shouldThrowException() {
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> brandService.getBrandById(1L));
    }

    @Test
    void getAllBrands_shouldReturnBrandList() {
        when(brandRepository.findAll()).thenReturn(List.of(brand));

        List<Brand> result = brandService.getAllBrands();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void createBrand_shouldReturnBrandResponse() {
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandResponse result = brandService.createBrand(brandReq);

        assertNotNull(result);
        assertEquals("Walmart", result.getName());
        assertEquals("http://example.com/walmart.jpg", result.getImageUrl());
    }

    @Test
    void updateBrand_shouldReturnUpdatedBrandResponse() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandResponse result = brandService.updateBrand(1L, brandPutReq);

        assertNotNull(result);
        assertEquals(brand.getId(), result.getId());
        assertEquals(brand.getName(), result.getName());
        assertEquals(brandPutReq.getImageUrl(), result.getImageUrl());
    }

    @Test
    void updateBrand_whenBrandNotFound_shouldThrowException() {
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> brandService.updateBrand(1L, brandPutReq));
    }

    @Test
    void deleteBrand_whenBrandExists_shouldDeleteBrand() {
        when(brandRepository.existsById(1L)).thenReturn(true);

        brandService.deleteBrand(1L);

        verify(brandRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBrand_whenBrandDoesNotExist_shouldThrowException() {
        when(brandRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> brandService.deleteBrand(1L));
    }
}
