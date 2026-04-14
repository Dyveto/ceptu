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
import java.time.Instant;
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
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
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
                .newStatus(OrderStatus.CREATED.name())
                .notes("Orden creada exitosamente")
                .changedAt(Instant.now())
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

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("No se puede cancelar una orden ya entregada");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException("La orden ya se encuentra cancelada");
        }

        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.SHIPPED) {
            for (OrderItem item : order.getOrderItems()) {
                Inventory inventory = item.getProduct().getInventory();
                inventory.setAvailableStock(inventory.getAvailableStock() + item.getQuantity());
                inventory.setUpdatedAt(Instant.now());
                inventoryRepository.save(inventory);
            }
        }

        String previousStatus = order.getStatus().name();
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.CANCELLED.name())
                .notes(request.notes() != null ? request.notes() : "Cancelación de pedido")
                .changedAt(Instant.now())
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

        for (OrderItem item : order.getOrderItems()) {
            Inventory inventory = item.getProduct().getInventory();
            if (inventory.getAvailableStock() < item.getQuantity()) {
                throw new BusinessException("Stock insuficiente para procesar el pago del producto: " + item.getProduct().getName());
            }
        }

        for (OrderItem item : order.getOrderItems()) {
            Inventory inventory = item.getProduct().getInventory();
            inventory.setAvailableStock(inventory.getAvailableStock() - item.getQuantity());
            inventory.setUpdatedAt(Instant.now());
            inventoryRepository.save(inventory);
        }

        String previousStatus = order.getStatus().name();
        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(Instant.now());

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.PAID.name())
                .notes("Pago confirmado y stock descontado")
                .changedAt(Instant.now())
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

        String previousStatus = order.getStatus().name();
        order.setStatus(OrderStatus.SHIPPED);
        order.setUpdatedAt(Instant.now());

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.SHIPPED.name())
                .notes("Pedido despachado para entrega")
                .changedAt(Instant.now())
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

        String previousStatus = order.getStatus().name();
        order.setStatus(OrderStatus.DELIVERED);
        order.setUpdatedAt(Instant.now());

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus)
                .newStatus(OrderStatus.DELIVERED.name())
                .notes("Pedido entregado exitosamente al cliente")
                .changedAt(Instant.now())
                .build();
        order.getOrderStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }
}