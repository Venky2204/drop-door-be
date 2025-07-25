package com.dropdoor.service;

import java.util.List;

import com.dropdoor.dto.request.ProductRequest;
import com.dropdoor.dto.response.ProductResponse;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    
    ProductResponse getProductById(Long id);
    
    ProductResponse createProduct(ProductRequest productRequest);
    
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    
    void deleteProduct(Long id);
    
    List<ProductResponse> getCurrentSupplierProducts();
    
    List<ProductResponse> getProductsBySupplier(Long supplierId);
    
    boolean isProductOwner(Long productId);
}