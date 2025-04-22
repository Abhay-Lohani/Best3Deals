package com.group5.best3deals.unit.storeProduct;

import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.storeproduct.repository.StoreProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreProductEntityTest {

    @Mock
    private StoreProductRepository storeProductRepository;

    // Test data builders
    private Product createTestProduct() {
        return Product.builder()
                .id(1L)
                .name("Test Product")
                .build();
    }

    private Store createTestStore() {
        return Store.builder()
                .id(1L)
                .name("Test Store")
                .build();
    }

    private Brand createTestBrand() {
        return Brand.builder()
                .id(1L)
                .name("Test Brand")
                .build();
    }

    private StoreProduct createTestStoreProduct() {
        return StoreProduct.builder()
                .id(1L)
                .product(createTestProduct())
                .store(createTestStore())
                .brand(createTestBrand())
                .price(99.99)
                .previousPrice(89.99)
                .quantityInStock(50)
                .productUrl("http://example.com/product")
                .dateAdded(LocalDateTime.now())
                .dateModified(LocalDateTime.now())
                .build();
    }

    private StoreProduct createTestStoreProductWithNullFields() {
        return StoreProduct.builder()
                .id(2L)
                .product(null)
                .store(null)
                .brand(null)
                .price(null)
                .previousPrice(null)
                .quantityInStock(null)
                .productUrl(null)
                .dateAdded(null)
                .dateModified(null)
                .build();
    }

    @Test
    void findById_ShouldReturnStoreProduct() {
        // Arrange
        StoreProduct mockProduct = createTestStoreProduct();
        when(storeProductRepository.findById(1L))
                .thenReturn(Optional.of(mockProduct));

        // Act
        Optional<StoreProduct> found = storeProductRepository.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        assertEquals(99.99, found.get().getPrice());
        assertEquals(50, found.get().getQuantityInStock());
        assertNotNull(found.get().getProduct());
        assertNotNull(found.get().getStore());
        assertNotNull(found.get().getBrand());
        verify(storeProductRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmptyForNonExistentId() {
        // Arrange
        when(storeProductRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act
        Optional<StoreProduct> found = storeProductRepository.findById(999L);

        // Assert
        assertTrue(found.isEmpty());
        verify(storeProductRepository, times(1)).findById(999L);
    }

    @Test
    void save_ShouldPersistStoreProduct() {
        // Arrange
        StoreProduct newProduct = createTestStoreProduct();
        newProduct.setId(null);

        StoreProduct savedProduct = createTestStoreProduct();
        when(storeProductRepository.save(newProduct))
                .thenReturn(savedProduct);

        // Act
        StoreProduct result = storeProductRepository.save(newProduct);

        // Assert
        assertNotNull(result.getId());
        assertEquals(99.99, result.getPrice());
        assertEquals(50, result.getQuantityInStock());
        assertNotNull(result.getDateAdded());
        verify(storeProductRepository, times(1)).save(newProduct);
    }

    @Test
    void save_ShouldHandleNullFields() {
        // Arrange
        StoreProduct newProduct = createTestStoreProductWithNullFields();
        when(storeProductRepository.save(newProduct))
                .thenReturn(newProduct);

        // Act
        StoreProduct result = storeProductRepository.save(newProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.getProduct());
        assertNull(result.getStore());
        assertNull(result.getBrand());
        assertNull(result.getPrice());
        assertNull(result.getPreviousPrice());
        assertNull(result.getQuantityInStock());
        assertNull(result.getProductUrl());
        assertNull(result.getDateAdded());
        assertNull(result.getDateModified());
        verify(storeProductRepository, times(1)).save(newProduct);
    }

    @Test
    void findAll_ShouldReturnAllStoreProducts() {
        // Arrange
        StoreProduct mockProduct1 = createTestStoreProduct();
        StoreProduct mockProduct2 = createTestStoreProductWithNullFields();

        when(storeProductRepository.findAll())
                .thenReturn(List.of(mockProduct1, mockProduct2));

        // Act
        List<StoreProduct> products = storeProductRepository.findAll();

        // Assert
        assertEquals(2, products.size());

        // Verify first product
        assertEquals(1L, products.get(0).getId());
        assertEquals(99.99, products.get(0).getPrice());

        // Verify second product
        assertEquals(2L, products.get(1).getId());
        assertNull(products.get(1).getPrice());

        verify(storeProductRepository, times(1)).findAll();
    }

    @Test
    void builder_ShouldCreateCompleteObject() {
        // Arrange & Act
        StoreProduct product = createTestStoreProduct();

        // Assert
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals(99.99, product.getPrice());
        assertEquals(89.99, product.getPreviousPrice());
        assertEquals(50, product.getQuantityInStock());
        assertEquals("http://example.com/product", product.getProductUrl());
        assertNotNull(product.getDateAdded());
        assertNotNull(product.getDateModified());
        assertNotNull(product.getProduct());
        assertNotNull(product.getStore());
        assertNotNull(product.getBrand());
    }

    @Test
    void builder_ShouldHandlePartialData() {
        // Arrange & Act
        StoreProduct product = StoreProduct.builder()
                .id(4L)
                .price(29.99)
                .quantityInStock(5)
                .build();

        // Assert
        assertNotNull(product);
        assertEquals(4L, product.getId());
        assertEquals(29.99, product.getPrice());
        assertEquals(5, product.getQuantityInStock());
        assertNull(product.getProduct());
        assertNull(product.getStore());
        assertNull(product.getBrand());
        assertNull(product.getPreviousPrice());
        assertNull(product.getProductUrl());
        assertNull(product.getDateAdded());
        assertNull(product.getDateModified());
    }

    @Test
    void toString_ShouldReturnNonEmptyString() {
        // Arrange
        StoreProduct product = createTestStoreProduct();

        // Act
        String result = product.toString();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("StoreProduct"));
        assertTrue(result.contains("id=1"));
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        StoreProduct product = new StoreProduct();
        Product testProduct = createTestProduct();
        Store testStore = createTestStore();
        Brand testBrand = createTestBrand();
        LocalDateTime now = LocalDateTime.now();

        // Act
        product.setId(5L);
        product.setProduct(testProduct);
        product.setStore(testStore);
        product.setBrand(testBrand);
        product.setPrice(199.99);
        product.setPreviousPrice(179.99);
        product.setQuantityInStock(100);
        product.setProductUrl("http://example.com/newproduct");
        product.setDateAdded(now);
        product.setDateModified(now);

        // Assert
        assertEquals(5L, product.getId());
        assertEquals(testProduct.getName(), product.getProduct().getName());
        assertEquals(testStore.getName(), product.getStore().getName());
        assertEquals(testBrand.getName(), product.getBrand().getName());
        assertEquals(199.99, product.getPrice());
        assertEquals(179.99, product.getPreviousPrice());
        assertEquals(100, product.getQuantityInStock());
        assertEquals("http://example.com/newproduct", product.getProductUrl());
        assertEquals(now, product.getDateAdded());
        assertEquals(now, product.getDateModified());
    }
    @Test
    void equals_ShouldReturnTrueForSameValues() {
        StoreProduct product1 = createTestStoreProduct();
        StoreProduct product2 = createTestStoreProduct();

        // Ensure they have the same ID for equality
        product2.setId(product1.getId());

        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalseForDifferentValues() {
        StoreProduct product1 = createTestStoreProduct();
        StoreProduct product2 = createTestStoreProduct();
        product2.setId(999L);

        assertNotEquals(product1, product2);
        assertNotEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void equals_ShouldBeConsistent() {
        StoreProduct product1 = createTestStoreProduct();
        StoreProduct product2 = createTestStoreProduct();
        product2.setId(product1.getId());

        boolean firstComparison = product1.equals(product2);
        boolean secondComparison = product1.equals(product2);

        assertEquals(firstComparison, secondComparison);
    }

    @Test
    void equals_ShouldHandleNull() {
        StoreProduct product = createTestStoreProduct();
        assertNotEquals(null, product);
    }

    @Test
    void equals_ShouldHandleDifferentClass() {
        StoreProduct product = createTestStoreProduct();
        assertNotEquals("string", product);
    }

}