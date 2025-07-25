package com.dropdoor.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropdoor.dto.request.ProductRequest;
import com.dropdoor.dto.response.ProductResponse;
import com.dropdoor.exception.ResourceNotFoundException;
import com.dropdoor.model.Product;
import com.dropdoor.model.User;
import com.dropdoor.repository.ProductRepository;
import com.dropdoor.service.ProductService;
import com.dropdoor.service.UserService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return convertToProductResponse(product);
    }
    
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        User currentUser = userService.getCurrentUser();
        
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setSupplier(currentUser);
        
        Product savedProduct = productRepository.save(product);
        return convertToProductResponse(savedProduct);
    }
    
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Update product fields
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStockQuantity(productRequest.getStockQuantity());
        
        Product updatedProduct = productRepository.save(product);
        return convertToProductResponse(updatedProduct);
    }
    
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
    }
    
    @Override
    public List<ProductResponse> getCurrentSupplierProducts() {
        User currentUser = userService.getCurrentUser();
        List<Product> products = productRepository.findBySupplier(currentUser);
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponse> getProductsBySupplier(Long supplierId) {
        User supplier = userService.getUserById(supplierId);
        List<Product> products = productRepository.findBySupplier(supplier);
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isProductOwner(Long productId) {
        User currentUser = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        return product.getSupplier().getId().equals(currentUser.getId());
    }
    
    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setImageUrl(product.getImageUrl());
        response.setStockQuantity(product.getStockQuantity());
        response.setSupplierId(product.getSupplier().getId());
        response.setSupplierName(product.getSupplier().getName());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}