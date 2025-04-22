package com.group5.best3deals.unit.storeProduct;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.storeproduct.controller.StoreProductController;
import com.group5.best3deals.storeproduct.dto.request.StoreProductPutRequest;
import com.group5.best3deals.storeproduct.dto.request.StoreProductRequest;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.storeproduct.service.StoreProductService;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreProductControllerTest {

    @Mock
    private StoreProductService storeProductService;

    @InjectMocks
    private StoreProductController storeProductController;

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
    @WithMockUser
    void getStoreProduct_ShouldReturnStoreProduct() {
        when(storeProductService.getStoreProduct(1L)).thenReturn(testStoreProduct);

        ResponseEntity<ApiResponse<StoreProduct>> response = storeProductController.getStoreProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testStoreProduct, response.getBody().getData());
        verify(storeProductService).getStoreProduct(1L);
    }

    @Test
    @WithMockUser
    void getAllStoreProducts_ShouldReturnAllStoreProducts() {
        List<StoreProduct> storeProducts = Arrays.asList(testStoreProduct);
        when(storeProductService.getAllStoreProducts()).thenReturn(storeProducts);

        ResponseEntity<ApiResponse<List<StoreProduct>>> response = storeProductController.getAllStoreProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(testStoreProduct, response.getBody().getData().get(0));
        verify(storeProductService).getAllStoreProducts();
    }

    @Test
    @WithMockUser
    void getNearbyStoreProducts_ShouldReturnNearbyProducts() {
        List<StoreProduct> nearbyProducts = Arrays.asList(testStoreProduct);
        when(storeProductService.getStoreProductsWithinDistance("organicApples", 40.7128, -74.0060, 10.0))
                .thenReturn(nearbyProducts);

        ResponseEntity<List<StoreProduct>> response = storeProductController.getNearbyStoreProducts(
                "organicApples", 40.7128, -74.0060, 10.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testStoreProduct, response.getBody().get(0));
        verify(storeProductService).getStoreProductsWithinDistance("organicApples", 40.7128, -74.0060, 10.0);
    }

    @Test
    @WithMockUser
    void createStoreProduct_ShouldCreateAndReturnStoreProduct() {
        when(storeProductService.createStoreProduct(testStoreProductRequest)).thenReturn(testStoreProduct);

        ResponseEntity<ApiResponse<StoreProduct>> response = storeProductController.createStoreProduct(testStoreProductRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testStoreProduct, response.getBody().getData());
        verify(storeProductService).createStoreProduct(testStoreProductRequest);
    }

    @Test
    @WithMockUser
    void updateProduct_ShouldUpdateAndReturnStoreProduct() {
        when(storeProductService.updateStoreProduct(1L, testStoreProductPutRequest)).thenReturn(testStoreProduct);

        ResponseEntity<ApiResponse<StoreProduct>> response = storeProductController.updateProduct(1L, testStoreProductPutRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testStoreProduct, response.getBody().getData());
        verify(storeProductService).updateStoreProduct(1L, testStoreProductPutRequest);
    }

    @Test
    @WithMockUser
    void deleteStoreProduct_ShouldDeleteAndReturnSuccess() {
        doNothing().when(storeProductService).deleteAStoreProduct(1L);

        ResponseEntity<ApiResponse<Null>> response = storeProductController.deleteStoreProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());
        verify(storeProductService).deleteAStoreProduct(1L);
    }

    @Test
    @WithMockUser
    void getStoreProduct_NotFound_ShouldThrowException() {
        when(storeProductService.getStoreProduct(999L)).thenThrow(new NoSuchElementException("Not found"));

        assertThrows(NoSuchElementException.class, () -> storeProductController.getStoreProduct(999L));
    }
}