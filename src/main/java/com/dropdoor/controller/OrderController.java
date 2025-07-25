package com.dropdoor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dropdoor.dto.request.OrderRequest;
import com.dropdoor.dto.request.OrderStatusUpdateRequest;
import com.dropdoor.dto.response.ApiResponse;
import com.dropdoor.dto.response.OrderResponse;
import com.dropdoor.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse order = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.canAccessOrder(#id)")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> getCurrentCustomerOrders() {
        List<OrderResponse> orders = orderService.getCurrentCustomerOrders();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/supplier")
    @PreAuthorize("hasRole('SUPPLIER')")
    public ResponseEntity<List<OrderResponse>> getCurrentSupplierOrders() {
        List<OrderResponse> orders = orderService.getCurrentSupplierOrders();
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SUPPLIER') and @orderService.isOrderSupplier(#id))")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, 
                                                        @Valid @RequestBody OrderStatusUpdateRequest statusRequest) {
        OrderResponse order = orderService.updateOrderStatus(id, statusRequest.getStatus());
        return ResponseEntity.ok(order);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @orderService.isOrderCustomer(#id) and @orderService.isOrderCancellable(#id))")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(new ApiResponse(true, "Order cancelled successfully"));
    }
}