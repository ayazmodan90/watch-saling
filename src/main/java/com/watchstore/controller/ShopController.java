package com.watchstore.controller;

import com.watchstore.model.CartItem;
import com.watchstore.model.Product;
import com.watchstore.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ShopController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("products", productService.getActiveProducts().stream().limit(8).toList());
        addCartCount(model, session);
        return "shop/home";
    }

    @GetMapping("/shop")
    public String shop(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String search,
                       Model model, HttpSession session) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("products", productService.searchProducts(search));
        } else if (category != null && !category.isEmpty()) {
            model.addAttribute("products", productService.getProductsByCategory(category));
        } else {
            model.addAttribute("products", productService.getActiveProducts());
        }
        addCartCount(model, session);
        return "shop/shop";
    }

    @GetMapping("/shop/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts",
            productService.getProductsByCategory(product.getCategory())
                .stream().filter(p -> !p.getId().equals(id)).limit(4).toList());
        addCartCount(model, session);
        return "shop/product-detail";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/admin/dashboard";
    }

    @SuppressWarnings("unchecked")
    private void addCartCount(Model model, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        int count = (cart == null) ? 0 : cart.stream().mapToInt(CartItem::getQuantity).sum();
        model.addAttribute("cartCount", count);
        model.addAttribute("loggedUser", session.getAttribute("loggedUser"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userRole", session.getAttribute("userRole"));
    }
}
