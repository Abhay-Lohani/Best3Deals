package com.group5.best3deals.unit.category;

import com.group5.best3deals.category.entity.Category;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testNoArgsConstructor() {
        Category category = new Category();
        assertNull(category.getId());
        assertNull(category.getName());
    }

    @Test
    void testAllArgsConstructor() {
        Category category = new Category(1L, "Electronics");
        assertEquals(1L, category.getId());
        assertEquals("Electronics", category.getName());
    }

    @Test
    void testBuilder() {
        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        assertEquals(1L, category.getId());
        assertEquals("Electronics", category.getName());
    }

    @Test
    void testEquals_SameId() {
        Category category1 = Category.builder().id(1L).name("Electronics").build();
        Category category2 = Category.builder().id(1L).name("Different Name").build();

        assertEquals(category1, category2);
    }

    @Test
    void testEquals_DifferentId() {
        Category category1 = Category.builder().id(1L).build();
        Category category2 = Category.builder().id(2L).build();

        assertNotEquals(category1, category2);
    }

    @Test
    void testEquals_NullId() {
        Category category1 = Category.builder().id(1L).build();
        Category category2 = new Category();

        assertNotEquals(category1, category2);
    }

    @Test
    void testEquals_SameNullId() {
        Category category1 = new Category();
        Category category2 = new Category();

        // Two categories with null IDs should NOT be considered equal
        // because the equals() implementation only checks non-null IDs
        assertNotEquals(category1, category2);
    }

    @Test
    void testEquals_NotCategoryObject() {
        Category category = new Category();
        Object other = new Object();

        assertNotEquals(category, other);
    }

    @Test
    void testEquals_Null() {
        Category category = new Category();

        assertNotEquals(category, null);
    }

    @Test
    void testHashCode_Consistency() {
        Category category = new Category();
        int initialHashCode = category.hashCode();

        assertEquals(initialHashCode, category.hashCode());
    }

    @Test
    void testHashCode_AllInstancesSame() {
        Category category1 = new Category();
        Category category2 = Category.builder().id(1L).build();
        Category category3 = Category.builder().id(2L).name("Test").build();

        assertEquals(category1.hashCode(), category2.hashCode());
        assertEquals(category1.hashCode(), category3.hashCode());
    }

    @Test
    void testGetters() {
        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        assertEquals(1L, category.getId());
        assertEquals("Electronics", category.getName());
    }
}