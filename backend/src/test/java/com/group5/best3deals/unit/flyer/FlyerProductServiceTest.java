package com.group5.best3deals.unit.flyer;

import com.group5.best3deals.flyer.dto.request.FlyerProductPutRequest;
import com.group5.best3deals.flyer.dto.request.FlyerProductRequest;
import com.group5.best3deals.flyer.dto.response.FlyerProductResponse;
import com.group5.best3deals.flyer.entity.FlyerProduct;
import com.group5.best3deals.flyer.repository.FlyerProductRepository;
import com.group5.best3deals.flyer.repository.FlyerRepository;
import com.group5.best3deals.flyer.service.FlyerProductService;
import com.group5.best3deals.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlyerProductServiceTest {

    @Mock
    private FlyerProductRepository flyerProductRepository;

    @Mock
    private FlyerRepository flyerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FlyerProductService flyerProductService;

    private FlyerProduct flyerProduct;

    private FlyerProductRequest flyerProductReq;

    private FlyerProductPutRequest flyerProductPutReq;

    @BeforeEach
    void setUp() {
        flyerProduct = FlyerProduct.builder()
                .id(1L)
                .flyerId(1L)
                .storeProductId(1L)
                .originalPrice(100.0F)
                .discountedPrice(50.0F)
                .build();

        flyerProductReq = new FlyerProductRequest(1L, 1L, 100.0F, 50.0F);

        flyerProductPutReq = new FlyerProductPutRequest();
        flyerProductPutReq.setStoreProductId(2L);
        flyerProductPutReq.setOriginalPrice(200.0F);
    }

    // Test get methods
    @Test
    public void testGetFlyerProductById() {
        when(flyerProductRepository.findById(1L)).thenReturn(Optional.of(flyerProduct));

        FlyerProduct result = flyerProductService.getFlyerProductById(1L);

        assertNotNull(result);
        assertEquals(flyerProduct.getId(), result.getId());
        verify(flyerProductRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetFlyerProductById_NotFound() {
        when(flyerProductRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            flyerProductService.getFlyerProductById(1L);
        });

        assertEquals("Product not found in flyer with 1", thrown.getMessage());
    }

    @Test
    public void testGetAllFlyerProductByProductId() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(flyerProductRepository.findAllByStoreProductId(1L)).thenReturn(List.of(flyerProduct));

        List<FlyerProduct> foundList = flyerProductService.getAllFlyerProductByProductId(1L);

        assertNotNull(foundList);
        assertEquals(1, foundList.size());
        verify(flyerProductRepository, times(1)).findAllByStoreProductId(1L);
    }

    @Test
    public void testGetAllFlyerProductByFlyerId() {
        when(flyerRepository.existsById(1L)).thenReturn(true);
        when(flyerProductRepository.findAllByFlyerId(1L)).thenReturn(List.of(flyerProduct));

        List<FlyerProduct> foundList = flyerProductService.getAllFlyerProductByFlyerId(1L);

        assertNotNull(foundList);
        assertEquals(1, foundList.size());
        assertEquals(flyerProduct, foundList.get(0));
    }

    // Test create method
    @Test
    public void testAddFlyerProduct() {
        when(flyerProductRepository.save(any(FlyerProduct.class))).thenReturn(flyerProduct);

        FlyerProductResponse response = flyerProductService.addFlyerProduct(flyerProductReq);

        assertNotNull(response);
        assertEquals(flyerProduct.getFlyerId(), response.getFlyerId());
        assertEquals(flyerProduct.getStoreProductId(), response.getStoreProductId());
        assertEquals(flyerProduct.getOriginalPrice(), response.getOriginalPrice());
        assertEquals(flyerProduct.getDiscountedPrice(), response.getDiscountedPrice());
        verify(flyerProductRepository, times(1)).save(any(FlyerProduct.class));
    }

    // Test put method
    @Test
    public void testUpdateFlyerProduct() {
        when(flyerProductRepository.findById(flyerProduct.getId())).thenReturn(Optional.of(flyerProduct));
        when(flyerProductRepository.save(any(FlyerProduct.class))).thenReturn(flyerProduct);

        FlyerProductResponse response = flyerProductService.updateFlyerProduct(flyerProduct.getId(), flyerProductPutReq);

        assertNotNull(response);
        assertEquals(flyerProduct.getId(), response.getId());
        assertEquals(flyerProduct.getFlyerId(), response.getFlyerId());
        assertEquals(flyerProductPutReq.getStoreProductId(), response.getStoreProductId());
        assertEquals(flyerProductPutReq.getOriginalPrice(), response.getOriginalPrice());
        assertEquals(flyerProduct.getDiscountedPrice(), response.getDiscountedPrice());
        verify(flyerProductRepository, times(1)).findById(1L);
        verify(flyerProductRepository, times(1)).save(any(FlyerProduct.class));
    }

    // Test delete method
    @Test
    public void testDeleteFlyerProduct() {
        when(flyerProductRepository.existsById(1L)).thenReturn(true);
        doNothing().when(flyerProductRepository).deleteById(1L);

        flyerProductService.deleteFlyerProduct(1L);

        verify(flyerProductRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteFlyerProduct_NotFound() {
        when(flyerProductRepository.existsById(1L)).thenReturn(false);

        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> {
            flyerProductService.deleteFlyerProduct(1L);
        });

        assertEquals("Product not found in flyer with 1", thrown.getMessage());
    }

}
