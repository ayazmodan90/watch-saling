package com.watchstore.controller;

import com.watchstore.model.*;
import com.watchstore.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private OrderService orderService;

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        return "ADMIN".equals(role);
    }

    private String requireAdmin(HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        return null;
    }

    // ===== DASHBOARD =====
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("totalProducts", productService.getTotalProducts());
        model.addAttribute("totalOrders", orderService.getTotalOrders());
        model.addAttribute("totalCustomers", userService.countCustomers());
        model.addAttribute("totalSellers", userService.countSellers());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        model.addAttribute("pendingOrders", orderService.getPendingOrders());
        model.addAttribute("outOfStock", productService.getOutOfStockCount());
        model.addAttribute("recentOrders", orderService.getAllOrders().stream().limit(5).toList());
        model.addAttribute("userName", session.getAttribute("userName"));
        return "admin/dashboard";
    }

    // ===== PRODUCTS =====
    @GetMapping("/products")
    public String products(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("product", new Product());
        model.addAttribute("sellers", userService.getUsersByRole(User.Role.SELLER));
        return "admin/product-form";
    }

    @PostMapping("/products/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product,
                             BindingResult result, Model model,
                             @RequestParam(value = "sellerId", required = false) Long sellerId,
                             RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        if (result.hasErrors()) {
            model.addAttribute("sellers", userService.getUsersByRole(User.Role.SELLER));
            return "admin/product-form";
        }
        if (sellerId != null) {
            userService.getUserById(sellerId).ifPresent(product::setSeller);
        }
        productService.saveProduct(product);
        ra.addFlashAttribute("success", "Product added successfully!");
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        model.addAttribute("sellers", userService.getUsersByRole(User.Role.SELLER));
        return "admin/product-form";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id,
                              @Valid @ModelAttribute("product") Product product,
                              BindingResult result, Model model,
                              @RequestParam(value = "sellerId", required = false) Long sellerId,
                              RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        if (result.hasErrors()) {
            model.addAttribute("sellers", userService.getUsersByRole(User.Role.SELLER));
            return "admin/product-form";
        }
        product.setId(id);
        if (sellerId != null) {
            userService.getUserById(sellerId).ifPresent(product::setSeller);
        }
        productService.saveProduct(product);
        ra.addFlashAttribute("success", "Product updated successfully!");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        productService.deleteProduct(id);
        ra.addFlashAttribute("success", "Product deleted!");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/status/{id}")
    public String updateProductStatus(@PathVariable Long id,
                                      @RequestParam String status, RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        productService.updateProductStatus(id, Product.ProductStatus.valueOf(status));
        ra.addFlashAttribute("success", "Product status updated!");
        return "redirect:/admin/products";
    }

    // ===== USERS =====
    @GetMapping("/users")
    public String users(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/status/{id}")
    public String updateUserStatus(@PathVariable Long id, @RequestParam String status,
                                   RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        userService.updateUserStatus(id, User.Status.valueOf(status));
        ra.addFlashAttribute("success", "User status updated!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/role/{id}")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role,
                                 RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        userService.updateUserRole(id, User.Role.valueOf(role));
        ra.addFlashAttribute("success", "User role updated!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted!");
        return "redirect:/admin/users";
    }

    // ===== SELLERS =====
    @GetMapping("/sellers")
    public String sellers(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("sellers", userService.getUsersByRole(User.Role.SELLER));
        return "admin/sellers";
    }

    // ===== ORDERS =====
    @GetMapping("/orders")
    public String orders(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        Order order = orderService.getOrderById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "admin/order-detail";
    }

    @PostMapping("/orders/status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status,
                                    RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        orderService.updateOrderStatus(id, Order.OrderStatus.valueOf(status));
        ra.addFlashAttribute("success", "Order status updated!");
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/payment/{id}")
    public String updatePaymentStatus(@PathVariable Long id, @RequestParam String paymentStatus,
                                      RedirectAttributes ra, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        orderService.updatePaymentStatus(id, Order.PaymentStatus.valueOf(paymentStatus));
        ra.addFlashAttribute("success", "Payment status updated!");
        return "redirect:/admin/orders";
    }

    // ===== PAYMENTS =====
    @GetMapping("/payments")
    public String payments(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/auth/login";
        model.addAttribute("paidOrders", orderService.getOrdersByPaymentStatus(Order.PaymentStatus.PAID));
        model.addAttribute("pendingOrders", orderService.getOrdersByPaymentStatus(Order.PaymentStatus.PENDING));
        model.addAttribute("failedOrders", orderService.getOrdersByPaymentStatus(Order.PaymentStatus.FAILED));
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        return "admin/payments";
    }
}
