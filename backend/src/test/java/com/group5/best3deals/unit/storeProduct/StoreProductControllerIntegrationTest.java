package com.group5.best3deals.unit.storeProduct;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;
import java.util.List;

import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.storeproduct.controller.StoreProductController;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.storeproduct.service.StoreProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class StoreProductControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private StoreProductService storeProductService; // Mock the service

    @InjectMocks
    private StoreProductController storeProductController; // Inject the mocks into the controller

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Set up MockMvc to use the controller
        mockMvc = MockMvcBuilders.standaloneSetup(storeProductController).build();
    }

    @Test
    public void testGetNearbyStoreProducts_ReturnsOk() throws Exception {
        // Arrange
        String productName = "Organic Apples";
        double latitude = 44.648764;
        double longitude = -63.575239;
        double distanceInKm = 10000;

        // Mock the service response
        StoreProduct storeProduct = StoreProduct.builder()
                .id(1L)
                .productUrl("http://apple.com/iphone-15-plus")
                .price(8.99)
                .quantityInStock(90)
                .store(Store.builder()
                        .name("wallmart")
                        .build())
                .product(Product.builder()
                        .name("Apple iPhone 13")
                        .build())
                .brand(Brand.builder()
                        .name("apple")
                        .build())
                .build();

        List<StoreProduct> mockResponse = Collections.singletonList(storeProduct);
        when(storeProductService.getStoreProductsWithinDistance(productName, latitude, longitude, distanceInKm))
                .thenReturn(mockResponse);

        // Act and Assert
        mockMvc.perform(get("/store-products/nearby/best3deals")
                        .param("organicApples", productName)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("distanceInKM", String.valueOf(distanceInKm)))
                .andExpect(status().isOk());

    }
}