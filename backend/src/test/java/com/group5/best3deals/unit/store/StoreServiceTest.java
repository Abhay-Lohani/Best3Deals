package com.group5.best3deals.unit.store;

import com.group5.best3deals.location.dto.request.LocationPutRequest;
import com.group5.best3deals.location.dto.request.LocationRequest;
import com.group5.best3deals.location.entity.Location;
import com.group5.best3deals.location.service.LocationService;
import com.group5.best3deals.store.dto.request.StorePutRequest;
import com.group5.best3deals.store.dto.request.StoreRequest;
import com.group5.best3deals.store.dto.response.StoreResponse;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.store.repository.StoreRepository;
import com.group5.best3deals.store.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private StoreService storeService;

    private Store store;

    private List<Store> stores;

    private StoreRequest storeReq;

    private StorePutRequest storePutReq;

    private Location updatedLocation;
    @BeforeEach
    public void setUp() {
        Location location = Location.builder().id(1L).latitude(1.0).longitude(1.0).build();
        store = Store.builder().id(1L).name("Walmart").address("Coburg Rd").imgUrl("url").location(location).brandId(1L).build();

        // For getAll test
        Location location2 = Location.builder().id(2L).latitude(2.0).longitude(2.0).build();
        stores = Arrays.asList(
                store,
                Store.builder().id(2L).name("Dollarama").address("Downtown").imgUrl("url2").location(location2).brandId(2L).build()
        );

        // For create test
        LocationRequest locationReq = new LocationRequest(1.0, 1.0);
        storeReq = new StoreRequest("Walmart", "Coburg Rd", "url", locationReq, null);

        // For update test
        LocationPutRequest locationPutReq = new LocationPutRequest();
        locationPutReq.setLatitude(2.0);

        updatedLocation = Location.builder().id(1L).latitude(2.0).longitude(1.0).build();

        storePutReq = new StorePutRequest();
        storePutReq.setName("Staples");
        storePutReq.setLocation(locationPutReq);
    }

    @Test
    public void testGetStoreById() {
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));

        Store foundStore = storeService.getStoreById(1L);

        assertNotNull(foundStore);
        assertEquals(store.getId(), foundStore.getId());
        assertEquals(store.getLocation().getId(), foundStore.getLocation().getId());
    }

    @Test
    public void testGetAllStores() {
        when(storeRepository.findAll()).thenReturn(stores);

        List<Store> foundStores = storeService.getAllStores();

        assertNotNull(foundStores);
        assertEquals(stores.size(), foundStores.size());
        assertEquals(stores.get(0), foundStores.get(0));
        assertEquals(stores.get(1), foundStores.get(1));
    }

    @Test
    public void testGetStoresWithinRadius() {
        when(storeRepository.findAll()).thenReturn(stores);

        List<Store> foundStores = storeService.getStoresWithinRadius(1.0,1.0,1);

        assertNotNull(foundStores);
        assertEquals(1, foundStores.size());
        assertEquals(stores.get(0), foundStores.get(0));
    }

    @Test
    public void testGetStoresByBrandId() {
        when(storeRepository.findAllByBrandId(1L)).thenReturn(Optional.of(List.of(store)));

        List<Store> foundStores = storeService.getStoresByBrandId(1L);

        assertNotNull(foundStores);
        assertEquals(1, foundStores.size());
        assertEquals(store, foundStores.get(0));
    }

    @Test
    public void testCreateStore() {
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        when(locationService.createLocation(any(LocationRequest.class))).thenReturn(store.getLocation());

        StoreResponse createdStore = storeService.createStore(storeReq);

        assertNotNull(createdStore);
        assertEquals(store.getName(), createdStore.getName());
        assertEquals(store.getAddress(), createdStore.getAddress());
        assertEquals(store.getImgUrl(), createdStore.getImgUrl());
        assertEquals(store.getLocation().getLatitude(), createdStore.getLocation().getLatitude());
        assertEquals(store.getLocation().getLongitude(), createdStore.getLocation().getLongitude());
    }

    @Test
    public void testUpdateStore() {
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        when(locationService.updateLocation(any(Long.class), any(LocationPutRequest.class))).thenReturn(updatedLocation);

        StoreResponse updatedStore = storeService.updateStore(1L, storePutReq);

        assertNotNull(updatedStore);
        assertEquals(storePutReq.getName(), updatedStore.getName());
        assertEquals(store.getAddress(), updatedStore.getAddress());
        assertEquals(store.getImgUrl(), updatedStore.getImgUrl());
        assertEquals(storePutReq.getLocation().getLatitude(), updatedStore.getLocation().getLatitude());
        assertEquals(store.getLocation().getLongitude(), updatedStore.getLocation().getLongitude());
    }

    @Test
    public void testUpdateStore_NotFound() {
        when(storeRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            storeService.updateStore(2L, storePutReq);  // This should throw NoSuchElementException
        } catch (NoSuchElementException e) {
            assertEquals("Store not found with 2", e.getMessage());
        }
    }

    @Test
    public void testDeleteStore() {
        when(storeRepository.existsById(1L)).thenReturn(true);

        storeService.deleteStoreById(1L);

        verify(storeRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteStore_NotFound() {
        when(storeRepository.existsById(2L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> storeService.deleteStoreById(2L));

        assertEquals("Store not found with 2", exception.getMessage());
    }
    @Test
    public void testFindStoresWithinDistance_ReturnsEmptyListWhenNoStoresFound() {
        // Arrange
        double latitude = 44.648764;
        double longitude = -63.575239;
        double distanceInKm = 10000;

        // Mock the repository to return an empty list
        when(storeRepository.findStoresWithinDistance(latitude, longitude, distanceInKm))
                .thenReturn(Collections.emptyList());

        // Act
        List<Store> storesWithinDistance = storeService.findStoresWithinDistance(latitude, longitude, distanceInKm);

        // Assert
        assertNotNull(storesWithinDistance, "The result should not be null");
        assertTrue(storesWithinDistance.isEmpty(), "The result should be an empty list");

        // Verify the repository method was called
        verify(storeRepository, times(1)).findStoresWithinDistance(latitude, longitude, distanceInKm);
    }
    @Test
    public void testFindStoresWithinDistance_ReturnsStoresWithinDistance() {
        // Arrange
        double latitude = 44.648764;
        double longitude = -63.575239;
        double distanceInKm = 10000;
        // Creat mocj location
        Location location = Location.builder()
                .id(1L)
                .longitude(44.648764)
                .longitude(-63.575239)
                .build();
        Location location2 = Location.builder()
                .id(1L)
                .longitude(44.650)
                .longitude(-63.577)
                .build();
        // Create mock stores
        Store store1 = Store.builder()
                .id(1L)
                .name("Store A")
                .imgUrl("imgUrl")
                .address("1333 south park")
                .location(location)
                .build();

        Store store2 = Store.builder()
                .id(2L)
                .name("Store B")
                .imgUrl("imgUrl")
                .address("1333 south park")
                .location(location2)
                .build();
        List<Store> mockStores = Arrays.asList(store1, store2);

        // Mock the repository's behavior
        when(storeRepository.findStoresWithinDistance(latitude, longitude, distanceInKm))
                .thenReturn(mockStores);

        // Act
        List<Store> storesWithinDistance = storeService.findStoresWithinDistance(latitude, longitude, distanceInKm);

        // Assert
        assertNotNull(storesWithinDistance, "The result should not be null");
        assertEquals(2, storesWithinDistance.size(), "The result should contain 2 stores");
        assertEquals("Store A", storesWithinDistance.get(0).getName(), "The first store's name should be 'Store A'");
        assertEquals("Store B", storesWithinDistance.get(1).getName(), "The second store's name should be 'Store B'");

        // Verify the repository method was called
        verify(storeRepository, times(1)).findStoresWithinDistance(latitude, longitude, distanceInKm);
    }
}
