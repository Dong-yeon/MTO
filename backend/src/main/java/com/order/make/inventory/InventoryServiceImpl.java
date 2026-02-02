package com.order.make.inventory;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final StockRepository stockRepository;

    @Override
    public int getAvailableQuantity(Long productId) {
        return stockRepository.findByProductId(productId).stream()
                .mapToInt(stock -> stock.getQuantity() - stock.getReservedQuantity())
                .sum();
    }
}
