package com.watchstore.repository;

import com.watchstore.model.Product;
import com.watchstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(Product.ProductStatus status);
    List<Product> findBySeller(User seller);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock = 0")
    long countOutOfStock();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'ACTIVE'")
    long countActive();
}
