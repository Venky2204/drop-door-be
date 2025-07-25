package com.dropdoor.repository;

import com.dropdoor.model.Product;
import com.dropdoor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySupplier(User supplier);
    
    List<Product> findBySupplier_Id(Long supplierId);
}