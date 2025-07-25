package com.dropdoor.config;

import com.dropdoor.model.Address;
import com.dropdoor.model.Role;
import com.dropdoor.model.User;
import com.dropdoor.repository.RoleRepository;
import com.dropdoor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize roles
        initRoles();
        
        // Initialize admin account if not exists
        createAdminIfNotExists();
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName(Role.RoleName.ROLE_ADMIN);
            
            Role supplierRole = new Role();
            supplierRole.setName(Role.RoleName.ROLE_SUPPLIER);
            
            Role customerRole = new Role();
            customerRole.setName(Role.RoleName.ROLE_CUSTOMER);
            
            roleRepository.saveAll(Arrays.asList(adminRole, supplierRole, customerRole));
        }
    }
    
    private void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("admin@dropdoor.com")) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@dropdoor.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("1234567890");
            
            Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin Role not found."));
            admin.setRole(adminRole);
            
            Address address = new Address();
            address.setStreet("Admin Street");
            address.setCity("Admin City");
            address.setState("Admin State");
            address.setZipCode("12345");
            admin.setAddress(address);
            
            userRepository.save(admin);
        }
    }
}