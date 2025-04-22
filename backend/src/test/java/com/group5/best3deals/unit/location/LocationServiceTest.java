package com.group5.best3deals.unit.location;


import com.group5.best3deals.location.dto.request.LocationPutRequest;
import com.group5.best3deals.location.dto.request.LocationRequest;
import com.group5.best3deals.location.entity.Location;
import com.group5.best3deals.location.repository.LocationRepository;
import com.group5.best3deals.location.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location location;

    private List<Location> locations;

    private LocationRequest locationReq;

    private LocationPutRequest locationPutReq;

    @BeforeEach
    public void setUp() {
        location = Location.builder().id(1L).longitude(1.0).latitude(1.0).build();

        locationReq = new LocationRequest(1.0, 1.0);

        locations = Arrays.asList(
                location,
                Location.builder().id(1L).longitude(2.0).latitude(2.0).build()
        );

        locationPutReq = new LocationPutRequest();
        locationPutReq.setLatitude(51.0);
    }

    @Test
    public void testGetLocationById() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        Location foundLocation = locationService.getLocationById(1L);

        assertEquals(location, foundLocation);
    }

    @Test
    public void testGetAllLocations() {
        when(locationRepository.findAll()).thenReturn(locations);

        List<Location> foundLocations = locationService.getAllLocations();

        assertNotNull(foundLocations);
        assertEquals(locations.size(), foundLocations.size());
        assertEquals(locations.get(0), foundLocations.get(0));
        assertEquals(locations.get(1), foundLocations.get(1));
    }

    @Test
    public void testCreateLocation() {
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        Location createdLocation = locationService.createLocation(locationReq);

        assertNotNull(createdLocation);
        assertEquals(location.getLongitude(), createdLocation.getLongitude());
        assertEquals(location.getLatitude(), createdLocation.getLatitude());
    }

    @Test
    public void testUpdateLocation() {
        when(locationRepository.findById(any(Long.class))).thenReturn(Optional.of(location));

        Location updatedLocation = locationService.updateLocation(1L, locationPutReq);

        assertEquals(locationPutReq.getLatitude(), updatedLocation.getLatitude());
        assertEquals(location.getLongitude(), updatedLocation.getLongitude());
    }

    @Test
    public void testDeleteLocation() {
        when(locationRepository.existsById(1L)).thenReturn(true);

        locationService.deleteLocation(1L);

        verify(locationRepository, times(1)).deleteById(1L);
    }
}
