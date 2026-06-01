package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.request.CancelOrderRequest;
import edu.unimagdalena.web.ceptu.dto.request.CreateOrderItemRequest;
import edu.unimagdalena.web.ceptu.dto.request.CreateOrderRequest;
import edu.unimagdalena.web.ceptu.dto.response.OrderResponse;
import edu.unimagdalena.web.ceptu.entities.*;
import edu.unimagdalena.web.ceptu.entities.enums.CustomerStatus;
import edu.unimagdalena.web.ceptu.entities.enums.OrderStatus;
import edu.unimagdalena.web.ceptu.exception.BusinessException;
import edu.unimagdalena.web.ceptu.exception.ConflictException;
import edu.unimagdalena.web.ceptu.exception.ResourceNotFoundException;
import edu.unimagdalena.web.ceptu.mappers.OrderMapper;
import edu.unimagdalena.web.ceptu.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new ConflictException("El cliente debe estar activo para realizar pedidos");
        }

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));

        if (!address.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("La dirección no pertenece al cliente especificado");
        }

        Order order = Order.builder()
                .customer(customer)
                .address(address)
                .status(OrderStatus.CREATED)
                .total(BigDecimal.ZERO)
                .orderItems(new ArrayList<>())
                .orderStatusHistories(new ArrayList<>())
                .build();

        BigDecimal grandTotal = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + itemReq.productId()));

            if (!product.isActive()) {
                throw new BusinessException("El producto " + product.getName() + " está inactivo.");
            }

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para el producto: " + product.getName()));

            if (inventory.getAvailableStock() < itemReq.quantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            inventory.setAvailableStock(inventory.getAvailableStock() - itemReq.quantity());
            inventoryRepository.save(inventory);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            grandTotal = grandTotal.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.quantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            order.getOrderItems().add(orderItem);
        }

        order.setTotal(grandTotal);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(null)
                .newStatus(OrderStatus.CREATED)
                .notes("Orden creada exitosamente")
                .build();
        order.getOrderStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(UUID customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID id, CancelOrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("No se puede cancelar un pedido que ya está en estado: " + order.getStatus());
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException("La orden ya se encuentra cancelada");
        }

        // Al cancelar, devolvemos el stock reservado al inventario general de la app
        for (OrderItem item : order.getOrderItems()) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado"));
            inventory.setAvailableStock(inventory.getAvailableStock() + item.getQuantity());
            inventoryRepository.save(inventory);
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.CANCELLED)
                .notes(request.notes() != null ? request.notes() : "Cancelación de pedido")
                .build();
        order.getOrderStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse payOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("Solo los pedidos en estado CREATED pueden ser pagados. Estado actual: " + order.getStatus());
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.PAID);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.PAID)
                .notes("Pago confirmado con éxito")
                .build();
        order.getOrderStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse shipOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("Solo los pedidos pagados (PAID) pueden ser enviados. Estado actual: " + order.getStatus());
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.SHIPPED);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.SHIPPED)
                .notes("Pedido despachado para entrega")
                .build();
        order.getOrderStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse deliverOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("Solo los pedidos enviados (SHIPPED) pueden marcarse como entregados. Estado actual: " + order.getStatus());
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.DELIVERED);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.DELIVERED)
                .notes("Pedido entregado exitosamente al cliente")
                .build();
        order.getOrderStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }
}