package com.dropdoor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dropdoor.dto.request.LoginRequest;
import com.dropdoor.dto.request.RegisterRequest;
import com.dropdoor.dto.response.JwtAuthResponse;
import com.dropdoor.exception.AppException;
import com.dropdoor.model.Address;
import com.dropdoor.model.Role;
import com.dropdoor.model.User;
import com.dropdoor.repository.RoleRepository;
import com.dropdoor.repository.UserRepository;
import com.dropdoor.security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public JwtAuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AppException("User not found"));

        return new JwtAuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getName().name()
        );
    }

    public User registerUser(RegisterRequest registerRequest) {
        // Create user object
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());

        // Set user role
        Role.RoleName roleName;
        if (registerRequest.getRole() != null) {
            switch (registerRequest.getRole().toLowerCase()) {
                case "admin":
                    roleName = Role.RoleName.ROLE_ADMIN;
                    break;
                case "supplier":
                    roleName = Role.RoleName.ROLE_SUPPLIER;
                    break;
                default:
                    roleName = Role.RoleName.ROLE_CUSTOMER;
            }
        } else {
            roleName = Role.RoleName.ROLE_CUSTOMER;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AppException("Role not found"));

        user.setRole(role);

        // Create and set address
        Address address = new Address();
        address.setCity(registerRequest.getAddress());
        address.setStreet(registerRequest.getAddress());
        address.setState("Andhra Pradesh");
        address.setZipCode("531116");
        // Set additional address fields if available
        user.setAddress(address);

        return userRepository.save(user);
    }
}