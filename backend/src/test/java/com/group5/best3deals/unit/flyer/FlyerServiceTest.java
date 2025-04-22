package com.group5.best3deals.unit.flyer;

import com.group5.best3deals.flyer.dto.request.FlyerPutRequest;
import com.group5.best3deals.flyer.dto.request.FlyerRequest;
import com.group5.best3deals.flyer.dto.response.FlyerResponse;
import com.group5.best3deals.flyer.entity.Flyer;
import com.group5.best3deals.flyer.repository.FlyerRepository;
import com.group5.best3deals.flyer.service.FlyerProductService;
import com.group5.best3deals.flyer.service.FlyerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlyerServiceTest {

    @Mock
    private FlyerRepository flyerRepository;

    @Mock
    private FlyerProductService flyerProductService;

    @InjectMocks
    private FlyerService flyerService;

    private Flyer flyer;

    private List<Flyer> flyers;

    private FlyerRequest flyerReq;

    private FlyerPutRequest flyerPutReq;

    @BeforeEach
    public void setUp() {
        flyer = new Flyer(1L, "Dec Flyer", "December Flyer", "url", 1L);

        flyers = Arrays.asList(
                new Flyer(1L, "Flyer1", "Description1", "url", 1L),
                new Flyer(2L, "Flyer2", "Description2", "url2", 1L)
        );

        flyerReq = new FlyerRequest("Dec Flyer", "December Flyer", "url", 1L);

        flyerPutReq = new FlyerPutRequest();
        flyerPutReq.setName("Nov Flyer");
        flyerPutReq.setDescription("November Flyer");
    }

    @Test
    public void testGetFlyerById() {
        when(flyerRepository.findById(anyLong())).thenReturn(Optional.of(flyer));

        FlyerResponse foundFlyer = flyerService.getFlyerById(1L);

        assertEquals(flyer.getId(), foundFlyer.getId());
        assertEquals(flyer.getName(), foundFlyer.getName());
        assertEquals(flyer.getDescription(), foundFlyer.getDescription());
        assertEquals(flyer.getImageUrl(), foundFlyer.getImageUrl());
        assertEquals(flyer.getStoreId(), foundFlyer.getStoreId());
    }

    @Test
    public void testGetAllFlyers() {
        when(flyerRepository.findAll()).thenReturn(flyers);

        List<Flyer> foundFlyers = flyerService.getAllFlyers();

        assertEquals(flyers.size(), foundFlyers.size());
        assertEquals(flyers.get(0).getId(), foundFlyers.get(0).getId());
        assertEquals(flyers.get(1).getId(), foundFlyers.get(1).getId());
    }

    @Test
    public void testGetAllFlyersByStoreId() {
        when(flyerRepository.findAllByStoreId(anyLong())).thenReturn(Optional.of(flyers));

        List<Flyer> foundFlyers = flyerService.getAllFlyersByStoreId(1L);

        assertNotNull(foundFlyers);
        assertEquals(flyers.size(), foundFlyers.size());
        assertEquals(flyers.get(0), foundFlyers.get(0));
        assertEquals(flyers.get(1), foundFlyers.get(1));
    }

    @Test
    public void testCreateFlyer() {
        when(flyerRepository.save(any(Flyer.class))).thenReturn(flyer);

        FlyerResponse createdFlyer = flyerService.createFlyer(flyerReq);

        assertNotNull(createdFlyer);
        assertEquals(flyer.getName(), createdFlyer.getName());
        assertEquals(flyer.getDescription(), createdFlyer.getDescription());
        assertEquals(flyer.getImageUrl(), createdFlyer.getImageUrl());
        assertEquals(flyer.getStoreId(), createdFlyer.getStoreId());
    }

    @Test
    public void testUpdateFlyer() {
        when(flyerRepository.findById(anyLong())).thenReturn(Optional.of(flyer));
        when(flyerProductService.getAllFlyerProductByFlyerId(anyLong())).thenReturn(new ArrayList<>());

        FlyerResponse updatedFlyer = flyerService.updateFlyer(1L, flyerPutReq);  // This should throw NoSuchElementException

        assertNotNull(updatedFlyer);
        assertEquals(flyer.getId(), updatedFlyer.getId());
        assertEquals(flyerPutReq.getName(), updatedFlyer.getName());
        assertEquals(flyerPutReq.getDescription(), updatedFlyer.getDescription());
        assertEquals(flyer.getImageUrl(), updatedFlyer.getImageUrl());
        assertEquals(flyer.getStoreId(), updatedFlyer.getStoreId());
    }

    @Test
    public void testDeleteFlyer() {
        when(flyerRepository.existsById(1L)).thenReturn(true);

        flyerService.deleteFlyer(1L);

        verify(flyerRepository, times(1)).deleteById(1L);
    }
}
