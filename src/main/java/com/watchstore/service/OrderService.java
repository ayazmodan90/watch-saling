package com.watchstore.service;

import com.watchstore.model.Order;
import com.watchstore.model.User;
import com.watchstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderedAtDesc();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByCustomer(User customer) {
        return orderRepository.findByCustomerOrderByOrderedAtDesc(customer);
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    public List<Order> getOrdersByPaymentStatus(Order.PaymentStatus status) {
        return orderRepository.findByPaymentStatus(status);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void updateOrderStatus(Long id, Order.OrderStatus status) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setOrderStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        });
    }

    public void updatePaymentStatus(Long id, Order.PaymentStatus status) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setPaymentStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        });
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public long getTotalOrders() {
        return orderRepository.count();
    }

    public long getPendingOrders() {
        return orderRepository.countPendingOrders();
    }
}
