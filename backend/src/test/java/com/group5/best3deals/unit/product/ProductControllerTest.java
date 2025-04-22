package com.group5.best3deals.unit.product;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.product.controller.ProductController;
import com.group5.best3deals.product.dto.request.ProductPutRequest;
import com.group5.best3deals.product.dto.request.ProductRequest;
import com.group5.best3deals.product.dto.response.ProductResponse;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.product.service.ProductService;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.runner.Version.id;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    public void testGetProducts() {
        // Arrange
        Product product1 = Product.builder().build();
        Product product2 = Product.builder().build();
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // Act
        ResponseEntity<ApiResponse<List<Product>>> response = productController.getProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testGetProductById() {
        // Arrange
        Long productId = 1L;
        Product product = Product.builder().
     id(productId).name("").build();
        when(productService.getProductById(productId)).thenReturn(product);

        // Act
        ResponseEntity<ApiResponse<Product>> response = productController.getProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(productId, response.getBody().getData().getId());
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    public void testCreateProduct() {
        // Arrange
        ProductRequest request = new ProductRequest("Test", "Description", "image.jpg", 1L);
        ProductResponse expectedResponse = new ProductResponse(1L, "Test", "Description", "image.jpg", 1L);
        when(productService.createProduct(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<ProductResponse>> response = productController.createProduct(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(expectedResponse, response.getBody().getData());
        verify(productService, times(1)).createProduct(request);
    }

    @Test
    public void testUpdateProduct() {
        // Arrange
        Long productId = 1L;
        ProductPutRequest request = new ProductPutRequest();
        request.setName("Updated");
        ProductResponse expectedResponse = new ProductResponse(productId, "Updated", "Desc", "img.jpg", 1L);
        when(productService.updateProduct(productId, request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<ProductResponse>> response = productController.updateProduct(productId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(expectedResponse, response.getBody().getData());
        verify(productService, times(1)).updateProduct(productId, request);
    }

    @Test
    public void testDeleteProduct() {
        // Arrange
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(productId);

        // Act
        ResponseEntity<ApiResponse<Null>> response = productController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    public void testGetProductsByCategory() {
        // Arrange
        Long categoryId = 1L;
        Product product1 = Product.builder().build();
        Product product2 = Product.builder().build();
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProductsByCategoryId(categoryId)).thenReturn(products);

        // Act
        ResponseEntity<ApiResponse<List<Product>>> response = productController.getProductsByCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(productService, times(1)).getAllProductsByCategoryId(categoryId);
    }

    @Test
    public void testGetProductsByStoreId() {
        // Arrange
        Long storeId = 1L;
        StoreProduct product1 = new StoreProduct();
        StoreProduct product2 = new StoreProduct();
        List<StoreProduct> products = Arrays.asList(product1, product2);
        when(productService.getAllProductsByStoreId(storeId)).thenReturn(products);

        // Act
        ResponseEntity<ApiResponse<List<StoreProduct>>> response = productController.getProductsWithinRadius(storeId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(productService, times(1)).getAllProductsByStoreId(storeId);
    }

    @Test
    public void testGetProductsWithinRadius() {
        // Arrange
        Double latitude = 40.7128;
        Double longitude = -74.0060;
        Integer radius = 10;
        StoreProduct product1 = new StoreProduct();
        StoreProduct product2 = new StoreProduct();
        List<StoreProduct> products = Arrays.asList(product1, product2);
        when(productService.getAllProductsWithinRadius(latitude, longitude, radius)).thenReturn(products);

        // Act
        ResponseEntity<ApiResponse<List<StoreProduct>>> response =
                productController.getProductsWithinRadius(latitude, longitude, radius);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(productService, times(1)).getAllProductsWithinRadius(latitude, longitude, radius);
    }

    @Test
    public void testGetProductsByCategory_EmptyResult() {
        // Arrange
        Long categoryId = 99L;
        when(productService.getAllProductsByCategoryId(categoryId)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ApiResponse<List<Product>>> response = productController.getProductsByCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().isEmpty());
    }
}