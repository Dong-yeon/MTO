package com.order.make.inventory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.make.inventory.dto.StockAllocationResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public int getAvailableQuantity(Long productId) {
        return stockRepository.findByProductId(productId).stream()
                .mapToInt(stock -> stock.getQuantity() - stock.getReservedQuantity())
                .sum();
    }

    /**
     * 재고를 원자적으로 할당합니다.
     * 조회-계산-차감을 한 트랜잭션 안에서 처리하여 동시성 문제를 방지합니다.
     */
    @Transactional
    public StockAllocationResult allocateStock(Long productId, int requestedQuantity) {
        // 1. 비관적 락으로 재고 조회 (다른 트랜잭션은 대기)
        List<Stock> stocks = stockRepository.findByProductIdWithLock(productId);

        // 2. 가용 재고 계산
        int totalAvailable = stocks.stream()
                .mapToInt(stock -> stock.getQuantity() - stock.getReservedQuantity())
                .sum();

        // 3. 할당 가능한 수량 계산
        int allocatedQuantity = Math.min(totalAvailable, requestedQuantity);
        int deficitQuantity = requestedQuantity - allocatedQuantity;

        // 4. 재고 차감 (실제 DB 업데이트)
        if (allocatedQuantity > 0) {
            allocateFromStocks(stocks, allocatedQuantity);
        }

        // 5. 결과 반환
        return StockAllocationResult.builder()
                .productId(productId)
                .requestedQuantity(requestedQuantity)
                .allocatedQuantity(allocatedQuantity)
                .deficitQuantity(deficitQuantity)
                .fullyAllocated(deficitQuantity == 0)
                .build();
    }

    /**
     * 여러 창고의 재고에서 순차적으로 차감합니다.
     */
    private void allocateFromStocks(List<Stock> stocks, int quantityToAllocate) {
        int remaining = quantityToAllocate;

        for (Stock stock : stocks) {
            if (remaining <= 0) {
                break;
            }

            int available = stock.getQuantity() - stock.getReservedQuantity();
            if (available <= 0) {
                continue;
            }

            int toAllocate = Math.min(available, remaining);

            // 예약 수량 증가 (실제 출하 전까지는 예약 상태)
            stock.setReservedQuantity(stock.getReservedQuantity() + toAllocate);
            stockRepository.save(stock);

            remaining -= toAllocate;
        }
    }
}
