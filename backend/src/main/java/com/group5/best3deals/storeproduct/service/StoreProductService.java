package com.group5.best3deals.storeproduct.service;


import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.brand.service.BrandService;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.product.service.ProductService;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.store.service.StoreService;

import com.group5.best3deals.storeproduct.dto.request.StoreProductPutRequest;
import com.group5.best3deals.storeproduct.dto.request.StoreProductRequest;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.storeproduct.repository.StoreProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StoreProductService {

    private static final int TOP_CHEAPEST_PRODUCTS_COUNT = 3; // Define a constant

    @Autowired
    private StoreProductRepository storeProductRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private StoreService storeService;

    public List<StoreProduct> getStoreProductsWithinDistance(String productName, double latitude, double longitude, double distanceInKm) {
        // Get product by name
        List<StoreProduct> storeProducts = getStoreProductByName(productName);

        // Get stores within distance
        List<Store> storesWithinDistance = storeService.findStoresWithinDistance(latitude, longitude, distanceInKm);

        // Filter products whose stores are within distance
        List<StoreProduct> filteredProducts = storeProducts.stream()
                .filter(sp -> storesWithinDistance.contains(sp.getStore())).sorted(Comparator.comparingDouble(StoreProduct::getPrice))
                .collect(Collectors.toList());

        // return the top 3 cheapest products
        return filteredProducts.size() <= TOP_CHEAPEST_PRODUCTS_COUNT ?
                filteredProducts :
                filteredProducts.subList(0, TOP_CHEAPEST_PRODUCTS_COUNT);
    }

    public List<StoreProduct> getStoreProductByName(String productName) {
        return storeProductRepository.findByProductName(productName);
    }

    public StoreProduct createStoreProduct(StoreProductRequest request) {
        // confirm that the store exists
        Product product = productService.getProductById(request.getProductId());
        // confirm that the product exists
        Store store = storeService.getStoreById(request.getStoreId());
        // confirm that the brand exist
        Brand brand = brandService.getBrandById(request.getBrandId());
        StoreProduct storeProduct = StoreProduct.builder()
                .product(product)
                .store(store)
                .brand(brand)
                .productUrl(request.getStoreProductUrl())
                .price(request.getPrice())
                .quantityInStock(request.getQuantityInStock())
                .previousPrice(request.getPrice())
                .dateAdded(LocalDateTime.now())
                .dateModified(LocalDateTime.now()).build();
        return storeProductRepository.save(storeProduct);
    }

    public StoreProduct updateStoreProduct(Long id, StoreProductPutRequest request) {
        StoreProduct existingStoreProduct = storeProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Store Product not found with id " +id));
        Store store = storeService.getStoreById(request.getStoreId());
        Product product = productService.getProductById(request.getProductId());
        Brand brand = brandService.getBrandById(request.getBrandId());


        StoreProduct storeProduct = StoreProduct.builder()
                .id(existingStoreProduct.getId())
                .store(request.getStoreId() != null ? store : existingStoreProduct.getStore())
                .product(request.getProductId() != null ? product:existingStoreProduct.getProduct())
                .brand((request.getBrandId() != null ? brand:existingStoreProduct.getBrand()))
                .previousPrice((request.getPrice() != null ? existingStoreProduct.getPrice() : existingStoreProduct.getPreviousPrice()))
                .price(request.getPrice() != null? request.getPrice():existingStoreProduct.getPrice())
                .quantityInStock(request.getQuantityInStock() != null?request.getQuantityInStock(): existingStoreProduct.getQuantityInStock())
                .productUrl(request.getStoreProductUrl() != null?request.getStoreProductUrl():existingStoreProduct.getProductUrl())
                .dateModified(LocalDateTime.now())
                .build();
        return storeProductRepository.save(storeProduct);
    }

    public StoreProduct getStoreProduct(Long id) {
        return storeProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Store Product not found with id " +id));
    }

    public List<StoreProduct> getAllStoreProducts() {
        return storeProductRepository.findAll();
    }

    public void deleteAStoreProduct(Long id) {
        StoreProduct storeProduct = storeProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Store Product not found with id " +id));
        storeProductRepository.delete(storeProduct);
    }
}