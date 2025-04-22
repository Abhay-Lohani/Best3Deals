package com.group5.best3deals.unit.storeProduct;
import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.brand.service.BrandService;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.product.service.ProductService;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.store.service.StoreService;
import com.group5.best3deals.storeproduct.dto.request.StoreProductPutRequest;
import com.group5.best3deals.storeproduct.dto.request.StoreProductRequest;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.storeproduct.repository.StoreProductRepository;
import com.group5.best3deals.storeproduct.service.StoreProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreProductServiceTest {

    @Mock
    private StoreProductRepository storeProductRepository;

    @Mock
    private ProductService productService;

    @Mock
    private BrandService brandService;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreProductService storeProductService;

    private StoreProduct testStoreProduct;
    private StoreProductRequest testStoreProductRequest;
    private StoreProductPutRequest testStoreProductPutRequest;

    @BeforeEach
    void setUp() {
        testStoreProduct = StoreProduct.builder()
                .id(1L)
                .price(99.99)
                .quantityInStock(50)
                .build();

        testStoreProductRequest = StoreProductRequest.builder()
                .storeId(1L)
                .productId(100L)
                .brandId(10L)
                .storeProductUrl("https://example.com/product")
                .price(99.99)
                .quantityInStock(50)
                .build();

        testStoreProductPutRequest = StoreProductPutRequest.builder()
                .storeProductId(1L)
                .storeId(1L)
                .productId(100L)
                .brandId(10L)
                .storeProductUrl("https://example.com/product")
                .price(99.99)
                .quantityInStock(50)
                .build();
    }

    @Test
    void getStoreProductsWithinDistance_ShouldReturnTop3Cheapest() {
        // Given
        Store store1 = Store.builder().id(1L).build();
        Store store2 = Store.builder().id(2L).build();

        StoreProduct product1 = StoreProduct.builder().id(1L).price(10.0).store(store1).build();
        StoreProduct product2 = StoreProduct.builder().id(2L).price(20.0).store(store1).build();
        StoreProduct product3 = StoreProduct.builder().id(3L).price(30.0).store(store2).build();
        StoreProduct product4 = StoreProduct.builder().id(4L).price(5.0).store(store2).build();

        when(storeProductRepository.findByProductName("organicApples"))
                .thenReturn(Arrays.asList(product1, product2, product3, product4));
        when(storeService.findStoresWithinDistance(40.7128, -74.0060, 10.0))
                .thenReturn(Arrays.asList(store1, store2));

        // When
        List<StoreProduct> result = storeProductService.getStoreProductsWithinDistance(
                "organicApples", 40.7128, -74.0060, 10.0);

        // Then
        assertEquals(3, result.size());
        assertEquals(4L, result.get(0).getId()); // Cheapest first
        assertEquals(1L, result.get(1).getId());
        assertEquals(2L, result.get(2).getId());
    }

    @Test
    void createStoreProduct_ShouldCreateNewProduct() {
        // Given
        Product product = Product.builder().id(100L).build();
        Store store = Store.builder().id(1L).build();
        Brand brand = Brand.builder().id(10L).build();

        when(productService.getProductById(100L)).thenReturn(product);
        when(storeService.getStoreById(1L)).thenReturn(store);
        when(brandService.getBrandById(10L)).thenReturn(brand);
        when(storeProductRepository.save(any(StoreProduct.class))).thenReturn(testStoreProduct);

        // When
        StoreProduct result = storeProductService.createStoreProduct(testStoreProductRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(storeProductRepository).save(any(StoreProduct.class));
    }

    @Test
    void updateStoreProduct_ShouldUpdateExistingProduct() {
        // Given
        StoreProduct existing = StoreProduct.builder().id(1L).price(50.0).build();
        Product product = Product.builder().id(100L).build();
        Store store = Store.builder().id(1L).build();
        Brand brand = Brand.builder().id(10L).build();

        when(storeProductRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productService.getProductById(100L)).thenReturn(product);
        when(storeService.getStoreById(1L)).thenReturn(store);
        when(brandService.getBrandById(10L)).thenReturn(brand);
        when(storeProductRepository.save(any(StoreProduct.class))).thenReturn(testStoreProduct);

        // When
        StoreProduct result = storeProductService.updateStoreProduct(1L, testStoreProductPutRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(storeProductRepository).save(any(StoreProduct.class));
    }

    @Test
    void getStoreProduct_ShouldReturnProduct() {
        // Given
        when(storeProductRepository.findById(1L)).thenReturn(Optional.of(testStoreProduct));

        // When
        StoreProduct result = storeProductService.getStoreProduct(1L);

        // Then
        assertEquals(testStoreProduct, result);
    }

    @Test
    void getStoreProduct_NotFound_ShouldThrowException() {
        // Given
        when(storeProductRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> storeProductService.getStoreProduct(999L));
    }

    @Test
    void getAllStoreProducts_ShouldReturnAllProducts() {
        // Given
        when(storeProductRepository.findAll()).thenReturn(Arrays.asList(testStoreProduct));

        // When
        List<StoreProduct> result = storeProductService.getAllStoreProducts();

        // Then
        assertEquals(1, result.size());
        assertEquals(testStoreProduct, result.get(0));
    }

    @Test
    void deleteAStoreProduct_ShouldDeleteProduct() {
        // Given
        when(storeProductRepository.findById(1L)).thenReturn(Optional.of(testStoreProduct));
        doNothing().when(storeProductRepository).delete(testStoreProduct);

        // When
        storeProductService.deleteAStoreProduct(1L);

        // Then
        verify(storeProductRepository).delete(testStoreProduct);
    }

    @Test
    void deleteAStoreProduct_NotFound_ShouldThrowException() {
        // Given
        when(storeProductRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> storeProductService.deleteAStoreProduct(999L));
    }
}