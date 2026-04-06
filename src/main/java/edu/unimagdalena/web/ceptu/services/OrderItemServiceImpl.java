package edu.unimagdalena.web.ceptu.services;

import edu.unimagdalena.web.ceptu.dto.response.OrderItemResponse;
import edu.unimagdalena.web.ceptu.entities.OrderItem;
import edu.unimagdalena.web.ceptu.mappers.OrderItemMapper;
import edu.unimagdalena.web.ceptu.repositories.OrderItemRepository;
import edu.unimagdalena.web.ceptu.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponse getOrderItemById(UUID id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ítem de orden no encontrado con ID: " + id));
        return orderItemMapper.toResponse(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getItemsByOrderId(UUID orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        
        return items.stream()
                .map(orderItemMapper::toResponse)
                .collect(Collectors.toList());
    }
}