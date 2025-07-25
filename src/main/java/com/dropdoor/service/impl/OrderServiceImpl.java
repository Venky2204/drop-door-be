package com.dropdoor.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dropdoor.dto.request.OrderRequest;
import com.dropdoor.dto.response.OrderResponse;
import com.dropdoor.exception.ResourceNotFoundException;
import com.dropdoor.model.Address;
import com.dropdoor.model.Order;
import com.dropdoor.model.OrderItem;
import com.dropdoor.model.Product;
import com.dropdoor.model.User;
import com.dropdoor.repository.OrderRepository;
import com.dropdoor.repository.ProductRepository;
import com.dropdoor.service.OrderService;
import com.dropdoor.service.UserService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        User customer = userService.getCurrentUser();
        
        Order order = new Order();
        order.setCustomer(customer);
        
        // Get the supplier from the first product
        Product firstProduct = productRepository.findById(orderRequest.getItems().get(0).getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", orderRequest.getItems().get(0).getProductId()));
        order.setSupplier(firstProduct.getSupplier());
        
        // Create order items
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()));
                    
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setPrice(product.getPrice());
                    
                    return orderItem;
                })
                .collect(Collectors.toList());
        
        order.setItems(orderItems);
        
        // Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setTotalAmount(totalAmount);
        
        // Set delivery address
        Address deliveryAddress = new Address();
        deliveryAddress.setStreet(orderRequest.getStreet());
        deliveryAddress.setCity(orderRequest.getCity());
        deliveryAddress.setState(orderRequest.getState());
        deliveryAddress.setZipCode(orderRequest.getZipCode());
        deliveryAddress.setLandmark(orderRequest.getLandmark());
        
        order.setDeliveryAddress(deliveryAddress);
        
        // Set status
        order.setStatus(Order.OrderStatus.PENDING);
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        return convertToOrderResponse(savedOrder);
    }
    
    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        return convertToOrderResponse(order);
    }
    
    @Override
    public List<OrderResponse> getCurrentCustomerOrders() {
        User customer = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByCustomer(customer);
        
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderResponse> getCurrentSupplierOrders() {
        User supplier = userService.getCurrentUser();
        List<Order> orders = orderRepository.findBySupplier(supplier);
        
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        order.setStatus(Order.OrderStatus.valueOf(status));
        
        Order updatedOrder = orderRepository.save(order);
        
        return convertToOrderResponse(updatedOrder);
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    
    @Override
    public boolean canAccessOrder(Long orderId) {
        User currentUser = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        return order.getCustomer().getId().equals(currentUser.getId()) || 
               order.getSupplier().getId().equals(currentUser.getId());
    }
    
    @Override
    public boolean isOrderSupplier(Long orderId) {
        User currentUser = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        return order.getSupplier().getId().equals(currentUser.getId());
    }
    
    @Override
    public boolean isOrderCustomer(Long orderId) {
        User currentUser = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        return order.getCustomer().getId().equals(currentUser.getId());
    }
    
    @Override
    public boolean isOrderCancellable(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        return order.getStatus() == Order.OrderStatus.PENDING;
    }
    
    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getName());
        response.setSupplierId(order.getSupplier().getId());
        response.setSupplierName(order.getSupplier().getName());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        if (order.getDeliveryAddress() != null) {
            response.setStreet(order.getDeliveryAddress().getStreet());
            response.setCity(order.getDeliveryAddress().getCity());
            response.setState(order.getDeliveryAddress().getState());
            response.setZipCode(order.getDeliveryAddress().getZipCode());
            response.setLandmark(order.getDeliveryAddress().getLandmark());
        }
        
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setProductId(item.getProduct().getId());
                    itemResponse.setProductName(item.getProduct().getName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPrice(item.getPrice());
                    return itemResponse;
                })
                .collect(Collectors.toList());
        
        response.setItems(itemResponses);
        
        return response;
    }
}