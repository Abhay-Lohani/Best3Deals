package com.group5.best3deals.product.service;

import com.group5.best3deals.category.entity.Category;
import com.group5.best3deals.category.service.CategoryService;
import com.group5.best3deals.product.dto.request.ProductPutRequest;
import com.group5.best3deals.product.dto.request.ProductRequest;
import com.group5.best3deals.product.dto.response.ProductResponse;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.product.repository.ProductRepository;
import com.group5.best3deals.storeproduct.repository.StoreProductRepository;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StoreProductRepository storeProductRepository;
    private final StoreService storeService;
    private final CategoryService categoryService;


    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with " + id));
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProductsByCategoryId(Long categoryId) {
        return productRepository.findAllByCategoryId(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with Category ID: " + categoryId));
    }

    @Transactional(readOnly = true)
    public List<StoreProduct> getAllProductsByStoreId(Long storeId) {
        return storeProductRepository.findAllByStoreId(storeId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with Store ID: " + storeId))
                ;
    }

    @Transactional(readOnly = true)
    public List<StoreProduct> getAllProductsWithinRadius(Double latitude, Double longitude, Integer radius) {
        List<Store> nearStores = storeService.getStoresWithinRadius(latitude, longitude, radius);
        List<StoreProduct> products = new ArrayList<>();

        for (Store store : nearStores) {
            products.addAll(getAllProductsByStoreId(store.getId()));
        }

        return products;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Category category = categoryService.getCategoryById(productRequest.getCategoryId());
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .imgUrl(productRequest.getImgUrl())
                .category(category)
                .build();

        productRepository.save(product);

        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getImgUrl(),
                product.getCategory().getId());
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductPutRequest productRequest) {
        Category category = categoryService.getCategoryById(productRequest.getCategoryId());
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with " + id));

        Product product = Product.builder()
                .id(existingProduct.getId())
                .name(productRequest.getName() != null ? productRequest.getName() : existingProduct.getName())
                .description(productRequest.getDescription() != null ? productRequest.getDescription() : existingProduct.getDescription())
                .imgUrl(productRequest.getImgUrl() != null ? productRequest.getImgUrl() : existingProduct.getImgUrl())
                .category(productRequest.getCategoryId() != null ? category: existingProduct.getCategory())
                .build();

        productRepository.save(product);

        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getImgUrl(),
                product.getCategory().getId());
    }

    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NoSuchElementException("Product not found with " + productId);
        }

        productRepository.deleteById(productId);
    }

}