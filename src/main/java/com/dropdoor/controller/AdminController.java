package com.dropdoor.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dropdoor.dto.response.ApiResponse;
import com.dropdoor.dto.response.OrderResponse;
import com.dropdoor.dto.response.UserResponse;
import com.dropdoor.model.User;
import com.dropdoor.service.OrderService;
import com.dropdoor.service.UserService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/suppliers")
    public ResponseEntity<List<UserResponse>> getAllSuppliers() {
        List<User> suppliers = userService.getAllSuppliers();
        List<UserResponse> supplierResponses = suppliers.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(supplierResponses);
    }
    
    @GetMapping("/customers")
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        List<User> customers = userService.getAllCustomers();
        List<UserResponse> customerResponses = customers.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customerResponses);
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User activated successfully"));
    }
    
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deactivated successfully"));
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        // Implement dashboard statistics logic
        return ResponseEntity.ok(userService.getDashboardStats());
    }
    
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().getName().name());
        response.setActive(user.isActive());
        
        if (user.getAddress() != null) {
            response.setStreet(user.getAddress().getStreet());
            response.setCity(user.getAddress().getCity());
            response.setState(user.getAddress().getState());
            response.setZipCode(user.getAddress().getZipCode());
            response.setLandmark(user.getAddress().getLandmark());
        }
        
        return response;
    }
}