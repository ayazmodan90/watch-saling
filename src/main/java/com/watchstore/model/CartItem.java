package com.watchstore.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem implements Serializable {
    private Long productId;
    private String name;
    private String brand;
    private String imageUrl;
    private BigDecimal price;
    private int quantity;

    public CartItem() {}

    public CartItem(Product product, int quantity) {
        this.productId = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.imageUrl = product.getImageUrl();
        this.price = product.getPrice();
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
