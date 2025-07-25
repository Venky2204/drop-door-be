package com.dropdoor.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dropdoor.dto.request.UserUpdateRequest;
import com.dropdoor.exception.ResourceNotFoundException;
import com.dropdoor.model.Role;
import com.dropdoor.model.User;
import com.dropdoor.repository.OrderRepository;
import com.dropdoor.repository.UserRepository;
import com.dropdoor.security.UserDetailsImpl;
import com.dropdoor.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public List<User> getAllSuppliers() {
        return userRepository.findByRole_Name(Role.RoleName.ROLE_SUPPLIER);
    }

    @Override
    public List<User> getAllCustomers() {
        return userRepository.findByRole_Name(Role.RoleName.ROLE_CUSTOMER);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", user.getId()));
        
        // Update fields
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        existingUser.setActive(user.isActive());
        
        // Update address if provided
        if (user.getAddress() != null) {
            existingUser.getAddress().setStreet(user.getAddress().getStreet());
            existingUser.getAddress().setCity(user.getAddress().getCity());
            existingUser.getAddress().setState(user.getAddress().getState());
            existingUser.getAddress().setZipCode(user.getAddress().getZipCode());
            existingUser.getAddress().setLandmark(user.getAddress().getLandmark());
        }
        
        return userRepository.save(existingUser);
    }
    
    @Override
    public User updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Update fields
        existingUser.setName(userUpdateRequest.getName());
        existingUser.setPhone(userUpdateRequest.getPhone());
        
        // Update address if provided
        if (existingUser.getAddress() != null) {
            existingUser.getAddress().setStreet(userUpdateRequest.getStreet());
            existingUser.getAddress().setCity(userUpdateRequest.getCity());
            existingUser.getAddress().setState(userUpdateRequest.getState());
            existingUser.getAddress().setZipCode(userUpdateRequest.getZipCode());
            existingUser.getAddress().setLandmark(userUpdateRequest.getLandmark());
        }
        
        return userRepository.save(existingUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }
    
    @Override
    public boolean isCurrentUser(Long userId) {
        User currentUser = getCurrentUser();
        return currentUser.getId().equals(userId);
    }
    
    // Implement the missing methods
    @Override
    public void activateUser(Long id) {
        User user = getUserById(id);
        user.setActive(true);
        userRepository.save(user);
    }
    
    @Override
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }
    
    @Override
    public Object getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count of users by role - use the new method name
        long supplierCount = userRepository.countByRoleName(Role.RoleName.ROLE_SUPPLIER);
        long customerCount = userRepository.countByRoleName(Role.RoleName.ROLE_CUSTOMER);
        
        stats.put("supplierCount", supplierCount);
        stats.put("customerCount", customerCount);
        
        // Count of orders
        long orderCount = orderRepository.count();
        stats.put("orderCount", orderCount);
        
        // Add more statistics as needed
        
        return stats;
    }
}