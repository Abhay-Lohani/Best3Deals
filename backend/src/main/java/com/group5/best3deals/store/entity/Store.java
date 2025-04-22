package com.group5.best3deals.store.entity;

import com.group5.best3deals.location.entity.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @JoinColumn(name = "brand_id")
    private Long brandId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return id != null && id.equals(store.id); // Only compare by ID
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // Consistent hash code for all instances
    }
}
