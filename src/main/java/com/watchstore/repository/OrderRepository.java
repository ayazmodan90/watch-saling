package com.watchstore.repository;

import com.watchstore.model.Order;
import com.watchstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findByCustomerOrderByOrderedAtDesc(User customer);
    List<Order> findByOrderStatus(Order.OrderStatus status);
    List<Order> findByPaymentStatus(Order.PaymentStatus status);
    List<Order> findAllByOrderByOrderedAtDesc();
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    BigDecimal getTotalRevenue();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'PENDING'")
    long countPendingOrders();
}
