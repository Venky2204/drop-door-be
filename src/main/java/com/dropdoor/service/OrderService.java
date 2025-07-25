package com.dropdoor.service;

import java.util.List;

import com.dropdoor.dto.request.OrderRequest;
import com.dropdoor.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    
    OrderResponse getOrderById(Long id);
    
    List<OrderResponse> getCurrentCustomerOrders();
    
    List<OrderResponse> getCurrentSupplierOrders();
    
    List<OrderResponse> getAllOrders();
    
    OrderResponse updateOrderStatus(Long id, String status);
    
    void cancelOrder(Long id);
    
    boolean canAccessOrder(Long orderId);
    
    boolean isOrderSupplier(Long orderId);
    
    boolean isOrderCustomer(Long orderId);
    
    boolean isOrderCancellable(Long orderId);
    
    
}