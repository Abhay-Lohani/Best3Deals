package com.group5.best3deals.location.service;

import com.group5.best3deals.location.dto.request.LocationPutRequest;
import com.group5.best3deals.location.dto.request.LocationRequest;
import com.group5.best3deals.location.entity.Location;
import com.group5.best3deals.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Location getLocationById(Long id) {
        return locationRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No location found with " + id));
    }

    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Transactional
    public Location createLocation(LocationRequest locationRequest) {
        Location location = Location.builder()
                .longitude(locationRequest.getLongitude())
                .latitude(locationRequest.getLatitude())
                .timestamp(new Date())
                .build();

        locationRepository.save(location);

        return location;
    }

    @Transactional
    public Location updateLocation(Long id, LocationPutRequest locationPutReq) {
        Location existingLocation = getLocationById(id);

        Location location = Location.builder()
                .id(existingLocation.getId())
                .latitude(locationPutReq.getLatitude() != null ? locationPutReq.getLatitude() : existingLocation.getLatitude() )
                .longitude(locationPutReq.getLongitude() != null ? locationPutReq.getLongitude() : existingLocation.getLongitude())
                .timestamp(new Date())
                .build();

        locationRepository.save(location);

        return location;
    }

    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new NoSuchElementException("No location found with " + id);
        }

        locationRepository.deleteById(id);
    }
}

