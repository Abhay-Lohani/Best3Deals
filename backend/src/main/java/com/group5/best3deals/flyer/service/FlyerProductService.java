package com.group5.best3deals.flyer.service;

import com.group5.best3deals.flyer.dto.request.FlyerProductPutRequest;
import com.group5.best3deals.flyer.dto.request.FlyerProductRequest;
import com.group5.best3deals.flyer.dto.response.FlyerProductResponse;
import com.group5.best3deals.flyer.entity.FlyerProduct;
import com.group5.best3deals.flyer.repository.FlyerProductRepository;
import com.group5.best3deals.flyer.repository.FlyerRepository;
import com.group5.best3deals.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FlyerProductService {

    private final FlyerProductRepository flyerProductRepository;
    private final FlyerRepository flyerRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public FlyerProduct getFlyerProductById(Long id) {
        return flyerProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found in flyer with " + id));
    }

    @Transactional(readOnly = true)
    public List<FlyerProduct> getAllFlyerProductByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NoSuchElementException("Product not found in flyer with " + productId);
        }

        return flyerProductRepository.findAllByStoreProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<FlyerProduct> getAllFlyerProductByFlyerId(Long flyerId) {
        if (!flyerRepository.existsById(flyerId)) {
            throw new NoSuchElementException("Product not found in flyer with " + flyerId);
        }

        return flyerProductRepository.findAllByFlyerId(flyerId);
    }

    @Transactional
    public FlyerProductResponse addFlyerProduct(FlyerProductRequest flyerProductRequest) {
        FlyerProduct flyerProduct = FlyerProduct.builder()
                .flyerId(flyerProductRequest.getFlyerId())
                .storeProductId(flyerProductRequest.getStoreProductId())
                .originalPrice(flyerProductRequest.getOriginalPrice())
                .discountedPrice(flyerProductRequest.getDiscountedPrice())
                .build();

        flyerProductRepository.save(flyerProduct);

        return new FlyerProductResponse(flyerProduct.getId(), flyerProduct.getFlyerId(), flyerProduct.getStoreProductId(), flyerProduct.getOriginalPrice(), flyerProduct.getDiscountedPrice());
    }

    @Transactional
    public FlyerProductResponse updateFlyerProduct(Long id, FlyerProductPutRequest flyerProductPutRequest) {
        FlyerProduct existingFP = getFlyerProductById(id);

        FlyerProduct flyerProduct = FlyerProduct.builder()
                .id(existingFP.getId())
                .flyerId(flyerProductPutRequest.getFlyerId() != null ? flyerProductPutRequest.getFlyerId() : existingFP.getFlyerId())
                .storeProductId(flyerProductPutRequest.getStoreProductId() != null ? flyerProductPutRequest.getStoreProductId() : existingFP.getStoreProductId())
                .originalPrice(flyerProductPutRequest.getOriginalPrice() != null ? flyerProductPutRequest.getOriginalPrice() : existingFP.getOriginalPrice())
                .discountedPrice(flyerProductPutRequest.getDiscountedPrice() != null ? flyerProductPutRequest.getDiscountedPrice() : existingFP.getDiscountedPrice())
                .build();

        flyerProductRepository.save(flyerProduct);

        return new FlyerProductResponse(flyerProduct.getId(), flyerProduct.getFlyerId(), flyerProduct.getStoreProductId(), flyerProduct.getOriginalPrice(), flyerProduct.getDiscountedPrice());
    }

    @Transactional
    public void deleteFlyerProduct(Long id) {
        if (!flyerProductRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found in flyer with " + id);
        }

        flyerProductRepository.deleteById(id);
    }
}