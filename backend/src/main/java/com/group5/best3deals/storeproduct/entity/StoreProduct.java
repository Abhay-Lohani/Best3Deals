package com.group5.best3deals.storeproduct.entity;

import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class StoreProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private Double price;
    private Double previousPrice;
    private Integer quantityInStock;
    private String productUrl;
    private LocalDateTime dateAdded;
    private LocalDateTime dateModified;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreProduct that = (StoreProduct) o;
        return Objects.equals(id, that.id) &&
                (product == null ? that.product == null :
                        product.getId() != null && product.getId().equals(that.product != null ? that.product.getId() : null)) &&
                (store == null ? that.store == null :
                        store.getId() != null && store.getId().equals(that.store != null ? that.store.getId() : null)) &&
                (brand == null ? that.brand == null :
                        brand.getId() != null && brand.getId().equals(that.brand != null ? that.brand.getId() : null)) &&
                Objects.equals(price, that.price) &&
                Objects.equals(previousPrice, that.previousPrice) &&
                Objects.equals(quantityInStock, that.quantityInStock) &&
                Objects.equals(productUrl, that.productUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                product != null ? product.getId() : null,
                store != null ? store.getId() : null,
                brand != null ? brand.getId() : null,
                price, previousPrice, quantityInStock, productUrl);
    }
}