package com.group5.best3deals.unit.store;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.location.entity.Location;
import com.group5.best3deals.store.controller.StoreController;
import com.group5.best3deals.store.dto.request.StorePutRequest;
import com.group5.best3deals.store.dto.request.StoreRequest;
import com.group5.best3deals.store.dto.response.StoreResponse;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.store.service.StoreService;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreControllerTest {

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreController storeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private StoreResponse createMockStoreResponse() {
        // Replace these with appropriate mock values that match your StoreResponse constructor
        return new StoreResponse(
                1L,                    // id
                "Test Store",          // name
                "123 Main St",
               "",
               new Location (0L,
                40.7128,              // latitude
                -74.0060, new Date())  ,      // longitude
                1L                    // brandId
        );
    }

    @Test
    @WithMockUser
    void getStoreById_ShouldReturnStore() {
        // Arrange
        Long storeId = 1L;
        Store mockStore = Store.builder().id(storeId).build();

        when(storeService.getStoreById(storeId)).thenReturn(mockStore);

        // Act
        ResponseEntity<ApiResponse<Store>> response = storeController.getStoreById(storeId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockStore, response.getBody().getData());
        verify(storeService, times(1)).getStoreById(storeId);
    }

    @Test
    @WithMockUser
    void getAllStores_ShouldReturnAllStores() {
        // Arrange
        Store store1 = new Store();
        Store store2 = new Store();
        List<Store> mockStores = Arrays.asList(store1, store2);
        when(storeService.getAllStores()).thenReturn(mockStores);

        // Act
        ResponseEntity<ApiResponse<List<Store>>> response = storeController.getAllStores();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(storeService, times(1)).getAllStores();
    }

    @Test
    @WithMockUser
    void addStore_ShouldCreateNewStore() {
        // Arrange
        StoreRequest request = new StoreRequest();
        StoreResponse mockResponse = createMockStoreResponse();
        when(storeService.createStore(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ApiResponse<StoreResponse>> response = storeController.addStore(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockResponse, response.getBody().getData());
        verify(storeService, times(1)).createStore(request);
    }

    @Test
    @WithMockUser
    void updateStore_ShouldUpdateExistingStore() {
        // Arrange
        Long storeId = 1L;
        StorePutRequest request = new StorePutRequest();
        StoreResponse mockResponse = createMockStoreResponse();
        when(storeService.updateStore(storeId, request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ApiResponse<StoreResponse>> response = storeController.updateStore(storeId, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockResponse, response.getBody().getData());
        verify(storeService, times(1)).updateStore(storeId, request);
    }

    @Test
    @WithMockUser
    void deleteStore_ShouldDeleteStore() {
        // Arrange
        Long storeId = 1L;
        doNothing().when(storeService).deleteStoreById(storeId);

        // Act
        ResponseEntity<ApiResponse<Null>> response = storeController.deleteStore(storeId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        verify(storeService, times(1)).deleteStoreById(storeId);
    }

    @Test
    @WithMockUser
    void getStoresWithinRadius_ShouldReturnStores() {
        // Arrange
        Double latitude = 40.7128;
        Double longitude = -74.0060;
        Integer radius = 10;
        Store store1 = new Store();
        Store store2 = new Store();
        List<Store> mockStores = Arrays.asList(store1, store2);
        when(storeService.getStoresWithinRadius(latitude, longitude, radius)).thenReturn(mockStores);

        // Act
        ResponseEntity<ApiResponse<List<Store>>> response = storeController.getStoresWithinRadius(latitude, longitude, radius);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(storeService, times(1)).getStoresWithinRadius(latitude, longitude, radius);
    }

    @Test
    @WithMockUser
    void getStoresWithinBrand_ShouldReturnStores() {
        // Arrange
        Long brandId = 1L;
        Store store1 = new Store();
        Store store2 = new Store();
        List<Store> mockStores = Arrays.asList(store1, store2);
        when(storeService.getStoresByBrandId(brandId)).thenReturn(mockStores);

        // Act
        ResponseEntity<ApiResponse<List<Store>>> response = storeController.getStoresWithinBrand(brandId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(storeService, times(1)).getStoresByBrandId(brandId);
    }
}