package com.watchstore.service;

import com.watchstore.model.Product;
import com.watchstore.model.User;
import com.watchstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByStatus(Product.ProductStatus.ACTIVE);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getProductsBySeller(User seller) {
        return productRepository.findBySeller(seller);
    }

    public Product saveProduct(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void updateProductStatus(Long id, Product.ProductStatus status) {
        productRepository.findById(id).ifPresent(p -> {
            p.setStatus(status);
            p.setUpdatedAt(LocalDateTime.now());
            productRepository.save(p);
        });
    }

    public long getTotalProducts() {
        return productRepository.count();
    }

    public long getOutOfStockCount() {
        return productRepository.countOutOfStock();
    }
}
