package com.watchstore.controller;

import com.watchstore.model.CartItem;
import com.watchstore.model.Order;
import com.watchstore.model.OrderItem;
import com.watchstore.model.Product;
import com.watchstore.model.User;
import com.watchstore.service.OrderService;
import com.watchstore.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // ─── ADD TO CART ───────────────────────────────────────────
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes ra) {
        Product product = productService.getProductById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        List<CartItem> cart = getCart(session);
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(product, quantity));
        }
        session.setAttribute("cart", cart);
        ra.addFlashAttribute("cartSuccess", product.getName() + " added to your cart! 🛒");
        return "redirect:/shop/product/" + productId;
    }

    // ─── VIEW CART ─────────────────────────────────────────────
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        BigDecimal total = cart.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "shop/cart";
    }

    // ─── UPDATE QUANTITY ───────────────────────────────────────
    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long productId,
                             @RequestParam int quantity,
                             HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (quantity <= 0) {
            cart.removeIf(i -> i.getProductId().equals(productId));
        } else {
            for (CartItem item : cart) {
                if (item.getProductId().equals(productId)) {
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // ─── REMOVE FROM CART ──────────────────────────────────────
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(i -> i.getProductId().equals(productId));
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // ─── CHECKOUT PAGE ─────────────────────────────────────────
    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/auth/login?next=checkout";
        }
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        BigDecimal total = cart.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("user", user);
        return "shop/checkout";
    }

    // ─── PLACE ORDER ───────────────────────────────────────────
    @PostMapping("/checkout/place-order")
    public String placeOrder(@RequestParam String shippingAddress,
                             @RequestParam String paymentMethod,
                             HttpSession session,
                             RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/auth/login";
        }
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setCustomer(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus(Order.OrderStatus.PENDING);

        if (paymentMethod.equals("CASH")) {
            order.setPaymentStatus(Order.PaymentStatus.PENDING);
        } else {
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setTransactionId("TXN" + System.currentTimeMillis());
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (CartItem ci : cart) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getPrice());
            // set product reference
            Product p = new Product();
            p.setId(ci.getProductId());
            oi.setProduct(p);
            items.add(oi);
            total = total.add(ci.getSubtotal());
        }
        order.setTotalAmount(total);
        order.setOrderItems(items);
        Order saved = orderService.saveOrder(order);

        // Clear cart
        session.removeAttribute("cart");
        ra.addFlashAttribute("orderSuccess", true);
        ra.addFlashAttribute("orderId", saved.getId());
        ra.addFlashAttribute("paymentMethod", paymentMethod);
        return "redirect:/order-success";
    }

    @GetMapping("/order-success")
    public String orderSuccess() {
        return "shop/order-success";
    }


    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/auth/login?next=my-orders";
        }
        model.addAttribute("orders", orderService.getOrdersByCustomer(user));
        model.addAttribute("userName", session.getAttribute("userName"));
        return "shop/my-orders";
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}
