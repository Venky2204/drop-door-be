package com.dropdoor.service;

import java.util.List;

import com.dropdoor.dto.request.UserUpdateRequest;
import com.dropdoor.model.User;

public interface UserService {
    User getCurrentUser();
    
    User getUserById(Long id);
    
    List<User> getAllSuppliers();
    
    List<User> getAllCustomers();
    
    User updateUser(User user);
    
    User updateUser(Long id, UserUpdateRequest userUpdateRequest);
    
    void deleteUser(Long id);
    
    boolean isCurrentUser(Long userId);
    
    // Add these methods
    void activateUser(Long id);
    
    void deactivateUser(Long id);
    
    Object getDashboardStats();
}