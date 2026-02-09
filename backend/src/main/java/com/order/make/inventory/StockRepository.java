package com.order.make.inventory;

import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByProductId(Long productId);

    List<Stock> findByProductIdAndLocationId(Long productId, Long locationId);

    /**
     * 비관적 락으로 재고를 조회합니다.
     * 다른 트랜잭션이 이 재고를 수정할 수 없도록 락을 겁니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.productId = :productId")
    List<Stock> findByProductIdWithLock(@Param("productId") Long productId);
}
