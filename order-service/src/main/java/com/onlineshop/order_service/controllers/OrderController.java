package com.onlineshop.order_service.controllers;

import com.onlineshop.order_service.clients.UserClient;
import com.onlineshop.order_service.entities.Order;
import com.onlineshop.order_service.repositories.OrderRepository;
import com.onlineshop.order_service.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderService orderService;

    @GetMapping("/test")
    public String test() {
        return "order-test";
    }

    @GetMapping("/test-user")
    public String getFromUser() {
        return userClient.getUserTest();
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.placeOrder(order));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable("id") Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
