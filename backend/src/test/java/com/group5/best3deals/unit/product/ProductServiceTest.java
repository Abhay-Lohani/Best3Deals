package com.group5.best3deals.unit.product;

import com.group5.best3deals.category.entity.Category;
import com.group5.best3deals.category.service.CategoryService;
import com.group5.best3deals.product.dto.request.ProductPutRequest;
import com.group5.best3deals.product.dto.request.ProductRequest;
import com.group5.best3deals.product.dto.response.ProductResponse;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.product.repository.ProductRepository;
import com.group5.best3deals.product.service.ProductService;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.store.service.StoreService;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.storeproduct.repository.StoreProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreProductRepository storeProductRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    private Product.ProductBuilder productBuilder() {
        return Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .imgUrl("test.jpg");
    }

    private StoreProduct.StoreProductBuilder storeProductBuilder() {
        return StoreProduct.builder()
                .id(1L);
    }

    private Category.CategoryBuilder categoryBuilder() {
        return Category.builder()
                .id(1L)
                .name("Test Category");
    }

    private Store.StoreBuilder storeBuilder() {
        return Store.builder()
                .id(1L)
                .name("Test Store");
    }

    private ProductRequest productRequestBuilder() {
        return new ProductRequest("Test Product","Test Description","test.jpg",1L);
    }

    private ProductPutRequest productPutRequestBuilder() {
        ProductPutRequest p = new  ProductPutRequest();
                p.setName("Updated Product");
                p.setDescription("Updated Description");
                p.setImgUrl("updated.jpg");
                p.setCategoryId(2L);
                return p;
    }

    private ProductResponse productResponseBuilder() {
        ProductResponse response = new ProductResponse(1L,"Test Product","Test Description","test.jpg",1L);
       return response;
    }

    @Test
    public void testGetProductById() {
        // Arrange
        Product product = productBuilder().build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertEquals(1L, result.getId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllProducts() {
        // Arrange
        Product product1 = productBuilder().build();
        Product product2 = productBuilder().id(2L).build();
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllProductsByCategoryId() {
        // Arrange
        Product product1 = productBuilder().build();
        Product product2 = productBuilder().id(2L).build();
        when(productRepository.findAllByCategoryId(1L)).thenReturn(Optional.of(Arrays.asList(product1, product2)));

        // Act
        List<Product> result = productService.getAllProductsByCategoryId(1L);

        // Assert
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAllByCategoryId(1L);
    }

    @Test
    public void testGetAllProductsByStoreId() {
        // Arrange
        StoreProduct product1 = storeProductBuilder().build();
        StoreProduct product2 = storeProductBuilder().id(2L).build();
        when(storeProductRepository.findAllByStoreId(1L)).thenReturn(Optional.of(Arrays.asList(product1, product2)));

        // Act
        List<StoreProduct> result = productService.getAllProductsByStoreId(1L);

        // Assert
        assertEquals(2, result.size());
        verify(storeProductRepository, times(1)).findAllByStoreId(1L);
    }

    @Test
    public void testGetAllProductsWithinRadius() {
        // Arrange
        Store store1 = storeBuilder().build();
        Store store2 = storeBuilder().id(2L).build();

        StoreProduct product1 = storeProductBuilder().build();
        StoreProduct product2 = storeProductBuilder().id(2L).build();

        when(storeService.getStoresWithinRadius(40.7128, -74.0060, 10))
                .thenReturn(Arrays.asList(store1, store2));
        when(storeProductRepository.findAllByStoreId(1L)).thenReturn(Optional.of(Collections.singletonList(product1)));
        when(storeProductRepository.findAllByStoreId(2L)).thenReturn(Optional.of(Collections.singletonList(product2)));

        // Act
        List<StoreProduct> result = productService.getAllProductsWithinRadius(40.7128, -74.0060, 10);

        // Assert
        assertEquals(2, result.size());
        verify(storeService, times(1)).getStoresWithinRadius(40.7128, -74.0060, 10);
    }

    @Test
    public void testDeleteProduct() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }
}