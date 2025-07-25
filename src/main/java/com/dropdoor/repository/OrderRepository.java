package com.dropdoor.repository;

import com.dropdoor.model.Order;
import com.dropdoor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    
    List<Order> findByCustomer_Id(Long customerId);
    
    List<Order> findBySupplier(User supplier);
    
    List<Order> findBySupplier_Id(Long supplierId);
}