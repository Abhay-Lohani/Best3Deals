package com.group5.best3deals.unit.wishlist;

import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.location.entity.Location;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.product.service.ProductService;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.wishlist.dto.request.WishlistRequest;
import com.group5.best3deals.wishlist.dto.response.WishlistResponse;
import com.group5.best3deals.wishlist.entity.Wishlist;
import com.group5.best3deals.wishlist.repository.WishlistRepository;
import com.group5.best3deals.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private WishlistService wishlistService;

    private Wishlist wishlist;

    private WishlistRequest wishlistReq;

    private Product product;
    private StoreProduct storeProduct;
    private Store store;
    private Location location;
    private Brand brand;
    @BeforeEach
    void setUp() {
        wishlist = Wishlist.builder().id(1L).userId(1L).storeProductId(1L).build();
        wishlistReq = new WishlistRequest(1L);
        product = Product.builder().id(1L).name("name").description("desc").build();
        location = Location.builder().latitude(34.0993).longitude(-9.093).timestamp(new Date()).build();
        store = Store.builder().id(1L).address("address").name("storename").brandId(1L).location(location).build();
        brand = Brand.builder().id(1L).name("brand").imageUrl("brandImgUrl").build();
        storeProduct = StoreProduct.builder().productUrl("").product(product).store(store).brand(brand)
                .price(9.99).quantityInStock(8).previousPrice(10.99).dateAdded(LocalDateTime.now()).build();
    }

    // Test to fetch all items from wishlist
    @Test
    public void testGetWishlist() {
        when(wishlistRepository.findAllByUserId(1L)).thenReturn(List.of(wishlist));

        List<Wishlist> foundWishlist = wishlistService.getAllWishlistByUserId(1L);

        assertNotNull(foundWishlist);
        assertEquals(1, foundWishlist.size());
        assertEquals(wishlist, foundWishlist.get(0));
    }

    // Test to an item by user id and product id
    @Test
    public void testGetWishlistByUserIdAndProductId() {
        when(wishlistRepository.findByUserIdAndStoreProductId(1L, 1L)).thenReturn(Optional.of(wishlist));

        Wishlist foundWishlist = wishlistService.getWishlistByUserIdAndProductId(1L, 1L);

        assertNotNull(foundWishlist);
        assertEquals(wishlist, foundWishlist);
    }

    // Test to add an items to wishlist
    @Test
    public void testAddToWishlist() {
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);
        when(productService.getProductById(anyLong())).thenReturn(product);

        WishlistResponse savedWishlist = wishlistService.addToWishlist(1L, wishlistReq);

        assertNotNull(savedWishlist);
        assertEquals(wishlist.getStoreProductId(), savedWishlist.getStoreProductId());
    }

    // Test to delete an item from wishlist
    @Test
    public void testDeleteFromWishlist() {
        when(wishlistRepository.findByUserIdAndStoreProductId(1L, 1L)).thenReturn(Optional.of(wishlist));
        doNothing().when(wishlistRepository).deleteById(1L);

        wishlistService.deleteFromWishlist(1L, 1L);

        verify(wishlistRepository, times(1)).deleteById(1L);
    }
}
